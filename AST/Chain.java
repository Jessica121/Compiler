package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public abstract class Chain extends Statement {
	public TypeName typeName;

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
