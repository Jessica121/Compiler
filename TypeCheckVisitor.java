package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Chain chain = binaryChain.getE0();
		Token op = binaryChain.getArrow();
		ChainElem chainElem = binaryChain.getE1();
		chain.visit(this, null);
		chainElem.visit(this, null);
		if (chain.typeName == TypeName.URL && op.isKind(Kind.ARROW) && chainElem.typeName == TypeName.IMAGE) {
			binaryChain.setTypeName(TypeName.IMAGE);
		} else if (chain.typeName == TypeName.FILE && op.isKind(Kind.ARROW) && chainElem.typeName == TypeName.IMAGE) {
			binaryChain.setTypeName(TypeName.IMAGE);
		} else if (chain.typeName == TypeName.FRAME && op.isKind(Kind.ARROW) && (chainElem instanceof FrameOpChain)
				&& (chainElem.getFirstToken().kind == Kind.KW_XLOC || chainElem.getFirstToken().kind == Kind.KW_YLOC)) {
			binaryChain.setTypeName(TypeName.INTEGER);
		} else if (chain.typeName == TypeName.FRAME && op.isKind(Kind.ARROW) && (chainElem instanceof FrameOpChain)
				&& (chainElem.getFirstToken().kind == Kind.KW_SHOW || chainElem.getFirstToken().kind == Kind.KW_HIDE
						|| chainElem.getFirstToken().kind == Kind.KW_MOVE)) {
			binaryChain.setTypeName(TypeName.FRAME);
		} else if (chain.typeName == TypeName.IMAGE && op.isKind(Kind.ARROW) && (chainElem instanceof ImageOpChain)
				&& (chainElem.getFirstToken().kind == Kind.OP_WIDTH
						|| chainElem.getFirstToken().kind == Kind.OP_HEIGHT)) {
			binaryChain.setTypeName(TypeName.INTEGER);
		} else if (chain.typeName == TypeName.IMAGE && op.isKind(Kind.ARROW) && chainElem.typeName == TypeName.FRAME) {
			binaryChain.setTypeName(TypeName.FRAME);
		} else if (chain.typeName == TypeName.IMAGE && op.isKind(Kind.ARROW) && chainElem.typeName == TypeName.FILE) {
			binaryChain.setTypeName(TypeName.NONE);
		} else if (chain.typeName == TypeName.IMAGE && (op.isKind(Kind.ARROW) || op.isKind(Kind.BARARROW))
				&& (chainElem instanceof FilterOpChain)
				&& (chainElem.getFirstToken().kind == Kind.OP_GRAY || chainElem.getFirstToken().kind == Kind.OP_BLUR
						|| chainElem.getFirstToken().kind == Kind.OP_CONVOLVE)) {
			binaryChain.setTypeName(TypeName.IMAGE);
		} else if (chain.typeName == TypeName.IMAGE && op.isKind(Kind.ARROW) && (chainElem instanceof ImageOpChain)
				&& chainElem.getFirstToken().kind == Kind.KW_SCALE) {
			binaryChain.setTypeName(TypeName.IMAGE);
		} else if (chain.typeName == TypeName.IMAGE && op.isKind(Kind.ARROW) && (chainElem instanceof IdentChain)) {
			binaryChain.setTypeName(TypeName.IMAGE);
		} else
			throw new TypeCheckException("Illegal type at binary chain");
		return binaryChain;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();
		Token op = binaryExpression.getOp();
		binaryExpression.getE0().visit(this, null);
		binaryExpression.getE1().visit(this, null);
		if (e0.typeName == TypeName.INTEGER && e1.typeName == TypeName.INTEGER
				&& (op.kind == Kind.PLUS || op.kind == Kind.MINUS || op.kind == Kind.TIMES || op.kind == Kind.DIV)) {
			binaryExpression.setTypeName(TypeName.INTEGER);
		} else if (e0.typeName == TypeName.IMAGE && e1.typeName == TypeName.IMAGE
				&& (op.kind == Kind.PLUS || op.kind == Kind.MINUS)) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		} else if (e0.typeName == TypeName.INTEGER && e1.typeName == TypeName.IMAGE && op.kind == Kind.TIMES) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		} else if (e0.typeName == TypeName.IMAGE && e1.typeName == TypeName.INTEGER && op.kind == Kind.TIMES) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		} else if (e0.typeName == TypeName.INTEGER && e1.typeName == TypeName.INTEGER
				&& (op.kind == Kind.LT || op.kind == Kind.GT || op.kind == Kind.LE || op.kind == Kind.GE)) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);

		} else if (e0.typeName == TypeName.BOOLEAN && e1.typeName == TypeName.BOOLEAN
				&& (op.kind == Kind.LT || op.kind == Kind.GT || op.kind == Kind.LE || op.kind == Kind.GE)) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);

		} else if ((op.kind == Kind.EQUAL || op.kind == Kind.NOTEQUAL)) {
			if (e0.typeName == e1.typeName)
				binaryExpression.setTypeName(TypeName.BOOLEAN);
			else throw new TypeCheckException("Illegal Binary Expression Type");
		} else
			throw new TypeCheckException("Illegal Binary Expression Type");
		return binaryExpression;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// symtab.enterScope() List<Dec> List<Statement> symtab.leaveScope()
		symtab.enterScope();
		for (Dec dec : block.getDecs()) {
			dec.visit(this, null);
		}
		for (Statement stmt : block.getStatements()) {
			stmt.visit(this, null);
		}
		symtab.leaveScope();
		return block;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// BooleanLitExpression.type BOOLEAN
		booleanLitExpression.setTypeName(TypeName.BOOLEAN);
		return booleanLitExpression;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		filterOpChain.getArg().visit(this, null);
		if (filterOpChain.getArg().getExprList().size() == 0)
			filterOpChain.setTypeName(TypeName.IMAGE);
		else
			throw new TypeCheckException("Illegal type in FrameOpChain");
		return filterOpChain;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		frameOpChain.getArg().visit(this, null);
		Token frameOp = frameOpChain.firstToken;
		if (frameOp.isKind(Kind.KW_SHOW) || frameOp.isKind(Kind.KW_HIDE)) {
			if (frameOpChain.getArg().getExprList().size() == 0)
				frameOpChain.setTypeName(TypeName.NONE);
			else throw new TypeCheckException("Illegal type in FrameOpChain");
		} else if (frameOp.isKind(Kind.KW_XLOC) || frameOp.isKind(Kind.KW_YLOC)) {
			if (frameOpChain.getArg().getExprList().size() == 0)
				frameOpChain.setTypeName(TypeName.INTEGER);
			else throw new TypeCheckException("Illegal type in FrameOpChain");
		} else if (frameOp.isKind(Kind.KW_MOVE)) {
			if (frameOpChain.getArg().getExprList().size() == 2)
				frameOpChain.setTypeName(TypeName.NONE);
			else throw new TypeCheckException("Illegal type in FrameOpChain");
		} else
			throw new TypeCheckException("Illegal type in FrameOpChain");
		return frameOpChain;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: ident has been declared and is visible in the current
		// scope
		// IdentChain.type ident.type
		// ident.type symtab.lookup(ident.getText()).getType()
		Dec dec = symtab.lookup(identChain.getFirstToken().getText());
		if (dec != null)
			identChain.setTypeName(dec.getTypeName());
		else {
			throw new TypeCheckException("Illegal type at visit ident chain");
		}
		return identChain;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: ident has been declared and is visible in the current
		// scope
		// IdentExpression.type ident.type
		// IdentExpression.dec Dec of ident

		Dec dec = symtab.lookup(identExpression.getFirstToken().getText());
		if (dec != null) {
			identExpression.setTypeName(dec.getTypeName());
			identExpression.setDec(dec);
		} else {
			throw new TypeCheckException("Illegal type in ident expression.");
		}

		return identExpression;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: Expression.type = Boolean
		ifStatement.getE().visit(this, null);
		if (ifStatement.getE().typeName != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected expression type boolean in ifStatement");
		}
		ifStatement.getB().visit(this, null);
		return ifStatement;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// IntLitExpression.type INTEGER
		intLitExpression.setTypeName(TypeName.INTEGER);
		return intLitExpression;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: Expression.type==INTEGER
		sleepStatement.getE().visit(this, null);
		if (sleepStatement.getE().getTypeName() != TypeName.INTEGER)
			throw new TypeCheckException("Illegal type in Sleep statement");
		return sleepStatement;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: Expression.type = Boolean
		whileStatement.getE().visit(this, null);
		if (whileStatement.getE().typeName != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected expression type boolean in whileStatement");
		}
		whileStatement.getB().visit(this, null);
		return whileStatement;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// symtab.insert(ident.getText(), Dec);
		declaration.setTypeName(null);
		if (!symtab.insert(declaration.getIdent().getText(), declaration))
			throw new TypeCheckException("Duplicate indent declaration");
		return declaration;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// List<ParamDec> Block
		for (ParamDec paramDec : program.getParams()) {
			paramDec.visit(this, null);
		}
		program.getB().visit(this, null);
		return program;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: IdentLValue.type== Expression.type
		IdentLValue identLVal = (IdentLValue) assignStatement.getVar().visit(this, null);
		Expression expr = (Expression) assignStatement.getE().visit(this, null);
		// identLVal.dec =
		// symtab.lookup(assignStatement.getVar().getFirstToken().getText());
		if (identLVal.getDec().getTypeName() != expr.getTypeName())
			throw new TypeCheckException("Illegal type in assignment statement");
		return assignStatement;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// condition: ident has been declared and is visible in the current
		// scope
		// IdentLValue.dec Dec of ident
		// Dec dec = identX.getDec();
		String identText = identX.getFirstToken().getText();
		Dec symTabDec = symtab.lookup(identText);
		if (symTabDec != null) {
			identX.setDec(symTabDec);
		} else
			throw new TypeCheckException("ident has not been declared or is not visible in the current scope");
		return identX;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		paramDec.setTypeName(null);
		if (!symtab.insert(paramDec.getIdent().getText(), paramDec)) {
			throw new TypeCheckException("Duplicate ident declaration");
		}
		return paramDec;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		// ConstantExpression.type INTEGER
		constantExpression.setTypeName(TypeName.INTEGER);
		return constantExpression;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		imageOpChain.getArg().visit(this, null);
		Kind imageOpKind = imageOpChain.firstToken.kind;
		if (imageOpKind == Kind.OP_WIDTH || imageOpKind == Kind.OP_HEIGHT) {
			if (imageOpChain.getArg().getExprList().size() == 0) {
				imageOpChain.setTypeName(TypeName.INTEGER);
			} else {
				throw new TypeCheckException("Illegal type in ImageOpChain");
			}
		} else if (imageOpKind == Kind.KW_SCALE) {
			if (imageOpChain.getArg().getExprList().size() == 1) {
				imageOpChain.setTypeName(TypeName.IMAGE);
			} else {
				throw new TypeCheckException("Illegal type in ImageOpChain");
			}
		} else {
			throw new TypeCheckException("Illegal type in ImageOpChain");
		}
		return imageOpChain;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		// for all expression in List<Expression>: Expression.type = INTEGER
		for (Expression e : tuple.getExprList()) {
			e.visit(this, null);
			if (e.typeName != TypeName.INTEGER) {
				throw new TypeCheckException("Expected type Integer in visitTuple");
			}
		}
		return tuple;
	}

}
