package cool;



import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;


public class GlobalData{
    /**
     * Map of Scope Tables for each class.
     * Modify this data structure later.
     */
    public static cool.ScopeTable<String> scpTable = new ScopeTable<>();

    public static HashMap<String,String> nameMap = new HashMap<>();


    /**
     * FileName for error reporting.
     */
    public static String curFileName = "";
    public static String curClassName = "";


    /* For adding parent features to children */
    public static HashMap<String,AST.class_> classCopy;

    /**
     * The inheritance graph object.
     * Set in constructor of inheritgraph (Supposed to be singleton class).
     */
    public static InheritGraph inheritGraph;

    /**
     * The number used to identify a String.
     */
    static int stringNameNum = 0;

    /**
     * Maps a string const to its name used in IR. Updated in TypeCheckVisitor.
     */
    static HashMap<String, String> stringConstNames = new HashMap<>();

    // For printing from other files.
    static PrintWriter out;

    public static final String NOTYPE = "No_type";

    /**
     * Return the name of the variable mangled by the class name.
     * @return
     */

    public static String varMangledName(String varName, String className){

        return (inheritGraph.getMangledKey(className,varName,false));
    }

    public static String funMangledName(String funName, String className){
        return (inheritGraph.getMangledKey(className,funName,true));
    }

    /**
     *
     * @param funName : Mangled name of function
     * @return List of argument types
     */
    public static List<String> argTypesFromFun(String funName){
        String argList = nameMap.get(funName);

        ArrayList<String> argTypes = new ArrayList<>();
        int lengthArgList = 0,counter =0;
        for(char letter : argList.toCharArray()) {
            if (Character.isDigit(letter)) {
                lengthArgList = lengthArgList * 10;
                lengthArgList += Character.getNumericValue(letter);
                counter++;
            }
            else break;
        }
        if(lengthArgList == 0) {
            return argTypes;
        }

        int argTypeLength = 0;
        for(int i = counter+1; i< argList.length() && argList.charAt(i)!='&';i++){
            if(Character.isDigit(argList.charAt(i))){
                argTypeLength = argTypeLength * 10;
                argTypeLength += Character.getNumericValue(argList.charAt(i));
            }
            else{
                StringBuilder attributeBuilder = new StringBuilder();
                int j;
                for(j = i;j<i+argTypeLength;j++){

                    attributeBuilder.append(argList.charAt(j));
                }
                i = j-1;
                argTypes.add(attributeBuilder.toString());
                argTypeLength = 0;
            }
        }
        return argTypes ;

    }


    public static String getReturnType(String mangledName){
        String mapped = nameMap.get(mangledName);
        if(mapped==null) return null;
        for(int i=0;i<mapped.length();i++){
            if(mapped.charAt(i) == '&'){
                return mapped.substring(i+1);
            }
        }
        // will never be executed hopefi;ly

        return null;
    }
}
