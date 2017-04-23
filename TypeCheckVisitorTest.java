/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package compiler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import compiler.TypeCheckVisitor.TypeCheckException;
import compiler.AST.ASTNode;
import compiler.AST.Expression;

public class TypeCheckVisitorTest {
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testAssignmentBoolLit0() throws Exception{
		String input = "p {\nboolean y \ny <- false;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception{
		String input = "p {\nboolean y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}	
	
	public void test1() throws Exception{
		String input = "p {\ninteger y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	@Test
	public void test2() throws Exception{
		String input = "p {integer x sleep 12+x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	@Test
	public void test3() throws Exception{
		String input = "12+1";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		Expression e= parser.expression();
		//ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		e.visit(v, null);
		//AssertEquals("Expression",e.getClass());
		
	}
	@Test
	public void test4() throws Exception{
		String input = "p {\ninteger y\nboolean y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	@Test
	public void test5() throws Exception{
		String input = "p {\ninteger y\ninteger y}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);	
	}
	@Test
	public void test6() throws Exception{
		String input = "p {integer x sleep x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);		
	}
	@Test
	public void test7() throws Exception{
		String input = "p {\nboolean y \ny <- false+true;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test8() throws Exception{
		String input = "p {\ninteger y \ninteger x\ny <- y+x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test9() throws Exception{
		String input = "p {\ninteger y \ny <- y+true;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test10() throws Exception{
		String input = "p{}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test11() throws Exception{
		String input = "p{integer x}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test12() throws Exception{
		String input = "p{ while (false) {integer x} "
				+ "\n if (true) {x <- 3;}"
				+ "\n if (true) {}"
				+ "\n if (false) {}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test13() throws Exception{
		String input = "p{ while (false) {integer x} "
				+ "\n if (true) {}"
				+ "\n if (true) {}"
				+ "\n if (false) {x <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test14() throws Exception{
		String input = "p{integer x \n if (x) {integer y}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test15() throws Exception{
		String input = "p{ while (false) {integer x \n if (true) {x <- 3;}} "
				+ "\n if (true) {}"+
				"}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test16() throws Exception{
		String input = "p{ \n if (true) {y <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test17() throws Exception{
		String input = "p{integer x integer y \n y <- x+y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test18() throws Exception{
		String input = "p{integer x integer y \n y <- 12+x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test19() throws Exception{
		String input = "p{image x image y \n y <- y+x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test20() throws Exception{
		String input = "p{image x integer y \n y <- y+x;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test21() throws Exception{
		String input = "p{image x integer y \n  x <- x*y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test22() throws Exception{
		String input = "p{integer x integer y \n  x <- 3; "
				+ "\n y <- 4;"
				+ "if (x<y) {}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test23() throws Exception{
		String input = "p{image a integer x integer y \n"
				+ "a <- x==y; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test24() throws Exception{
		String input = "p{integer x \n if (x) {integer y}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test25() throws Exception{
		String input = "p{integer x integer y\n"
				+ "x -> y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test26() throws Exception{
		String input = "p{integer x frame x\n"
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test27() throws Exception{
		String input = "p{if(2<3) {"
				+ "integer x frame x}\n"
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test28() throws Exception{
		String input = "p{integer p \n}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test29() throws Exception{
		String input = "p url u ,file f {}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test30() throws Exception{
		String input = "p url u ,file f {integer u}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test31() throws Exception{
		String input = "p boolean u ,file f {integer u boolean f "
				+ "if(f) {u <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test32() throws Exception{
		String input = "p boolean u ,file f {integer u boolean f "
				+ "if(f) {u <- false;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test33() throws Exception{
		String input = "p boolean u ,file f {integer u boolean f "
				+ "if(f) {u <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test34() throws Exception{
		String input = "p boolean u ,file f {boolean f "
				+ "if(f) {u <- 3;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test35() throws Exception{
		String input = "p boolean u ,file f {boolean f "
				+ "if(f) {u <- false;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test36() throws Exception{
		String input = "p{integer y\n"
				+ "while(y!=3) "
				+ "{y <- 3;}"
				+ "}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test37() throws Exception{
		String input = "p url u{image i \n"
				+ " u->i;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test38() throws Exception{
		String input = "p file f{image i\n"
				+ "f-> i;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test39() throws Exception{
		String input = "p{frame f f -> xloc -> yloc;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test40() throws Exception{
		String input = "p{image x integer y \n  y <- x*y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		//ASTNode program = parser.parse();
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test41() throws Exception{
		String input = "p{image i frame f image i2 i2 -> i -> f;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test42() throws Exception{
		String input = "p file f{image i i -> f;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test43() throws Exception{
		String input = "p{image i i->blur;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test44() throws Exception{
		String input = "p{frame f f -> move;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test45() throws Exception{
		String input = "p file f{image i i->f;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test46() throws Exception{
		String input = "p file f{image i i->width;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test47() throws Exception{
		String input = "p{scale(10) -> scale(12);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test48() throws Exception{
		String input = "p{image i i->blur|->blur->blur;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test49() throws Exception{
		String input = "p{frame f f->width;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test50() throws Exception{
		String input = "p {image i\n"
				+ "i -> width;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		//System.out.println(program);
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
	@Test
	public void test51() throws Exception{
		String input = "parser "+
				"{"+
				"image i\n"+
				"i -> blur;\n"+
				"i |-> blur;\n"+
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		//System.out.println(program);
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);		
	}
                        
	                    
	@Test
	public void testSleepStatement() throws Exception{
		String input = "p {\ninteger y \nsleep y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testSleepStatementIntLit() throws Exception{
		String input = "p {\ninteger y \nsleep 10;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	@Test
	public void testSleepConstStmt() throws Exception{
		String input = "p {\ninteger y \nsleep screenheight;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	//TODO :  add test cases for Sleep to be throwing all exception types.

	@Test
	public void testSleepBinaryExpression() throws Exception{
		String input = "p {\ninteger y \nsleep y * 2 * 3 * y;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testWhileBooleanExpression() throws Exception{
		String input = "p {\nboolean y \nwhile(y) {} }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testIfBooleanExpression() throws Exception{
		String input = "p {\nboolean y \nif(2==3) {" +
				"if(2<3) {"+
				"boolean y " +
				"y <- true;"+
				"if (3>2){" +
				"if(3!=2){" +
				"if(2<=2){" +
				"if(2>=2){" +
						"}" +
				      "}" +
					"}" +
				  "}" +
				"}" +
				"}" +
				" }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testIdentChain() throws Exception{
		String input = "p url y { y -> scale (3);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCase() throws Exception{
		String input = "a file b {\nboolean y\nimage c\nimage d\ninteger z\ny <- true; "
				+ "sleep z;"
				+ "while(1>0){sleep screenwidth;} "
				+ "if(true) {integer y  y <- 10;} "
				+ "d -> c;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCase1() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer\n"
				+
				"{integer a boolean b image c frame d\n" +
				" while(true){\n" +
				"\n"+
				"boolean fal\n"+
				"sleep URL;\n" +
				"fal<-false;\n" +
				"}\n" +
				"if(screenheight<a)\n" +
				"{\n" +
				"}\n" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCase2() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep i;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCaseSleepWithFrameType() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep f;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCaseSleepWithBooleanType() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep b;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCaseSleepWithImageType() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep im;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCaseSleepWithURLType() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep File;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testComplicatedCaseSleepWithFileType() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep Boolean;\n" +
				"sleep 3;\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testWhile() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"sleep i;\n" +
				"while(b){\n"+
				"sleep 3;\n" +
				"}\n" +
				"while(b<=b){\n"+
				"sleep i;\n" +
				"}\n" +
				"while(2*i < 3*i){\n"+
				"sleep i;\n" +
				"}\n" +
				"while(2<3){\n"+
				"integer f"+
				" sleep i;\n" +
				"}\n" +
				"while(2<3){\n"+
				"integer f\n"+
				"sleep f;\n" +
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testWhile1() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"while(2<3){\n"+
				"integer f"+
				" sleep f;\n" +
				"}\n" +
				"while(2<3){\n"+
				//"sleep f;\n" +
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testWhile2() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"while(2<3){\n"+
				"integer f\n"+
				" sleep f;\n" +
				"}\n" +
				"while(2<3){\n"+
				"sleep f;\n" +
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssign1() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer i, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"integer f\n"+
				"frame f\n"+
				"f<-i;"+
				"f<-i*3;"+
				"f<-i*3;"+
				" sleep f;\n" +
				"}\n" +
				"if(2<3){\n"+
				"sleep f;\n" +
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
	    thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssign2() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"f<-i;"+
				"f<-i*3;"+
				"f<-i*3;"+
				" sleep f;\n" +
				"}\n" +
				"if(2<3){\n"+
				"sleep f;\n" +
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssign3() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"sleep i;\n" +
				"Integer <- true;\n"+
				"Integer <- 2<3;\n"+
				"Integer <- 2>3;\n"+
				"Integer <- 2<=3;\n"+
				"Integer <- 2>=3;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}
	@Test
	public void testAssign4() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"sleep i;\n" +
				"Integer <- true;\n"+
				"Integer <- 2<3;\n"+
				"Integer <- 2>3;\n"+
				"Integer <- 2<=3;\n"+
				"Integer <- 2>=3;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- fal;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssign5() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"sleep i;\n" +
				"Integer <- true;\n"+
				"Integer <- 2<3;\n"+
				"Integer <- 2>3;\n"+
				"Integer <- 2<=3;\n"+
				"Integer <- 2>=3;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer * 3;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testChain() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur;\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3,i/3);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray;\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testChain1() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur;\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3,i/3);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray;\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3,4);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testChain2() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur;\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray;\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testChain3() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur;\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3,i);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray (3);\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testChain4() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur(4,5);\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3,i);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray (3);\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testChain5() throws Exception{
		String input = "parser url File, file Boolean, integer URL, boolean Integer,integer f, boolean b,url u,file fl\n"
				+
				"{integer i boolean b image im frame f\n" +
				"if(2<3){\n"+
				"image im\n"+
				"im <- im * 3;\n"+
				"im <- im - im;"+
				"im <-im + im;"+
				" sleep i;\n" +
				"}\n" +
				"if(2<3){\n"+
				"frame fr\n"+
				"image im\n"+
				"im -> blur;\n"+
				"im |-> blur;\n"+
				"u -> im;\n"+
				"fl -> im;\n"+
				"fr -> xloc;\n"+
				"fr -> yloc;\n"+
				"fr -> show ;\n"+
				"fr -> hide;\n"+
				"fr -> move (2+3,i);\n"+
				"im -> fr;\n"+
				"im -> fl;\n"+
				"im |-> gray;\n"+
				"im |-> convolve;\n"+
				"im -> width;\n"+
				"im -> height;\n"+
				"im -> scale(3);\n"+
				"im -> im;\n"+
				"Integer <- 2==3;\n"+
				"Integer <- 2!=3;\n"+
				"Integer <- Integer;\n"+
				"}\n" +
				"integer URL\n"+
				//"URL -> i;"+
				"sleep screenwidth;\n" +
				"sleep screenheight;\n" +
				" sleep i*3/screenwidth-4+screenheight;" +
				"}\n";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		//thrown.expect(TypeCheckException.class);
		program.visit(v, null);
	}


}
