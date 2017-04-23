package compiler;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import compiler.Scanner.Kind;
import compiler.Scanner.Token;
import compiler.AST.ASTVisitor;
import compiler.AST.AssignmentStatement;
import compiler.AST.BinaryChain;
import compiler.AST.BinaryExpression;
import compiler.AST.Block;
import compiler.AST.BooleanLitExpression;
import compiler.AST.ConstantExpression;
import compiler.AST.Dec;
import compiler.AST.Expression;
import compiler.AST.FilterOpChain;
import compiler.AST.FrameOpChain;
import compiler.AST.IdentChain;
import compiler.AST.IdentExpression;
import compiler.AST.IdentLValue;
import compiler.AST.IfStatement;
import compiler.AST.ImageOpChain;
import compiler.AST.IntLitExpression;
import compiler.AST.ParamDec;
import compiler.AST.Program;
import compiler.AST.SleepStatement;
import compiler.AST.Statement;
import compiler.AST.Tuple;
import compiler.AST.Type.TypeName;
import compiler.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	private int slot = 1; // TODO
	private int paramArgs = 0;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// cw = new ClassWriter(0);
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec paramdec : params)
			paramdec.visit(this, mv);
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, 1);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		// // TODO visit the local variables
		for (Dec dec : program.getB().getDecs()) {
			// Dec dec = (Dec) DecIt.next();
			mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, startRun, endRun,
					dec.getSlotNum());
		}

		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method

		cw.visitEnd();// end of class

		// generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Implement this
		TypeName t0 = binaryExpression.getE0().getTypeName();
		TypeName t1 = binaryExpression.getE1().getTypeName();
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		Token op = binaryExpression.getOp();
		if (op.getKind() == Kind.PLUS) {
			if (t0 == TypeName.INTEGER) {
				mv.visitInsn(IADD);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
			}
		}

		else if (op.getKind() == Kind.MINUS) {
			if (t0 == TypeName.INTEGER) {
				mv.visitInsn(ISUB);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
			}
		} else if (op.getKind() == Kind.TIMES) {
			if ((t0 == TypeName.INTEGER) && (t1 == TypeName.INTEGER)) {
				mv.visitInsn(IMUL);

			} else if ((t0 == TypeName.INTEGER) && (t1 == TypeName.IMAGE)) {
				mv.visitInsn(SWAP);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
			}
		}

		else if (op.getKind() == Kind.DIV) {
			if ((t0 == TypeName.INTEGER) && (t1 == TypeName.INTEGER)) {
				mv.visitInsn(IDIV);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
			}
		}

		else if (op.getKind() == Kind.MOD) {
			if ((t0 == TypeName.INTEGER) && (t1 == TypeName.INTEGER)) {
				mv.visitInsn(IREM);
			} else {
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
			}
		}

		else if (op.getKind() == Kind.OR) {
			Label orLabelStart = new Label();
			mv.visitInsn(IOR);
			Label orLabelEnd = new Label();
			mv.visitLabel(orLabelStart);
			mv.visitLabel(orLabelEnd);
		}

		else if (op.getKind() == Kind.AND) {
			Label andLabelStart = new Label();
			mv.visitInsn(IAND);
			Label andLabelEnd = new Label();
			mv.visitLabel(andLabelStart);
			mv.visitLabel(andLabelEnd);
		}

		else if (op.getKind() == Kind.LT) {
			Label ltLabelStart = new Label();
			mv.visitJumpInsn(IF_ICMPGE, ltLabelStart);
			mv.visitInsn(ICONST_1);
			Label ltLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, ltLabelEnd);
			mv.visitLabel(ltLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(ltLabelEnd);
		}

		else if (op.getKind() == Kind.LE) {
			Label leLabelStart = new Label();
			mv.visitJumpInsn(IF_ICMPGT, leLabelStart);
			mv.visitInsn(ICONST_1);
			Label leLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, leLabelEnd);
			mv.visitLabel(leLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(leLabelEnd);
		}

		else if (op.getKind() == Kind.GT) {
			Label gtLabelStart = new Label();
			mv.visitJumpInsn(IF_ICMPLE, gtLabelStart);
			mv.visitInsn(ICONST_1);
			Label gtLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, gtLabelEnd);
			mv.visitLabel(gtLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(gtLabelEnd);
		}

		else if (op.getKind() == Kind.GE) {
			Label geLabelStart = new Label();
			mv.visitJumpInsn(IF_ICMPLT, geLabelStart);
			mv.visitInsn(ICONST_1);
			Label geLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, geLabelEnd);
			mv.visitLabel(geLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(geLabelEnd);
		}

		else if (op.getKind() == Kind.EQUAL) {
			Label eqLabelStart = new Label();
			if (binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER)
					|| binaryExpression.getE0().getTypeName().equals(TypeName.BOOLEAN))
				mv.visitJumpInsn(IF_ICMPNE, eqLabelStart);
			else
				mv.visitJumpInsn(IF_ACMPNE, eqLabelStart);
			mv.visitInsn(ICONST_1);
			Label eqLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, eqLabelEnd);
			mv.visitLabel(eqLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(eqLabelEnd);
		}

		else if (op.getKind() == Kind.NOTEQUAL) {
			Label neqLabelStart = new Label();
			if (binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER)
					|| binaryExpression.getE0().getTypeName().equals(TypeName.BOOLEAN))
				mv.visitJumpInsn(IF_ICMPEQ, neqLabelStart);
			else
				mv.visitJumpInsn(IF_ACMPEQ, neqLabelStart);
			mv.visitInsn(ICONST_1);
			Label neqLabelEnd = new Label();
			mv.visitJumpInsn(GOTO, neqLabelEnd);
			mv.visitLabel(neqLabelStart);
			mv.visitInsn(ICONST_0);
			mv.visitLabel(neqLabelEnd);
		}

		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Implement this
		Label beginBlock = new Label();
		mv.visitLabel(beginBlock);
		Label endBlock = new Label();
		for (Dec dec : block.getDecs()) {
			dec.visit(this, arg);
		}
		for (Statement statement : block.getStatements()) {
			statement.visit(this, 0);
		}
		mv.visitLabel(endBlock);
		if (arg != null && (int) arg == 2) {
			// visit the local variables in the inner blocks
			for (Dec dec : block.getDecs()) {
				mv.visitLocalVariable(dec.getIdent().getText(), dec.getTypeName().getJVMTypeDesc(), null, beginBlock,
						endBlock, dec.getSlotNum());
			}
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Implement this
		if (booleanLitExpression.getValue()) {
			mv.visitInsn(ICONST_1);
		} else {
			mv.visitInsn(ICONST_0);
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		if (constantExpression.getFirstToken().kind.equals(Kind.KW_SCREENHEIGHT)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight",
					PLPRuntimeFrame.getScreenHeightSig, false);
		} else if (constantExpression.getFirstToken().kind.equals(Kind.KW_SCREENWIDTH)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth",
					PLPRuntimeFrame.getScreenWidthSig, false);
		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Implement this
		// Assign a slot in the local variable array to this variable and save
		// it in the new slot attribute in the Dec class. //0 is for this
		declaration.setSlotNum(slot++);
		// Initialize frame object to null
		if (declaration.getTypeName() == TypeName.FRAME) {
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlotNum());
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// assert false : "not yet implemented";
		if (filterOpChain.getFirstToken().kind.equals(Kind.OP_GRAY)) {
			if (((Token) arg).kind == Kind.BARARROW) {
				mv.visitInsn(DUP); // If bararrow, duplicate the source
			} else
				mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		} else if (filterOpChain.getFirstToken().kind.equals(Kind.OP_BLUR)) {
			if (((Token) arg).kind == Kind.BARARROW) {
				mv.visitInsn(DUP);
			} else
				mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);

		} else if (filterOpChain.getFirstToken().kind.equals(Kind.OP_CONVOLVE)) {
			if (((Token) arg).kind == Kind.BARARROW) {
				mv.visitInsn(DUP);
			} else
				mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig,
					false);
		}
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// assert false : "not yet implemented";
		// return null;
		imageOpChain.getArg().visit(this, arg);
		if (imageOpChain.getFirstToken().kind.equals(Kind.KW_SCALE)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		} else if (imageOpChain.getFirstToken().kind.equals(Kind.OP_HEIGHT)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight",
					PLPRuntimeImageOps.getHeightSig, false);
		} else if (imageOpChain.getFirstToken().kind.equals(Kind.OP_WIDTH)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth",
					PLPRuntimeImageOps.getWidthSig, false);
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.getArg().visit(this, arg);
		if (frameOpChain.getFirstToken().kind.equals(Kind.KW_SHOW)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc,
					false);
		} else if (frameOpChain.getFirstToken().kind.equals(Kind.KW_HIDE)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc,
					false);
		} else if (frameOpChain.getFirstToken().kind.equals(Kind.KW_MOVE)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc,
					false);
		} else if (frameOpChain.getFirstToken().kind.equals(Kind.KW_XLOC)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc,
					false);
		} else if (frameOpChain.getFirstToken().kind.equals(Kind.KW_YLOC)) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc,
					false);
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// assert false : "not yet implemented";
		Dec idDec = identChain.getDec();
		if (identChain.isLeftIdent()) {
			if (idDec instanceof ParamDec) {
				if (idDec.getTypeName() == TypeName.INTEGER) {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), "I");
				} else if (idDec.getTypeName() == TypeName.BOOLEAN) {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(), "Z");
				} else if (idDec.getTypeName() == TypeName.FILE) {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(),
							TypeName.FILE.getJVMTypeDesc());
				} else if (idDec.getTypeName() == TypeName.URL) {// TODO
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(),
							TypeName.URL.getJVMTypeDesc());
				}
			} else if (idDec.getTypeName() == TypeName.IMAGE || idDec.getTypeName() == TypeName.FRAME) {
				mv.visitVarInsn(ALOAD, idDec.getSlotNum());
			} else
				mv.visitVarInsn(ILOAD, idDec.getSlotNum());
		} else {
			if (idDec instanceof ParamDec) {
				if (idDec.getTypeName() == TypeName.FILE) {
					mv.visitFieldInsn(GETSTATIC, className, identChain.getFirstToken().getText(),
							TypeName.FILE.getJVMTypeDesc()); // getting file on
																// top of stack
					// mv.visitInsn(SWAP);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write",
							PLPRuntimeImageIO.writeImageDesc, false);
				}
			} else if (identChain.getTypeName() == TypeName.INTEGER) {
				mv.visitVarInsn(ISTORE, idDec.getSlotNum());
				mv.visitVarInsn(ILOAD, idDec.getSlotNum());
			} else if (identChain.getTypeName() == TypeName.IMAGE) {
				mv.visitVarInsn(ASTORE, idDec.getSlotNum());
				mv.visitVarInsn(ALOAD, idDec.getSlotNum());
			} else if (identChain.getTypeName() == TypeName.FRAME) {
				mv.visitVarInsn(ALOAD, idDec.getSlotNum());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame",
						PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitVarInsn(ASTORE, idDec.getSlotNum());
				mv.visitVarInsn(ALOAD, idDec.getSlotNum());
			}
			return idDec;
		}
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// assert false : "not yet implemented";
		// return null;
		// TODO

		Dec iDec = null;
		binaryChain.getE0().setLeftIdent(true);
		iDec = (Dec) binaryChain.getE0().visit(this, null);
		if (binaryChain.getE0().getTypeName().equals(TypeName.FILE)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile",
					PLPRuntimeImageIO.readFromFileDesc, false);
		} else if (binaryChain.getE0().getTypeName().equals(TypeName.URL)) {
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL",
					PLPRuntimeImageIO.readFromURLSig, false);
		}
		iDec = (Dec) binaryChain.getE1().visit(this, binaryChain.getArrow());
		if (arg != null && (int) arg == 0)
			mv.visitInsn(POP);
		return iDec;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Implement this
		// load value of variable (this could be a field or a local var)
		Dec idDec = identExpression.getDec();
		if (idDec instanceof ParamDec) {
			// mv.visitVarInsn(ALOAD, 0);// TODO for 'this'
			if (idDec.getTypeName() == TypeName.INTEGER) {
				mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(), "I");
			} else if (idDec.getTypeName() == TypeName.BOOLEAN) {
				mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(), "Z");
			} else if (idDec.getTypeName() == TypeName.FILE) {
				mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(),
						TypeName.FILE.getJVMTypeDesc());
			} else if (idDec.getTypeName() == TypeName.URL) {// TODO
				mv.visitFieldInsn(GETSTATIC, className, identExpression.getFirstToken().getText(),
						TypeName.URL.getJVMTypeDesc());
			}
		} else if (idDec.getTypeName() == TypeName.IMAGE || idDec.getTypeName() == TypeName.FRAME)
			mv.visitVarInsn(ALOAD, idDec.getSlotNum());
		else
			mv.visitVarInsn(ILOAD, idDec.getSlotNum());
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Implement this
		// store value on top of stack to this variable (which could be a field
		// or local var)
		Dec identDec = identX.getDec();
		if (identDec instanceof ParamDec) {
			if (identDec.getTypeName() == TypeName.INTEGER) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.INTEGER.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.BOOLEAN) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.BOOLEAN.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.IMAGE) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.IMAGE.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.FRAME) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.FRAME.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.FILE) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.FILE.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.URL) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.URL.getJVMTypeDesc());
			} else if (identDec.getTypeName() == TypeName.NONE) {
				mv.visitFieldInsn(PUTSTATIC, className, identX.getText(), TypeName.NONE.getJVMTypeDesc());
			}
		} else {
			if (identDec.getTypeName() == TypeName.INTEGER || identDec.getTypeName() == TypeName.BOOLEAN) {
				mv.visitVarInsn(ISTORE, identDec.getSlotNum());
			} else {
				if (identDec.getTypeName() == TypeName.IMAGE) { // changed
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage",
							PLPRuntimeImageOps.copyImageSig, false);
				}
				mv.visitVarInsn(ASTORE, identDec.getSlotNum());
			}
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Implement this
		Label after = new Label();
		ifStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFEQ, after);
		ifStatement.getB().visit(this, 2);
		mv.visitLabel(after);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Implement this
		// load constant
		mv.visitLdcInsn(intLitExpression.value);
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Implement this
		// For assignment 5, only needs to handle integers and booleans
		// instance variable in class, initialized with values from arg array
		TypeName paramDecType = paramDec.getTypeName();
		if (paramDecType == TypeName.INTEGER) {
			FieldVisitor fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), "I", null, null);
			fv.visitEnd();
		} else if (paramDecType == TypeName.BOOLEAN) {
			FieldVisitor fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), "Z", null, null);
			fv.visitEnd();
		} else if (paramDecType == TypeName.URL) {
			FieldVisitor fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), "Ljava/net/URL;", null, null);
			fv.visitEnd();
		} else if (paramDecType == TypeName.FILE) {
			FieldVisitor fv = cw.visitField(ACC_STATIC, paramDec.getIdent().getText(), "Ljava/io/File;", null, null);
			fv.visitEnd();
		}
		mv.visitVarInsn(ALOAD, 0); // this
		if (paramDecType == TypeName.INTEGER) {
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(paramArgs);
			paramArgs++;
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), "I");
		} else if (paramDecType == TypeName.BOOLEAN) {
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(paramArgs);
			paramArgs++;
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), "Z");
		} else if (paramDecType == TypeName.URL) {
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(paramArgs);
			paramArgs++;
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), "Ljava/net/URL;");
		} else if (paramDecType == TypeName.FILE) {
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1); // args
			mv.visitLdcInsn(paramArgs);
			paramArgs++;
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTSTATIC, className, paramDec.getIdent().getText(), "Ljava/io/File;");
		}
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// assert false : "not yet implemented";
		sleepStatement.getE().visit(this, null);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// assert false : "not yet implemented";
		for (Expression e : tuple.getExprList()) {
			e.visit(this, null);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Implement this
		Label guard = new Label();
		Label body = new Label();
		mv.visitJumpInsn(GOTO, guard);
		mv.visitLabel(body);
		whileStatement.getB().visit(this, 2);
		mv.visitLabel(guard);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, body);
		return null;
	}

}
