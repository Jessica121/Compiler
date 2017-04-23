package compiler.AST;

import compiler.AST.Type.TypeName;
import compiler.Scanner.Token;

public class IdentLValue extends ASTNode {
	public Dec dec;
	
	public Dec getDec() {
		return dec;
	}

	public void setDec(Dec dec) {
		this.dec = dec;
	}

	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}
	public TypeName typeName;
	public TypeName getTypeName() {
		return typeName;
	}

	public boolean setTypeName(TypeName type)
	{
		try
		{
			if(type != null)			
				this.typeName = type;
			else
				this.typeName = Type.getTypeName(firstToken);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

}
