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
    boolean hasMain;

    int numClasses;
    /* Stores updated class list with updated child and parent pointers */


    public List<AST.class_> classList;
    
    /* Init Map, Update AST.class_ Nodes and Check if Main Class exists, and no class is defined twice. */
    public InheritGraph(AST.program program){
        hasMain = false;
        numClasses = program.classes.size();
        classList = program.classes;
        map = new HashMap<>();      

        

        /* First run on the classList for populating the map for obtaining parent references */
        for (AST.class_ curr : classList){
            /* Class redefined */
            if(map.containsKey(curr.name)) GlobalError.reportError(curr.filename, curr.lineNo, "Multiple definitions of same class: "+curr.name+"!");
            else{
                map.put(curr.name, curr);
                /* Main reported*/
                if(curr.name.equals("Main")) hasMain = true;
            }
        }
        
        
        /* Main not found */
        if(!hasMain) GlobalError.reportError("", 0, "No Main Class found!");
        
        /* Updates the child and parent pointers */
        for( AST.class_ curr : classList){
            String parent = curr.parent;
            
            /* TODO : Add object class parent null exception */
            if(map.containsKey(parent)){
                curr.parentClass = map.get(parent);
                
                map.get(parent).children.add(curr.name);
            }
        }

        boolean foundCycle = CheckCycle();
        
    }


    /* Checks if there is a cycle i.e. a child is a self conforming class */
    public boolean CheckCycle(){
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
            if(visited.get(curr) == false) {
                if(ParseTree(curr,visited,activeStack)){
                    GlobalError.reportError(curr, map.get(curr).lineNo, "Cycle found involving class"+curr);
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns whether this particular parse with curr has a cycle */
    private boolean ParseTree(String curr, HashMap<String, Boolean> visited,HashMap<String, Boolean> activeStack ){
        activeStack.put(curr,true);
        for( String currChild : map.get(curr).children ){
            /* Checks if child is unvisited and parse of it's child includes this current node for back edge */
            if( visited.get(currChild) == false && ParseTree(currChild, visited, activeStack)){
                return true;
            }
            else if(activeStack.get(currChild) ==true) return true;
        }
        activeStack.put(curr,false);
        return false;
        
    }


}