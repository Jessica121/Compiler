package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

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
