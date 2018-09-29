package cool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import cool.AST;
import cool.GlobalError;
import cool.AST.class_;
public class InheritGraph{
    
    /* Maps class name to object reference for quick access */
    public HashMap<String,AST.class_> map;
    
    /* Checks if Input class list has a main class */
    private boolean hasMain;

    private int numClasses;
    /* Stores updated class list with updated child and parent pointers */


    public List<AST.class_> classList;
    
    /* Init Map, Update AST.class_ Nodes and Check if Main Class exists, and no class is defined twice. */
    public InheritGraph(AST.program program){
        hasMain = false;
        numClasses = program.classes.size();
        classList = program.classes;
        map = new HashMap<>();      

        // Add base Classes now. Can do some other parsing etc if needed before.
        this.addBaseClassesToGraph();
        numClasses = classList.size();

        /* Run on the classList for populating the map for obtaining parent-child references */
        // Run this from Semantic
        //populateAndLink();
        GlobalData.inheritGraph = this;
    }

    boolean inValidInheritanceGraph(){
        return !this.populateAndLink() || this.checkCycle();
    }

    private boolean populateAndLink(){
        boolean retVal = true;
        for (AST.class_ curr : classList){
            /* Class redefined */
            if(map.containsKey(curr.name)) {
                GlobalError.reportError(curr.filename, curr.lineNo, "Multiple definitions of same class: "+curr.name+"!");
                retVal=false;
            }
            else{
                map.put(curr.name, curr);
                /* Main reported*/
                if(curr.name.equals("Main")) hasMain = true;
            }
        }
        
        
        /* Main not found */
        if(!hasMain) {
            GlobalError.reportError("", 0, "No Main Class found!");
            retVal = false;
        }
        
        /* Updates the child and parent pointers */
        for( AST.class_ curr : classList){
            String parent = curr.parent;
            
            /* object class parent null exception */
            if(parent!=null){
                if(!isInheritableClass(parent)){
                    GlobalError.reportError(curr.filename, curr.lineNo, "Error: Cannot Inherit from: "+parent);
                    // Set the Parent to Object now, for further semantic checks?
                    parent = "Object";
                    retVal=false;
                }
                if(map.containsKey(parent)){
                    curr.parentClass = map.get(parent);
                    map.get(parent).children.add(curr.name);
                }
                else{ // Parent not found?
                    GlobalError.reportError(curr.filename, curr.lineNo, "Error: "+curr.name+"'s parent: "+parent+" is not defined");
                    // Make Object its parent for further semantic check
                    curr.parentClass = map.get("Object");
                    map.get("Object").children.add(curr.name);
                    retVal=false;
                }
            } // Parent != null

        } // Iterate over class list
        return retVal;
    }


    /* Checks if there is a cycle i.e. a child is a self conforming class */
    private boolean checkCycle(){
        /* Marks whether node has been seen or not */
        HashMap<String,Boolean> visited = new HashMap<>(); 
        
        /* Active recursion stack for testing a back edge */
        HashMap<String,Boolean> activeStack = new HashMap<>();

        /* Initialise very node and unvisited and recursion stack as empty */
        for( AST.class_ curr : classList){
            visited.put(curr.name,false);
            activeStack.put(curr.name,false);
        }

        /* curr is class name */
        for( String curr : visited.keySet()){
            //Checks a non visited node
            if(!visited.get(curr)) {
                if(ParseTree(curr,visited,activeStack)){
                    GlobalError.reportError(curr, map.get(curr).lineNo, "Cycle found involving class "+curr + "!");
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns whether this particular parse with curr has a cycle */
    private boolean ParseTree(String curr, HashMap<String, Boolean> visited,HashMap<String, Boolean> activeStack ){
        activeStack.put(curr,true);
        visited.put(curr,true);
        for( String currChild : map.get(curr).children ){
            /* Checks if child is unvisited and parse of its child includes this current node for back edge */
            if( !visited.get(currChild) && ParseTree(currChild, visited, activeStack)){
                return true;
            }
            else if(activeStack.get(currChild)) return true;
        }
        /* Pop current node out of stack */
        activeStack.put(curr,false);
        return false;   
    }

    /* Add classes like Object, int etc. */
    private void addBaseClassesToGraph(){
        String fn = "";
        if(!classList.isEmpty()) fn = classList.get(0).filename;
        // Create Object
        List<AST.feature> x = new ArrayList<>();
        x.add(new AST.method("abort", new ArrayList<AST.formal>(), "Object", null,0));
        x.add(new AST.method("type_name", new ArrayList<AST.formal>(), "String", null, 0));
        // Add this? Might need to change !!
        x.add(new AST.method("copy", new ArrayList<AST.formal>(), "Object", null, 0));
        // Its parent is null!
        AST.class_ obj = new AST.class_("Object", fn, null, x, 0);
        classList.add(obj);

        // Add IO()
        List<AST.feature> y = new ArrayList<>();
        List<AST.formal> gg = new ArrayList<>();
        gg.add(new AST.formal("x", "String", 0));
        y.add(new AST.method("out_string", gg, "Object", null,0));
        List<AST.formal> gg_int = new ArrayList<>();
        gg_int.add(new AST.formal("x", "Int", 0));
        y.add(new AST.method("out_int", gg_int, "Object", null, 0));
        y.add(new AST.method("in_int", new ArrayList<AST.formal>(), "Int", null, 0));
        y.add(new AST.method("in_string", new ArrayList<AST.formal>(), "String", null, 0));
        AST.class_ io = new AST.class_("IO", fn, "Object", y, 0);
        classList.add(io);

        // Add String
        List<AST.feature> z = new ArrayList<>();
        List<AST.formal> zf = new ArrayList<>();
        zf.add(new AST.formal("s", "String", 0));
        List<AST.formal> zf2 = new ArrayList<>();
        zf2.add(new AST.formal("i", "Int", 0));
        zf2.add(new AST.formal("l", "Int", 0));

        z.add(new AST.method("length", new ArrayList<AST.formal>(), "Int", null, 0));
        z.add(new AST.method("concat", zf, "String", null, 0));
        z.add(new AST.method("substr", zf2, "String", null, 0));
        AST.class_ str = new AST.class_("String", fn, "Object", z, 0);
        classList.add(str);
        // Add Int
        List<AST.feature> w = new ArrayList<>();
        AST.class_ intCl = new AST.class_("Int", fn, "Object", w, 0);
        classList.add(intCl);
        // Add Bool
        List<AST.feature> u = new ArrayList<>();
        AST.class_ boolCl = new AST.class_("Bool", fn, "Object", u, 0);
        classList.add(boolCl);
    } // addBaseClassesToGraph

    private boolean isInheritableClass(String clName){
        if(clName.equals("Int") || clName.equals("String") || clName.equals("Bool")) return false;
        return true;
    }

    /**
     * Does t exist?
     * @param t The type
     * @return true if it exists
     */
    public boolean doesTypeExist(String t){
        return map.get(t)!=null;
    }

    /**
     * Is t1 a subtype of t2? Assume both these types exist
     * @param t1 Type to be checked for subtype
     * @param t2 The type to be checked for supertype
     * @return True if t1 is a subtype of t2.
     */
    public boolean isSubType(String t1, String t2){
        do{
            if(t1.equals("Object") || t1.equals(t2)) break;
            t1 = map.get(t1).parent;
        } while(true);
        return t1.equals(t2);
    }
} // Class ends