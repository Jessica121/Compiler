package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (2,9) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
		parser.arg();
	}
	
	public void testArg2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
		String input1 = "(something)anything";
		Parser parser1 = new Parser(new Scanner(input1).scan());
		parser1.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (1,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}


	@Test
	public void testIf() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " _$ {if(false){}} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}

	@Test
	public void testIfErr() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " if(6>  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.ifStatement();
	}

	@Test
	public void testBlockErr() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " k}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {k}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(){}}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}


	@Test
	public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(k true){}}  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testBlock4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(k)} true)  ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}

	@Test
	public void testBlock5() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {if(k){}   ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}


	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {a <- k ; } ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}


	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " integer k_1578 ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.paramDec();
	}

	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " frame k";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.dec();
	}


	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "true-false!=k";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.expression();
	}


	@Test
	public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.chainElem();
	}

	@Test
	public void testChainElemErr() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "hide  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.chainElem();
	}

	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "convolve  (1,4)->convolve  (8,5)->->->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.chain();
	}


	@Test
	public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "url blue";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.paramDec();
	}

	@Test
	public void testParamDecErr() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "file hide";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}

	

	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(true){integer blue sleep\n true-false!=k;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}
	@Test
	public void testWhileStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " {while(true){}} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.block();
	}


		
	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "false122314hldsf";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	@Test
	public void test123() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "__ {__->_|->$0|-> show (__/_%($$TAT$T_T%$)|true*screenwidth&$|_*$!=_==_>=(z_z),_&$|_$+_0); while (__==$$!=$_/_){sleep z$_2+_3z%$;} blur -> width ($);}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.program();
	}
	
}
