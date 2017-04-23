package compiler.AST;

import compiler.AST.Type.TypeName;
import compiler.Scanner.Token;

public abstract class Chain extends Statement {
	public TypeName typeName;
	public boolean leftIdent;
	public boolean isLeftIdent() {
		return leftIdent;
	}

	public void setLeftIdent(boolean leftIdent) {
		this.leftIdent = leftIdent;
	}

	public Chain(Token firstToken) {
		super(firstToken);
	}

	public TypeName getTypeName() {
		return typeName;
	}

	public boolean setTypeName(TypeName type) {
		try {
			if (type != null)
				this.typeName = type;
			else
				this.typeName = Type.getTypeName(firstToken);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
