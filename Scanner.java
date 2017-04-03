package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
	/**
	 * Kind enum
	 */
	List<Integer> lineNos = new ArrayList<Integer>();

	public static enum State {
		START, IN_DIGIT, IN_IDENT, AFTER_EQ, AFTER_SLASH, AFTER_MINUS, AFTER_EXCLAMATION, AFTER_GREATER, AFTER_LESS, AFTER_PIPE;
	}

	public static enum Kind {
		COMMENT(""), IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), KW_IMAGE("image"), KW_URL(
				"url"), KW_FILE("file"), KW_FRAME("frame"), KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE(
						"false"), SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), RBRACE("}"), ARROW(
								"->"), BARARROW("|->"), OR("|"), AND("&"), EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(
										">"), LE("<="), GE(">="), PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD(
												"%"), NOT("!"), ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY(
														"gray"), OP_CONVOLVE("convolve"), KW_SCREENHEIGHT(
																"screenheight"), KW_SCREENWIDTH(
																		"screenwidth"), OP_WIDTH("width"), OP_HEIGHT(
																				"height"), KW_XLOC("xloc"), KW_YLOC(
																						"yloc"), KW_HIDE(
																								"hide"), KW_SHOW(
																										"show"), KW_MOVE(
																												"move"), OP_SLEEP(
																														"sleep"), KW_SCALE(
																																"scale"), EOF(
																																		"eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}

	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown by Scanner when an int literal is not a value that can be
	 * represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
		public IllegalNumberException(String message) {
			super(message);
		}
	}

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;

		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}

	public class Token {
		public final Kind kind;
		public final int pos; // position in input array
		public final int length;

		// returns the text of this Token
		public String getText() {
			if (kind == Kind.EOF) {
				return "eof";
			} else
				return new StringBuilder().append(chars, pos, pos + length).toString();
		}

		// returns a LinePos object representing the line and column of this
		// Token
		LinePos getLinePos() {
			return new LinePos(search(pos), pos - lineNos.get(search(pos)));
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/**
		 * Precondition: kind = Kind.INT_LIT, the text can be represented with a
		 * Java int. Note that the validity of the input should have been
		 * checked when the Token was created. So the exception should never be
		 * thrown.
		 * 
		 * @return int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException {
			if (kind == Kind.INT_LIT) {
				return Integer.parseInt(this.getText());
			} else
				return -1;
		}

		public boolean isKind(Kind kind) {
			return this.kind == kind;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}

		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to
	 * tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0;
		int length = chars.length();
		State state = State.START;
		int startPos = 0;
		int ch;
		lineNos.add(0);
		while (pos <= length) {
			ch = pos < length ? chars.charAt(pos) : -1;
			switch (state) {

			case START:
				pos = skipWhiteSpace(pos);
				ch = pos < length ? chars.charAt(pos) : -1;
				startPos = pos;

				switch (ch) {
				case -1: {
					tokens.add(new Token(Kind.EOF, pos, 0));
					pos++;
				}
					break;
				case '+': {
					tokens.add(new Token(Kind.PLUS, startPos, 1));
					pos++;
				}
					break;
				case '*': {
					tokens.add(new Token(Kind.TIMES, startPos, 1));
					pos++;
				}
					break;
				case '%': {
					tokens.add(new Token(Kind.MOD, startPos, 1));
					pos++;
				}
					break;
				case '&': {
					tokens.add(new Token(Kind.AND, startPos, 1));
					pos++;
				}
					break;
				case '0': {
					tokens.add(new Token(Kind.INT_LIT, startPos, 1));
					pos++;
				}
					break;
				case '(': {
					tokens.add(new Token(Kind.LPAREN, startPos, 1));
					pos++;
				}
					break;
				case ')': {
					tokens.add(new Token(Kind.RPAREN, startPos, 1));
					pos++;
				}
					break;
				case ';': {
					tokens.add(new Token(Kind.SEMI, startPos, 1));
					pos++;
				}
					break;
				case ',': {
					tokens.add(new Token(Kind.COMMA, startPos, 1));
					pos++;
				}
					break;
				case '{': {
					tokens.add(new Token(Kind.LBRACE, startPos, 1));
					pos++;
				}
					break;
				case '}': {
					tokens.add(new Token(Kind.RBRACE, startPos, 1));
					pos++;
				}
					break;
				case '|':
					if (pos + 1 < length) {
						state = State.AFTER_PIPE;
						pos++;
					} else {
						tokens.add(new Token(Kind.OR, startPos, 1));
						state = State.START;
						pos++;
					}
					break;
				case '=': {
					if (pos + 1 < length) {
						state = State.AFTER_EQ;
						pos++;
					} else {
						state = State.START;
						throw new IllegalCharException("illegal char " + (char) ch + " at pos " + pos++);
					}
					break;
				}
				case '>':
					if (pos + 1 < length) {
						state = State.AFTER_GREATER;
						pos++;
					} else {
						tokens.add(new Token(Kind.GT, startPos, 1));
						state = State.START;
						pos++;
					}
					break;
				case '<':
					if (pos + 1 < length) {
						state = State.AFTER_LESS;
						pos++;
					} else {
						tokens.add(new Token(Kind.LT, startPos, 1));
						state = State.START;
						pos++;
					}
					break;
				case '!':
					if (pos + 1 < length) {
						state = State.AFTER_EXCLAMATION;
						pos++;
					} else {
						tokens.add(new Token(Kind.NOT, startPos, 1));
						state = State.START;
						pos++;
					}
					break;
				case '-':
					if (pos + 1 < length) {
						state = State.AFTER_MINUS;
						pos++;
					} else {
						tokens.add(new Token(Kind.MINUS, startPos, 1));
						state = State.START;
						pos++;
					}
					break;
				case '/':
					if (pos + 1 < length) {
						state = State.AFTER_SLASH;
						pos++;
					} else {
						tokens.add(new Token(Kind.DIV, startPos, 1));
						state = State.START;
						pos++;
					}
					break;

				default: {
					if (Character.isDigit(ch)) {
						if (pos + 1 < length) {
							state = State.IN_DIGIT;
							pos++;
						} else {
							tokens.add(new Token(Kind.INT_LIT, startPos, 1));
							state = State.START;
							pos++;
						}
					} else if (Character.isJavaIdentifierStart(ch)) {
						if (pos + 1 < length) {
							state = State.IN_IDENT;
							pos++;
						} else {
							tokens.add(new Token(Kind.IDENT, startPos, 1));
							state = State.START;
							pos++;
						}
					} else {
						throw new IllegalCharException("illegal char " + (char) ch + " at pos " + pos);
					}
				}

				}
				break;

			case IN_DIGIT:
				int digitLength = 1;
				while (pos < length && Character.isDigit(chars.charAt(pos))) {
					pos++;
					digitLength++;
				}
				StringBuilder sb = new StringBuilder();
				sb.append(chars, startPos, pos);
				try {
					Integer.parseInt(sb.toString());
					tokens.add(new Token(Kind.INT_LIT, startPos, digitLength));
					state = State.START;
				} catch (Exception e) {
					throw new IllegalNumberException("Java Int out of range");
				}
				break;
			case IN_IDENT:
				StringBuilder str = new StringBuilder();
				int strLength;
				str.append(chars.charAt(pos - 1));
				while (pos < length && Character.isJavaIdentifierPart(chars.charAt(pos))) {
					str.append(chars.charAt(pos));
					chars.charAt(pos);
					pos++;
				}
				state = State.START;
				strLength = str.length();
				switch (str.toString()) {
				case "integer": {
					tokens.add(new Token(Kind.KW_INTEGER, startPos, strLength));
				}
					break;
				case "boolean": {
					tokens.add(new Token(Kind.KW_BOOLEAN, startPos, strLength));
				}
					break;
				case "image": {
					tokens.add(new Token(Kind.KW_IMAGE, startPos, strLength));
				}
					break;
				case "url": {
					tokens.add(new Token(Kind.KW_URL, startPos, strLength));
				}
					break;
				case "file": {
					tokens.add(new Token(Kind.KW_FILE, startPos, strLength));
				}
					break;
				case "frame": {
					tokens.add(new Token(Kind.KW_FRAME, startPos, strLength));
				}
					break;
				case "while": {
					tokens.add(new Token(Kind.KW_WHILE, startPos, strLength));
				}
					break;
				case "if": {
					tokens.add(new Token(Kind.KW_IF, startPos, strLength));
				}
					break;
				case "true": {
					tokens.add(new Token(Kind.KW_TRUE, startPos, strLength));
				}
					break;
				case "false": {
					tokens.add(new Token(Kind.KW_FALSE, startPos, strLength));
				}
					break;
				case "blur": {
					tokens.add(new Token(Kind.OP_BLUR, startPos, strLength));
				}
					break;
				case "gray": {
					tokens.add(new Token(Kind.OP_GRAY, startPos, strLength));
				}
					break;
				case "convolve": {
					tokens.add(new Token(Kind.OP_CONVOLVE, startPos, strLength));
				}
					break;
				case "screenheight": {
					tokens.add(new Token(Kind.KW_SCREENHEIGHT, startPos, strLength));
				}
					break;
				case "screenwidth": {
					tokens.add(new Token(Kind.KW_SCREENWIDTH, startPos, strLength));
				}
					break;
				case "width": {
					tokens.add(new Token(Kind.OP_WIDTH, startPos, strLength));
				}
					break;
				case "height": {
					tokens.add(new Token(Kind.OP_HEIGHT, startPos, strLength));
				}
					break;
				case "xloc": {
					tokens.add(new Token(Kind.KW_XLOC, startPos, strLength));
				}
					break;
				case "yloc": {
					tokens.add(new Token(Kind.KW_YLOC, startPos, strLength));
				}
					break;
				case "hide": {
					tokens.add(new Token(Kind.KW_HIDE, startPos, strLength));
				}
					break;
				case "show": {
					tokens.add(new Token(Kind.KW_SHOW, startPos, strLength));
				}
					break;
				case "move": {
					tokens.add(new Token(Kind.KW_MOVE, startPos, strLength));
				}
					break;
				case "sleep": {
					tokens.add(new Token(Kind.OP_SLEEP, startPos, strLength));
				}
					break;
				case "scale": {
					tokens.add(new Token(Kind.KW_SCALE, startPos, strLength));
				}
					break;
				default: {
					tokens.add(new Token(Kind.IDENT, startPos, strLength));
				}

				}

				break;
			case AFTER_EQ:
				ch = chars.charAt(pos);
				if (ch == '=') {
					tokens.add(new Token(Kind.EQUAL, startPos, 2));
					pos++;
					state = State.START;
				} else {
					throw new IllegalCharException("illegal char " + (char) ch + " at pos " + pos);
				}
				break;
			case AFTER_PIPE:
				ch = chars.charAt(pos);
				int flagMinus = 0;
				if (ch == '-') {
					if (pos + 1 < length && chars.charAt(pos + 1) == '>') {
						tokens.add(new Token(Kind.BARARROW, startPos, 3));
						pos = pos + 2;
						state = State.START;
					} else {
						tokens.add(new Token(Kind.OR, startPos, 1));
						flagMinus = 1;
					}
				} else {
					tokens.add(new Token(Kind.OR, startPos, 1));

				}
				if (flagMinus == 1) {
					tokens.add(new Token(Kind.MINUS, pos, 1));
					pos++;
				}
				state = State.START;
				break;
			case AFTER_GREATER: {
				ch = chars.charAt(pos);
				if (ch == '=') {
					tokens.add(new Token(Kind.GE, startPos, 2));
					pos++;
					state = State.START;
				} else {
					tokens.add(new Token(Kind.GT, startPos, 1));
					state = State.START;
				}
			}
				break;
			case AFTER_LESS: {
				ch = chars.charAt(pos);
				if (ch == '=') {
					tokens.add(new Token(Kind.LE, startPos, 2));
					pos++;
					state = State.START;
				} else if (ch == '-') {
					tokens.add(new Token(Kind.ASSIGN, startPos, 2));
					pos++;
					state = State.START;
				} else {
					tokens.add(new Token(Kind.LT, startPos, 1));
					state = State.START;
				}

			}
				break;
			case AFTER_EXCLAMATION: {
				ch = chars.charAt(pos);
				if (ch == '=') {
					tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
					pos++;
					state = State.START;
				} else {
					tokens.add(new Token(Kind.NOT, startPos, 1));
					state = State.START;
				}
			}
				break;
			case AFTER_MINUS: {
				ch = chars.charAt(pos);
				if (ch == '>') {
					tokens.add(new Token(Kind.ARROW, startPos, 2));
					pos++;
					state = State.START;
				} else {
					tokens.add(new Token(Kind.MINUS, startPos, 1));
					state = State.START;
				}
			}
				break;
			case AFTER_SLASH: {
				if (pos < length && (ch = chars.charAt(pos)) == '*') {
					while (pos < length) {
						pos++;
						pos = skipWhiteSpace(pos);
						if (pos < length && chars.charAt(pos) == '*') {
							if (pos + 1 < length && chars.charAt(pos + 1) == '/') {
								pos = pos + 2;
								state = State.START;
								break;

							}
						} else if (pos >= length - 1) {
							state = State.START;
							if (pos == length - 1)
								pos++;
							break;
						}
					}

				} else {
					tokens.add(new Token(Kind.DIV, startPos, 1));
					state = State.START;
				}
			}
				break;
			default:
				assert false;
			}
		}
		return this;
	}

	/*
	 * Skips whitespaces and adds the position of new line to lineNos Arraylist
	 */
	public int skipWhiteSpace(int pos) {
		while (pos < chars.length()) {
			if (Character.isWhitespace(chars.charAt(pos))) {
				if (chars.charAt(pos) == '\n') {
					lineNos.add(pos + 1);
				}
				pos++;
			} else
				break;
		}
		return pos;
	}

	/*
	 * Does a binary search in lineNos Arraylist to find the next smaller number
	 * than the key to determine the line number
	 */
	public int search(int key) {

		int res = Arrays.binarySearch(lineNos.toArray(), key);
		int insertion_point;
		if (res < 0) {
			insertion_point = -1 * (res + 1) - 1;
			return insertion_point;
		}
		return res;
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that the
	 * next call will return the Token..
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	/*
	 * Return the next token in the token list without updating the state. (So
	 * the following call to next will return the same token.)
	 */
	public Token peek() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the
	 * given token.
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}

}
