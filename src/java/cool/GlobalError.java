// For ease in error reporting, just call these functions from anywhere.

package cool;

public class GlobalError{
	final public static boolean DBG = true;

	public  static boolean invalidIGraph=false;

	private static boolean errorFlag = false;
	public static void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}

	public static boolean getErrorFlag(){
		return errorFlag;
	}
}