package cop5556sp17;

import static cop5556sp17.Scanner.Kind.GT;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.INT_LIT;
import static cop5556sp17.Scanner.Kind.KW_INTEGER;
import static cop5556sp17.Scanner.Kind.KW_TRUE;
import static cop5556sp17.Scanner.Kind.LBRACE;
import static cop5556sp17.Scanner.Kind.LE;
import static cop5556sp17.Scanner.Kind.LT;
import static cop5556sp17.Scanner.Kind.MINUS;
import static cop5556sp17.Scanner.Kind.NOT;
import static cop5556sp17.Scanner.Kind.OR;
import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.RBRACE;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();


	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testCommentsNewLines() throws IllegalCharException, IllegalNumberException {
		String input = "{/*\n\n*/}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		//token=scanner.nextToken();
		while(token!=null){
			if(token.getText().equals("}")){
				assertEquals(RBRACE, token.kind);
				assertEquals(2, token.getLinePos().line);
				assertEquals(2, token.getLinePos().posInLine);
				//assertEquals(token, scanner.peek());
			}

			token= scanner.nextToken();
		}
	}
	
	@Test
	public void testEndWhiteSpace() throws IllegalCharException, IllegalNumberException {
		String input = "a\na ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		while(token!=null){
			//System.out.println(token.getText()+" "+token.kind+" "+token.pos);
			token= scanner.nextToken();
		}
	}
	
	@Test
	public void testIllegalEqual()  throws IllegalCharException, IllegalNumberException{
		String input = "{/===abcd120a|/<01b01\ninteger\ntruegray|true|gray|0yloc0\n*/";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test
	public void testKeyWords1() throws IllegalCharException, IllegalNumberException{
		String input = "{/==abcd120a|/<01b01\ninteger\ntruegray|true|gray|0yloc0\n*/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		while(token!=null){
			if(token.getText().contains("12")){
			assertEquals(IDENT, token.kind);
			assertEquals(8, token.getText().length());
			assertEquals(0, token.getLinePos().line);
			}
			if(token.getText().equals("integer")){
				assertEquals(KW_INTEGER, token.kind);
				assertEquals(1, token.getLinePos().line);
			}
			if(token.getText().equals("integer")){
				assertEquals(KW_INTEGER, token.kind);
				assertEquals(1, token.getLinePos().line);
			}
			if(token.getText().equals("true")){
				assertEquals(KW_TRUE, token.kind);
				assertEquals(2, token.getLinePos().line);
			}
			if(token.getText().equals("<")){
				assertEquals(LT, token.kind);
				assertEquals(LT.text, token.getText());
				assertEquals(14, token.pos);
				assertEquals(14, token.getLinePos().posInLine);
			}

			token= scanner.nextToken();
		}
	}
	
	@Test 
	public void testIllegalChar() throws IllegalCharException, IllegalNumberException {
		String input = "{\n-|->\n\n\nA$&#\n";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}
	
	@Test 
	public void testIllegalNum() throws IllegalCharException, IllegalNumberException {
		String input = "{sleep/eat/code08989845354353453412323321321332313a12313123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		while(token!=null){
			if(token.getText().contains("13")){
			assertEquals(IDENT, token.kind);
			}
			token= scanner.nextToken();
		}
	}
	@Test 
	public void testMinus() throws IllegalCharException, IllegalNumberException {
		String input = "{a++b++c++0integer++/!|-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		HashMap<Integer,Kind> testMap= new HashMap<Integer,Kind>();
		testMap.put(0, LBRACE);
		testMap.put(1, IDENT);
		testMap.put(2, PLUS);
		testMap.put(10, INT_LIT);
		testMap.put(11, KW_INTEGER);
		testMap.put(21, NOT);
		testMap.put(22, OR);
		while(token!=null){
			if(token.pos==0 ||token.pos==1||token.pos==2||token.pos==10||token.pos==11||token.pos==21||token.pos==22){
				assertEquals(testMap.get(token.pos),token.kind);
			}
			token= scanner.nextToken();
		}
	}
	
	@Test
	public void testBarrow() throws IllegalCharException, IllegalNumberException {
		String input = "= =";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		//assertEquals(OR, token.kind);
		
		while(token!=null){
			System.out.println(token.getText()+" "+token.kind+" "+token.pos);
			token= scanner.nextToken();
		}
		
		}
	
	@Test
	public void testNewStringWithNewLine() throws IllegalCharException, IllegalNumberException{
		String input = "| 123 abc0\n 01677 |\n - <=> | - >";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		//Token 1
		assertEquals(OR, token.kind);
		assertEquals(0, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(0,token.getLinePos().posInLine);
		String text = OR.getText();
		text = OR.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos1 =token.getLinePos();
		assertEquals(0, linepos1.line);
		assertEquals(0,linepos1.posInLine);
		
		//Token 2
		token= scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(2, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(2,token.getLinePos().posInLine);
	//	String text = I.getText();
		text = INT_LIT.getText();
		assertEquals("123", token.getText());
		assertEquals(3, token.length);
		LinePos linepos2 =token.getLinePos();
		assertEquals(0, linepos2.line);
		assertEquals(2,linepos2.posInLine);
		
		//Token 3
		token= scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(6, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(6,token.getLinePos().posInLine);
	//	String text = I.getText();
		text = IDENT.getText();
		assertEquals("abc0", token.getText());
		assertEquals(4, token.length);
		LinePos linepos3 =token.getLinePos();
		assertEquals(0, linepos3.line);
		assertEquals(6,linepos3.posInLine);
		
		//Token 4
		token= scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(12, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(1,token.getLinePos().posInLine);
	//	String text = I.getText();
		text = INT_LIT.getText();
		assertEquals("0", token.getText());
		assertEquals(1, token.length);
		LinePos linepos4 =token.getLinePos();
		assertEquals(1, linepos4.line);
		assertEquals(1,linepos4.posInLine);
		
		//Token 5
		token= scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(13, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(2,token.getLinePos().posInLine);
	//	String text = I.getText();
		text = INT_LIT.getText();
		assertEquals("1677", token.getText());
		assertEquals(4, token.length);
		LinePos linepos5 =token.getLinePos();
		assertEquals(1, linepos5.line);
		assertEquals(2,linepos5.posInLine);
		
		//Token 6
		token= scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(18, token.pos);
		assertEquals(1,token.getLinePos().line);
		assertEquals(7,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = OR.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos6 =token.getLinePos();
		assertEquals(1, linepos6.line);
		assertEquals(7,linepos6.posInLine);
		
		//Token 7
		token= scanner.nextToken();
		assertEquals(MINUS, token.kind);
		assertEquals(21, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(1,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = MINUS.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos7 =token.getLinePos();
		assertEquals(2, linepos7.line);
		assertEquals(1,linepos7.posInLine);
		
		//Token 8
		token= scanner.nextToken();
		assertEquals(LE, token.kind);
		assertEquals(23, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(3,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = LE.getText();
		assertEquals(text, token.getText());
		assertEquals(2, token.length);
		LinePos linepos8 =token.getLinePos();
		assertEquals(2, linepos8.line);
		assertEquals(3,linepos8.posInLine);
		
		//Token 9
		token= scanner.nextToken();
		assertEquals(GT, token.kind);
		assertEquals(25, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(5,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = GT.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos9 =token.getLinePos();
		assertEquals(2, linepos9.line);
		assertEquals(5,linepos9.posInLine);
		
		//Token 10
		token= scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(27, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(7,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = OR.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos10 =token.getLinePos();
		assertEquals(2, linepos10.line);
		assertEquals(7,linepos10.posInLine);

		//Token 11
		token= scanner.nextToken();
		assertEquals(MINUS, token.kind);
		assertEquals(29, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(9,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = MINUS.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos11 =token.getLinePos();
		assertEquals(2, linepos11.line);
		assertEquals(9,linepos11.posInLine);		
		
		//Token 12
		token= scanner.nextToken();
		assertEquals(GT, token.kind);
		assertEquals(31, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(11,token.getLinePos().posInLine);
		//String text = OR.getText();
		text = GT.getText();
		assertEquals(text, token.getText());
		assertEquals(1, token.length);
		LinePos linepos12 =token.getLinePos();
		assertEquals(2, linepos12.line);
		assertEquals(11,linepos12.posInLine);

	}
	
	@Test
	public void testLineNumber() throws IllegalCharException, IllegalNumberException {
		String input = "123mno\n|abc\n0def\n==ghi\n/jkl";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		while(token!=null){
			if(token.getText().contains("n")){
				assertEquals(IDENT,token.kind);
				assertEquals(0,token.getLinePos().line);
				assertEquals(3,token.getLinePos().posInLine);
			}
			if(token.getText().contains("a")){
				assertEquals(IDENT,token.kind);
				assertEquals(1,token.getLinePos().line);
				assertEquals(1,token.getLinePos().posInLine);
			}
			if(token.getText().contains("d")){
				assertEquals(IDENT,token.kind);
				assertEquals(2,token.getLinePos().line);
				assertEquals(1,token.getLinePos().posInLine);
			}
			if(token.getText().contains("g")){
				assertEquals(IDENT,token.kind);
				assertEquals(3,token.getLinePos().line);
				assertEquals(2,token.getLinePos().posInLine);
			}
			if(token.getText().contains("l")){
				assertEquals(IDENT,token.kind);
				assertEquals(4,token.getLinePos().line);
				assertEquals(1,token.getLinePos().posInLine);
			}
			token= scanner.nextToken();
		}
	}
	
	@Test
	public void testNumTokens() throws IllegalCharException, IllegalNumberException{
		
		String input = "12!adasd_\n\n\n$$$/convolve/*";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		int count=0;
		while(token!=null){
			count++;
			//System.out.println(token.getText()+" "+token.kind+" "+token.pos);

			token= scanner.nextToken();
		}
		assertEquals(7, count);
	}
	
	@Test
	public void testBarArrow() throws IllegalCharException, IllegalNumberException{
		String input = "a{/*hellobye\ntatahi\n*/0|-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		
		token=scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(0, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(0,token.getLinePos().posInLine);
		String text = IDENT.getText();
		assertEquals(1, token.length);
		assertEquals("a",token.getText());
		
		//nextToken
		token=scanner.nextToken();
		assertEquals(LBRACE, token.kind);
		assertEquals(1, token.pos);
		assertEquals(0,token.getLinePos().line);
		assertEquals(1,token.getLinePos().posInLine);
		text = LBRACE.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		assertEquals("{",token.getText());
		
		//nextToken
		token=scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(22, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(2,token.getLinePos().posInLine);
		text = INT_LIT.getText();
		assertEquals(1, token.length);
	//	assertEquals(text, token.getText());
		//assertEquals(0,token.intVal(text));
		
		//nextToken
		token=scanner.nextToken();
		assertEquals(OR, token.kind);
		assertEquals(23, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(3,token.getLinePos().posInLine);
		text = OR.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		assertEquals("|",token.getText());
		
		//lastToken
		token=scanner.nextToken();
		assertEquals(MINUS, token.kind);
		assertEquals(24, token.pos);
		assertEquals(2,token.getLinePos().line);
		assertEquals(4,token.getLinePos().posInLine);
		text = MINUS.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		assertEquals("-",token.getText());
	}
	
	@Test
	public void testIntVal() throws IllegalNumberException,IllegalCharException{
		String input = "567876543";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token=scanner.nextToken();
		assertEquals(567876543,token.intVal());
	}
	
	@Test
	public void testNewLine() throws IllegalCharException, IllegalNumberException{
		//System.out.println("TestNewLineeeeeeeeeeeeeeeeee");
		String input = "{/*\n\n*/}jj/*\na*/b";
				
		Scanner scanner = new Scanner(input);
		scanner.scan();
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(LBRACE, token.kind);
		assertEquals(0, token.pos);
		String text = LBRACE.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(RBRACE, token1.kind);
		assertEquals(7, token1.pos);
		assertEquals(1, token1.length);
		assertEquals("}", token1.getText());
		
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(IDENT, token2.kind);
		assertEquals(8, token2.pos);
		assertEquals(2, token2.length);
		assertEquals("jj", token2.getText());		
		
		token2 = scanner.nextToken();
		
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "+*310<-->ab23*)";//"abc/*asdad*/";//";;;\na";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = null;
		token= scanner.nextToken();
		while(token!=null){
			token= scanner.nextToken();
		}
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
	}
	
	public String readFromFile(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		StringBuilder sb = new StringBuilder();
		try {
		    
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * 
	 * case1: unclosed comment
	 * case2: Illegal characters should be ignored if inside the comment tag
	 * case3: TODO check the line number value change if there is a new line inside the comment section
	 */
	@Test
	public void testComment() throws IllegalCharException, IllegalNumberException{
		String input = "test integer with /*comment here /** )(#@ '/ still should pass";
		Scanner scanner = new Scanner(input);
		//thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.IDENT, token1.kind);
		assertEquals("test", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.KW_INTEGER, token2.kind);
		assertEquals("integer", token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.IDENT, token3.kind);
		assertEquals("with", token3.getText());
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * 
	 * case1: identify proper start with _, $, A..Z, a..z
	 * case2:
	 */
	@Test
	public void testIdent() throws IllegalCharException, IllegalNumberException {
		String input = "_test $integer Qwith sTEST 123character";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.IDENT, token1.kind);
		assertEquals("_test", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.IDENT, token2.kind);
		assertEquals("$integer", token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.IDENT, token3.kind);
		assertEquals("Qwith", token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Kind.IDENT, token4.kind);
		assertEquals("sTEST", token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token5.kind);
		assertEquals("123", token5.getText());
		assertEquals(123, token5.intVal());
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(Kind.IDENT, token6.kind);
		assertEquals("character", token6.getText());
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * 
	 * case1: numbers starting with 0
	 * case2: zero is in the middle
	 * case3: zero at the end
	 */
	@Test
	public void testNumLiteral() throws IllegalCharException, IllegalNumberException {
		String input = "00123455035 87000";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token1.kind);
		assertEquals(0, token1.intVal());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token2.kind);
		assertEquals(0, token2.intVal());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token3.kind);
		assertEquals(123455035, token3.intVal());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Kind.INT_LIT, token4.kind);
		assertEquals(87000, token4.intVal());
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 * 
	 */
	@Test
	public void testKeyWords() throws IllegalCharException, IllegalNumberException {
		String input = "integer boolean image url file frame "
				+ "\nwhile if sleep screenheight screenwidth"
				+ "\ngray convolve blur scale"
				+ "\nwidth height"
				+ "\nxloc yloc hide show move"
				+ "\ntrue false";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.KW_INTEGER, token1.kind);
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.KW_BOOLEAN, token2.kind);
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.KW_IMAGE, token3.kind);
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Kind.KW_URL, token4.kind);
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Kind.KW_FILE, token5.kind);
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(Kind.KW_FRAME, token6.kind);
		Scanner.Token token7 = scanner.nextToken();
		assertEquals(Kind.KW_WHILE, token7.kind);
		Scanner.Token token8 = scanner.nextToken();
		assertEquals(Kind.KW_IF, token8.kind);
		Scanner.Token token9 = scanner.nextToken();
		assertEquals(Kind.OP_SLEEP, token9.kind);
		Scanner.Token token10 = scanner.nextToken();
		assertEquals(Kind.KW_SCREENHEIGHT, token10.kind);
		Scanner.Token token11 = scanner.nextToken();
		assertEquals(Kind.KW_SCREENWIDTH, token11.kind);
		Scanner.Token token12 = scanner.nextToken();
		assertEquals(Kind.OP_GRAY, token12.kind);
		Scanner.Token token13 = scanner.nextToken();
		assertEquals(Kind.OP_CONVOLVE, token13.kind);
		Scanner.Token token14 = scanner.nextToken();
		assertEquals(Kind.OP_BLUR, token14.kind);
		Scanner.Token token15 = scanner.nextToken();
		assertEquals(Kind.KW_SCALE, token15.kind);
		Scanner.Token token16 = scanner.nextToken();
		assertEquals(Kind.OP_WIDTH, token16.kind);
		Scanner.Token token17 = scanner.nextToken();
		assertEquals(Kind.OP_HEIGHT, token17.kind);
		Scanner.Token token18 = scanner.nextToken();
		assertEquals(Kind.KW_XLOC, token18.kind);
		Scanner.Token token19 = scanner.nextToken();
		assertEquals(Kind.KW_YLOC, token19.kind);
		Scanner.Token token20 = scanner.nextToken();
		assertEquals(Kind.KW_HIDE, token20.kind);
		Scanner.Token token21 = scanner.nextToken();
		assertEquals(Kind.KW_SHOW, token21.kind);
		Scanner.Token token22 = scanner.nextToken();
		assertEquals(Kind.KW_MOVE, token22.kind);
		Scanner.Token token23 = scanner.nextToken();
		assertEquals(Kind.KW_TRUE, token23.kind);
		Scanner.Token token24 = scanner.nextToken();
		assertEquals(Kind.KW_FALSE, token24.kind);				
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testSeparatorOperator1() throws IllegalCharException, IllegalNumberException {
		String input = "!===";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token1.kind);
		assertEquals("!=", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.EQUAL, token2.kind);
		assertEquals("==", token2.getText());
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testSeparatorOperator2() throws IllegalCharException, IllegalNumberException {
		String input = "!==";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token1.kind);
		assertEquals("!=", token1.getText());		
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testSeparatorOperator3() throws IllegalCharException, IllegalNumberException {
		String input = "<<-|->";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.LT, token1.kind);
		assertEquals("<", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.ASSIGN, token2.kind);
		assertEquals("<-", token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.BARARROW, token3.kind);
		assertEquals("|->", token3.getText());
	}
	
	/**
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testSeparatorOperator4() throws IllegalCharException, IllegalNumberException {
		String input = ";,(){}|&==!=<><=>=+-*/%!->|-><-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.SEMI, token1.kind);
		assertEquals(";", token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.COMMA, token2.kind);
		assertEquals(",", token2.getText());
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Kind.LPAREN, token3.kind);
		assertEquals("(", token3.getText());
		Scanner.Token token4 = scanner.nextToken();
		assertEquals(Kind.RPAREN, token4.kind);
		assertEquals(")", token4.getText());
		Scanner.Token token5 = scanner.nextToken();
		assertEquals(Kind.LBRACE, token5.kind);
		assertEquals("{", token5.getText());
		Scanner.Token token6 = scanner.nextToken();
		assertEquals(Kind.RBRACE, token6.kind);
		assertEquals("}", token6.getText());
		Scanner.Token token7 = scanner.nextToken();
		assertEquals(Kind.OR, token7.kind);
		assertEquals("|", token7.getText());
		Scanner.Token token8 = scanner.nextToken();
		assertEquals(Kind.AND, token8.kind);
		assertEquals("&", token8.getText());
		Scanner.Token token9 = scanner.nextToken();
		assertEquals(Kind.EQUAL, token9.kind);
		assertEquals("==", token9.getText());
		Scanner.Token token10 = scanner.nextToken();
		assertEquals(Kind.NOTEQUAL, token10.kind);
		assertEquals("!=", token10.getText());
		Scanner.Token token11 = scanner.nextToken();
		assertEquals(Kind.LT, token11.kind);
		assertEquals("<", token11.getText());
		Scanner.Token token12 = scanner.nextToken();
		assertEquals(Kind.GT, token12.kind);
		assertEquals(">", token12.getText());
		Scanner.Token token13 = scanner.nextToken();
		assertEquals(Kind.LE, token13.kind);
		assertEquals("<=", token13.getText());
		Scanner.Token token14 = scanner.nextToken();
		assertEquals(Kind.GE, token14.kind);
		assertEquals(">=", token14.getText());
		Scanner.Token token15 = scanner.nextToken();
		assertEquals(Kind.PLUS, token15.kind);
		assertEquals("+", token15.getText());
		Scanner.Token token16 = scanner.nextToken();
		assertEquals(Kind.MINUS, token16.kind);
		assertEquals("-", token16.getText());
		Scanner.Token token17 = scanner.nextToken();
		assertEquals(Kind.TIMES, token17.kind);
		assertEquals("*", token17.getText());
		Scanner.Token token18 = scanner.nextToken();
		assertEquals(Kind.DIV, token18.kind);
		assertEquals("/", token18.getText());
		Scanner.Token token19 = scanner.nextToken();
		assertEquals(Kind.MOD, token19.kind);
		assertEquals("%", token19.getText());
		Scanner.Token token20 = scanner.nextToken();
		assertEquals(Kind.NOT, token20.kind);
		assertEquals("!", token20.getText());
		Scanner.Token token21 = scanner.nextToken();
		assertEquals(Kind.ARROW, token21.kind);
		assertEquals("->", token21.getText());
		Scanner.Token token22 = scanner.nextToken();
		assertEquals(Kind.BARARROW, token22.kind);
		assertEquals("|->", token22.getText());
		Scanner.Token token23 = scanner.nextToken();
		assertEquals(Kind.ASSIGN, token23.kind);
		assertEquals("<-", token23.getText());
		
	}
	
	
	@Test
	public void testIllegalChar1() throws IllegalCharException, IllegalNumberException {
		String input = "test ~ string";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(Kind.IDENT, token1.kind);
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Kind.IDENT, token2.kind);
	}

	
}
