package cop5556sp17;

import static cop5556sp17.Scanner.Kind.AND;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.ASSIGN;
import static cop5556sp17.Scanner.Kind.BARARROW;
import static cop5556sp17.Scanner.Kind.COMMA;
import static cop5556sp17.Scanner.Kind.DIV;
import static cop5556sp17.Scanner.Kind.EOF;
import static cop5556sp17.Scanner.Kind.EQUAL;
import static cop5556sp17.Scanner.Kind.GE;
import static cop5556sp17.Scanner.Kind.GT;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_IF;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SCALE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_WHILE;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.LBRACE;
import static cop5556sp17.Scanner.Kind.LE;
import static cop5556sp17.Scanner.Kind.LPAREN;
import static cop5556sp17.Scanner.Kind.LT;
import static cop5556sp17.Scanner.Kind.MINUS;
import static cop5556sp17.Scanner.Kind.MOD;
import static cop5556sp17.Scanner.Kind.NOTEQUAL;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_SLEEP;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.OR;
import static cop5556sp17.Scanner.Kind.PLUS;
import static cop5556sp17.Scanner.Kind.RBRACE;
import static cop5556sp17.Scanner.Kind.RPAREN;
import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind.TIMES;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTNode;
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
import cop5556sp17.AST.WhileStatement;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input. You
	 * will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}

	/**
	 * Useful during development to ensure unimplemented routines are not
	 * accidentally called during development. Delete it when the Parser is
	 * finished.
	 *
	 */
	@SuppressWarnings("serial")
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner. Check for EOF (i.e. no
	 * trailing junk) when finished
	 * 
	 * @return
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	Program program() throws SyntaxException {
		// TODO
		// program ::= IDENT block
		// program ::= IDENT param_dec ( , param_dec )* block
		// Program ::= List<ParamDec> Block
		ArrayList<ParamDec> paramList = new ArrayList<ParamDec>();
		Token firstToken = t;
		if (t.isKind(IDENT)) {
			if (getFirstSets("param_dec").contains(scanner.peek().kind)) {
				consume();
				ParamDec p = paramDec();
				paramList.add(p);
				while (t.isKind(COMMA)) {
					consume();
					p = paramDec();
					paramList.add(p);
				}
				Block b = block();
				return new Program(firstToken, paramList, b);
			} else if (scanner.peek().isKind(LBRACE)) {
				consume();
				Block b = block();
				return new Program(firstToken, paramList, b);
			} else {
				throw new SyntaxException("Error while parsing program at " + t.kind);
			}
		} else {
			throw new SyntaxException("Error while parsing program. Doesn't start with INDENT");
		}
	}

	Expression expression() throws SyntaxException {
		// TODO
		// term ( relOp term)*
		// Expression ::= IdentExpression | IntLitExpression |
		// BooleanLitExpression | ConstantExpression | BinaryExpression
		Token firstToken = t;
		Expression e0 = null, e1 = null;
		e0 = term();
		while (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) {
			Token op = t;
			consume();
			e1 = term();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;

	}

	Expression term() throws SyntaxException {
		// TODO
		// elem ( weakOp elem)*
		Token firstToken = t;
		Expression e0 = null;
		e0 = elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)) {
			Token op = t;
			consume();
			Expression e1 = elem();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;

	}

	Expression elem() throws SyntaxException {
		// TODO
		// factor ( strongOp factor)*
		Token firstToken = t;
		Expression e0 = factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)) {
			Token op = t;
			consume();
			Expression e1 = factor();
			e0 = new BinaryExpression(firstToken, e0, op, e1);
		}
		return e0;

	}

	Expression factor() throws SyntaxException {
		// IDENT | INT_LIT | KW_TRUE | KW_FALSE
		// | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
		// Expression ::= IdentExpression | IntLitExpression |
		// BooleanLitExpression | ConstantExpression | BinaryExpression
		Expression e = null;
		Token firstToken = t;
		Kind kind = t.kind;
		switch (kind) {
		case IDENT: {
			e = new IdentExpression(firstToken);
			consume();
		}
			break;
		case INT_LIT: {
			e = new IntLitExpression(firstToken);
			consume();
		}
			break;
		case KW_TRUE: {
			e = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_FALSE: {
			e = new BooleanLitExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENWIDTH: {
			e = new ConstantExpression(firstToken);
			consume();
		}
			break;
		case KW_SCREENHEIGHT: {
			e = new ConstantExpression(firstToken);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			e = expression();
			match(RPAREN);
		}
			break;
		default:
			// you will want to provide a more useful error message
			throw new SyntaxException("Error while parsing factor at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		}
		return e;
	}

	Block block() throws SyntaxException {
		// TODO
		// { ( dec | statement) * }
		// Block ::= List<Dec> List<Statement>
		Token firstToken = t;
		ArrayList<Dec> decList = new ArrayList<Dec>();
		ArrayList<Statement> statList = new ArrayList<Statement>();

		match(LBRACE);
		while (getFirstSets("dec").contains(t.kind) || getFirstSets("statement").contains(t.kind)) {
			if (getFirstSets("dec").contains(t.kind)) {
				Dec d = dec();
				decList.add(d);
			} else if (getFirstSets("statement").contains(t.kind)) {
				Statement s = statement();
				statList.add(s);
			}
		}
		match(RBRACE);
		return new Block(firstToken, decList, statList);
	}

	ParamDec paramDec() throws SyntaxException {
		// TODO
		// throw new UnimplementedFeatureException();
		// paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN) IDENT
		// ParamDec ::= type ident
		// type ::= integer | image | frame | file | boolean | url
		Token firstToken = t;
		Kind kind = t.kind;
		switch (kind) {
		case KW_URL: {
			consume();
		}
			break;
		case KW_FILE: {
			consume();
		}
			break;
		case KW_INTEGER: {
			consume();
		}
			break;
		case KW_BOOLEAN: {
			consume();
		}
			break;
		default:
			// you will want to provide a more useful error message
			throw new SyntaxException("Error while parsing paramDec at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		}

		if (t.isKind(IDENT)) {
			Token ident = t;
			consume();
			return new ParamDec(firstToken, ident);
		} else
			throw new SyntaxException("Error while parsing paramDec at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		
	}

	Dec dec() throws SyntaxException {
		// TODO
		// dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME) IDENT
		// Dec ::= type ident
		Token firstToken = t;
		Token ident = null;
		Kind kind = t.kind;
		switch (kind) {
		case KW_INTEGER: {
			consume();
		}
			break;
		case KW_BOOLEAN: {
			consume();
		}
			break;
		case KW_IMAGE: {
			consume();
		}
			break;
		case KW_FRAME: {
			consume();
		}
			break;
		default:
			// you will want to provide a more useful error message
			throw new SyntaxException("Error while parsing dec at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		}

		if (t.isKind(IDENT)) {
			ident = t;
			consume();
		} else
			throw new SyntaxException("Error while parsing dec at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);

		Dec dec = new Dec(firstToken, ident);
		return dec;
	}

	Statement statement() throws SyntaxException {
		// TODO
		// statement ::= OP_SLEEP expression ; | whileStatement | ifStatement |
		// chain ; | assign ;
		// Statement ::= SleepStatement | WhileStatement | IfStatement | Chain |
		// AssignmentStatement
		Token firstToken = t;
		Expression e0 = null;
		Statement s = null;
		if (getFirstSets("statement").contains(t.kind)) {
			if (t.isKind(OP_SLEEP)) {
				consume();
				e0 = expression();
				match(SEMI);
				s = new SleepStatement(firstToken, e0);
			} else if (t.isKind(IDENT)) {
				if (scanner.peek().kind.equals(ASSIGN)) {
					s = assign();
					match(SEMI);
				} else if (scanner.peek().kind.equals(ARROW) || scanner.peek().kind.equals(BARARROW)) {
					s = chain();
					match(SEMI);
				} else {
					throw new SyntaxException("Error while parsing statement at " + t.kind+ " line no. " + t.getLinePos().line
							+ " col no. " + t.getLinePos().posInLine);
				}
			} else if (getFirstSets("ifStatement").contains(t.kind)) {
				s = ifStatement();
			} else if (getFirstSets("whileStatement").contains(t.kind)) {
				s = whileStatement();
			} else if (getFirstSets("chain").contains(t.kind)) {
				s = chain();
				match(SEMI);
			}
		} else
			throw new SyntaxException("Error while parsing statement at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		return s;

	}

	Chain chain() throws SyntaxException {
		// TODO
		// chainElem arrowOp chainElem ( arrowOp chainElem)*
		// Chain ::= ChainElem | BinaryChain
		Token firstToken = t;
		Token arrow = null;
		Chain c0 = chainElem();
		if (t.isKind(ARROW) || t.isKind(BARARROW)) {
			arrow = t;
			consume();
		} else
			throw new SyntaxException("Error while parsing chain at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		ChainElem c1 = chainElem();
		c0 = new BinaryChain(firstToken, c0, arrow, c1);
		while (t.isKind(ARROW) || t.isKind(BARARROW)) {
			arrow = t;
			consume();
			c1 = chainElem();
			c0 = new BinaryChain(firstToken, c0, arrow, c1);
		}
		return c0;
	}

	ChainElem chainElem() throws SyntaxException {
		// TODO
		// chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
		// ChainElem ::= IdentChain | FilterOpChain | FrameOpChain |
		// ImageOpChain
		Token firstToken = t;
		ChainElem c0 = null;
		if (t.isKind(IDENT)) {
			c0 = new IdentChain(firstToken);
			consume();
		} else if (getFirstSets("filterOp").contains(t.kind)) {
			filterOp();
			Tuple arg = arg();
			c0 = new FilterOpChain(firstToken, arg);

		} else if (getFirstSets("frameOp").contains(t.kind)) {
			frameOp();
			Tuple arg = arg();
			c0 = new FrameOpChain(firstToken, arg);
		} else if (getFirstSets("imageOp").contains(t.kind)) {
			imageOp();
			Tuple arg = arg();
			c0 = new ImageOpChain(firstToken, arg);
		} else
			throw new SyntaxException("Error while parsing chainElem at " + t.kind + " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		return c0;
	}

	Tuple arg() throws SyntaxException {
		// TODO
		// arg ::= empty | ( expression ( ,expression)* )
		Token firstToken = t;
		List<Expression> argList = new ArrayList<Expression>();
		if (t.isKind(LPAREN)) {
			match(LPAREN);
			Expression e = expression();
			argList.add(e);
			while (t.isKind(COMMA)) {
				consume();
				e = expression();
				argList.add(e);
			}
			match(RPAREN);
		}
		Tuple tup = new Tuple(firstToken, argList);
		return tup;
	}

	AssignmentStatement assign() throws SyntaxException {
		// TODO
		// assign ::= IDENT ASSIGN expression
		Token firstToken = t;
		Expression e = null;
		IdentLValue var = null;
		AssignmentStatement assignStmt = null;
		if (t.isKind(IDENT)) {
			var = new IdentLValue(t);
			consume();
		} else
			throw new SyntaxException("Error while parsing assign at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		if (t.isKind(ASSIGN))
			consume();
		else
			throw new SyntaxException("Error while parsing assign at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		e = expression();
		assignStmt = new AssignmentStatement(firstToken, var, e);
		return assignStmt;
	}

	IfStatement ifStatement() throws SyntaxException {
		// TODO
		// ifStatement ::= KW_IF ( expression ) block
		// IfStatement ::= Expression Block
		Token firstToken = t;
		Expression e = null;
		if (t.isKind(KW_IF))
			consume();
		else
			throw new SyntaxException("Error while parsing ifStatement at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		match(LPAREN);
		e = expression();
		match(RPAREN);
		Block b = block();
		IfStatement ifStatement = new IfStatement(firstToken, e, b);
		return ifStatement;
	}

	WhileStatement whileStatement() throws SyntaxException {
		// TODO
		// whileStatement ::= KW_WHILE ( expression ) block
		// WhileStatement ::= Expression Block
		Token firstToken = t;
		Expression e = null;
		if (t.isKind(KW_WHILE))
			consume();
		else
			throw new SyntaxException("Error while parsing whileStatement at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
		match(LPAREN);
		e = expression();
		match(RPAREN);
		Block b = block();
		WhileStatement whileStatement = new WhileStatement(firstToken, e, b);
		return whileStatement;
	}

	void filterOp() throws SyntaxException {
		// TODO
		// filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE
		if (t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)) {
			consume();
		} else
			throw new SyntaxException("Error while parsing filterOp at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);

	}

	void frameOp() throws SyntaxException {
		// TODO
		// frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
		if (t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || t.isKind(KW_YLOC)) {
			consume();
		} else
			throw new SyntaxException("Error while parsing frameOp at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);
	}

	void imageOp() throws SyntaxException {
		// TODO
		// imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE
		if (t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			consume();
		} else
			throw new SyntaxException("Error while parsing imageOp at " + t.kind+ " line no. " + t.getLinePos().line
					+ " col no. " + t.getLinePos().posInLine);

	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + " expected " + kind+ " line no. " + t.getLinePos().line
				+ " col no. " + t.getLinePos().posInLine);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	// private Token match(Kind... kinds) throws SyntaxException {
	// // TODO. Optional but handy
	// return null; // replace this statement
	// }

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

	private ArrayList<Kind> getFirstSets(String element) {
		ArrayList<Kind> firstSets = new ArrayList<Kind>();
		return getFirstSets(element, firstSets);
	}

	private ArrayList<Kind> getFirstSets(String element, ArrayList<Kind> firstSets) {
		switch (element) {
		case "dec":
			firstSets.add(Kind.KW_INTEGER);
			firstSets.add(Kind.KW_BOOLEAN);
			firstSets.add(Kind.KW_IMAGE);
			firstSets.add(Kind.KW_FRAME);
			break;
		case "statement":
			firstSets.add(Kind.OP_SLEEP);
			firstSets = getFirstSets("whileStatement", firstSets);
			firstSets = getFirstSets("ifStatement", firstSets);
			firstSets = getFirstSets("chain", firstSets);
			firstSets = getFirstSets("assign", firstSets);
			break;
		case "whileStatement":
			firstSets.add(Kind.KW_WHILE);
			break;
		case "ifStatement":
			firstSets.add(Kind.KW_IF);
			break;
		case "assign":
			firstSets.add(Kind.IDENT);
			break;
		case "chain":
			firstSets = getFirstSets("chainElem", firstSets);
			break;
		case "chainElem":
			firstSets.add(Kind.IDENT);
			firstSets = getFirstSets("filterOp", firstSets);
			firstSets = getFirstSets("imageOp", firstSets);
			firstSets = getFirstSets("filterOp", firstSets);
			break;
		case "filterOp":
			firstSets.add(Kind.OP_BLUR);
			firstSets.add(Kind.OP_GRAY);
			firstSets.add(Kind.OP_CONVOLVE);
			break;
		case "frameOp":
			firstSets.add(Kind.KW_SHOW);
			firstSets.add(Kind.KW_HIDE);
			firstSets.add(Kind.KW_MOVE);
			firstSets.add(Kind.KW_XLOC);
			firstSets.add(Kind.KW_YLOC);
			break;
		case "imageOp":
			firstSets.add(Kind.OP_WIDTH);
			firstSets.add(Kind.OP_HEIGHT);
			firstSets.add(Kind.KW_SCALE);
			break;
		case "expression":
			firstSets.add(Kind.IDENT);
			firstSets.add(Kind.INT_LIT);
			firstSets.add(Kind.KW_TRUE);
			firstSets.add(Kind.KW_FALSE);
			firstSets.add(Kind.KW_SCREENWIDTH);
			firstSets.add(Kind.KW_SCREENHEIGHT);
			firstSets.add(Kind.LPAREN);
			break;
		case "term":
			firstSets.add(Kind.IDENT);
			firstSets.add(Kind.INT_LIT);
			firstSets.add(Kind.KW_TRUE);
			firstSets.add(Kind.KW_FALSE);
			firstSets.add(Kind.KW_SCREENWIDTH);
			firstSets.add(Kind.KW_SCREENHEIGHT);
			firstSets.add(Kind.LPAREN);
			break;
		case "factor":
			firstSets.add(Kind.IDENT);
			firstSets.add(Kind.INT_LIT);
			firstSets.add(Kind.KW_TRUE);
			firstSets.add(Kind.KW_FALSE);
			firstSets.add(Kind.KW_SCREENWIDTH);
			firstSets.add(Kind.KW_SCREENHEIGHT);
			firstSets.add(Kind.LPAREN);
			break;
		case "elem":
			firstSets.add(Kind.IDENT);
			firstSets.add(Kind.INT_LIT);
			firstSets.add(Kind.KW_TRUE);
			firstSets.add(Kind.KW_FALSE);
			firstSets.add(Kind.KW_SCREENWIDTH);
			firstSets.add(Kind.KW_SCREENHEIGHT);
			firstSets.add(Kind.LPAREN);
			break;
		case "param_dec":
			firstSets.add(Kind.KW_URL);
			firstSets.add(Kind.KW_FILE);
			firstSets.add(Kind.KW_INTEGER);
			firstSets.add(Kind.KW_BOOLEAN);
			break;
		default:
			return null;
		}

		Set<Kind> hs = new HashSet<>();
		hs.addAll(firstSets);
		firstSets.clear();
		firstSets.addAll(hs);
		return firstSets;

	}

}
