package cool;

public class GlobalData{
    /**
     * Map of Scope Tables for each class.
     * Modify this data structure later.
     */
    public static cool.ScopeTable<String> scpTable = new ScopeTable<>();

    /**
     * FileName for error reporting.
     */
    public static String curFileName = "";
    public static String curClassName = "";
    /**
     * The inheritance graph object.
     * Set in constructor of inheritgraph (Supposed to be singleton class).
     */
    public static InheritGraph inheritGraph;

    /**
     * Return the name of the variable mangled by the class name.
     * @return
     */
    public static String varMangledName(String varName, String ClassName){
        return varName;
    }

}