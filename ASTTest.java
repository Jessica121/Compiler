package compiler;

import static compiler.Scanner.Kind.ARROW;
import static compiler.Scanner.Kind.IDENT;
import static compiler.Scanner.Kind.KW_INTEGER;
import static compiler.Scanner.Kind.LBRACE;
import static compiler.Scanner.Kind.NOTEQUAL;
import static compiler.Scanner.Kind.OP_SLEEP;
import static compiler.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import compiler.Parser.SyntaxException;
import compiler.Scanner.IllegalCharException;
import compiler.Scanner.IllegalNumberException;
import compiler.AST.ASTNode;
import compiler.AST.AssignmentStatement;
import compiler.AST.BinaryChain;
import compiler.AST.BinaryExpression;
import compiler.AST.Block;
import compiler.AST.BooleanLitExpression;
import compiler.AST.ConstantExpression;
import compiler.AST.Dec;
import compiler.AST.FilterOpChain;
import compiler.AST.IdentExpression;
import compiler.AST.IfStatement;
import compiler.AST.IntLitExpression;
import compiler.AST.ParamDec;
import compiler.AST.Program;
import compiler.AST.SleepStatement;
import compiler.AST.Statement;
import compiler.AST.Tuple;
import compiler.AST.WhileStatement;

public class ASTTest {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testBinaryExpr1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(4 + 8) * 9";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
	}
	
	@Test
	public void testBinaryExpr2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "3 + 4 * 3";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(BinaryExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		//assertEquals(LPAREN,be.getE0().getFirstToken());
	}
	
	@Test
	public void testTuple() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(6 * 8, 2 * 8, (1 + 9))";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple tu = (Tuple) ast;
		assertEquals(3, tu.getExprList().size());
		assertEquals(BinaryExpression.class, tu.getExprList().get(0).getClass());
		assertEquals(BinaryExpression.class, tu.getExprList().get(1).getClass());
		assertEquals(BinaryExpression.class, tu.getExprList().get(2).getClass());
	}
	

	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true){integer blue sleep\n true-false!=k;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement ifState = (IfStatement) ast;
		assertEquals("blue", ifState.getB().getDecs().get(0).getIdent().getText());
		assertEquals(SleepStatement.class, ifState.getB().getStatements().get(0).getClass());
		assertEquals("if", ifState.getFirstToken().getText());
		assertEquals(BooleanLitExpression.class,ifState.getE().getClass());
	}
	
	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true){integer blue sleep\n true-false!=k;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement whileState = (WhileStatement) ast;
		assertEquals("blue", whileState.getB().getDecs().get(0).getIdent().getText());
		assertEquals(SleepStatement.class, whileState.getB().getStatements().get(0).getClass());
		assertEquals("while", whileState.getFirstToken().getText());
		assertEquals(BooleanLitExpression.class,whileState.getE().getClass());
	}
	
	@Test
	public void testAssignmentStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{a <- k ; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		Block block = (Block) ast;
		assertEquals(Block.class, ast.getClass());
		assertEquals(AssignmentStatement.class,block.getStatements().get(0).getClass());
		AssignmentStatement assgnStmt = (AssignmentStatement) block.getStatements().get(0);
		assertEquals(IdentExpression.class,assgnStmt.getE().getClass());	
	}
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "integer k_1578";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		ParamDec paramDec = (ParamDec) ast;
		assertEquals(ParamDec.class, ast.getClass());
		assertEquals(IDENT,paramDec.getIdent().kind);
	}
	
	@Test
	public void testChainStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve  (1,4)->convolve;";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		BinaryChain chainState = (BinaryChain) ast;
		assertEquals(BinaryChain.class, ast.getClass());
		assertEquals(FilterOpChain.class,chainState.getE0().getClass());
		assertEquals(ARROW, chainState.getArrow().kind);
		assertEquals(FilterOpChain.class,chainState.getE1().getClass());	
	}
	
	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file test";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		ParamDec paramdec = (ParamDec) ast;
		assertEquals(ParamDec.class, ast.getClass());
		assertEquals("file", paramdec.getFirstToken().getText());
		assertEquals("test",paramdec.getIdent().getText());
		
	}
	
	@Test
	public void testIfStatemet1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "_$ {if(false){}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.program();
		Program programParser = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(IDENT, programParser.getFirstToken().kind);
		assertEquals(IfStatement.class,programParser.getB().getStatements().get(0).getClass());
	}
	
	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "program {}";
		Parser parser = new Parser(new Scanner(input).scan());
		ASTNode ast = parser.parse();
		Program programParser = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(IDENT, programParser.getFirstToken().kind);
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(k) {x<-y;}}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		Block blockVal = (Block) ast;
		assertEquals(Block.class, ast.getClass());
		assertEquals(LBRACE, blockVal.firstToken.kind);
		IfStatement ifStmt = (IfStatement) blockVal.getStatements().get(0);
		assertEquals(IdentExpression.class, ifStmt.getE().getClass());
		assertEquals(Block.class, ifStmt.getB().getClass());
		Block innerBlock = (Block) ifStmt.getB();
		assertEquals(AssignmentStatement.class, innerBlock.getStatements().get(0).getClass());
	}
	
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "Test file abc, boolean xyz { integer x sleep screenwidth != screenheight;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//thrown.expect(Parser.SyntaxException.class);
		ASTNode ast = parser.program();
		Program p = (Program) ast;
		assertEquals(Program.class, ast.getClass());
		assertEquals(Block.class,p.getB().getClass());
		assertEquals(ParamDec.class, p.getParams().get(0).getClass());
		Block b = p.getB();
		assertEquals(Dec.class, b.getDecs().get(0).getClass());
		assertEquals(SleepStatement.class, b.getStatements().get(0).getClass());
		ArrayList<Dec> d = b.getDecs();
		ArrayList<Statement> s = b.getStatements();
		assertEquals(KW_INTEGER, d.get(0).firstToken.kind);
		assertEquals(IDENT, d.get(0).getIdent().kind);
		assertEquals(OP_SLEEP, s.get(0).firstToken.kind);
		BinaryExpression be = (BinaryExpression) ((SleepStatement)s.get(0)).getE();
		assertEquals(ConstantExpression.class,be.getE0().getClass());
		assertEquals(ConstantExpression.class,be.getE1().getClass());
		assertEquals(NOTEQUAL, be.getOp().kind);
	}
	

}
