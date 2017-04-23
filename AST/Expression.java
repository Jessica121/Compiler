package compiler.AST;

import compiler.Scanner.Token;
import compiler.AST.Type.TypeName;

public abstract class Expression extends ASTNode {
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

	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
