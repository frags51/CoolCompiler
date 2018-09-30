package cool;

public class Semantic{
	private boolean errorFlag = false;
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/
	public Semantic(AST.program program){
		//Write Semantic analyzer code here

		Visitor declVisitor = new VisitorImpl();
		program.accept(declVisitor);
		Visitor typeChecker = new TypeCheckVisitor();
		program.accept(typeChecker);
		// NEED TO SET ERROR FLAG HERE!
		this.setErrorFlag();
	}

	private void setErrorFlag() {
		if(GlobalError.getErrorFlag()) this.errorFlag=true;
	}
}
