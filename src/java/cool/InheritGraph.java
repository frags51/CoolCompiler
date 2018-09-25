package cool;
import java.util.HashMap;
import java.util.List;


import cool.AST;
public class InheritGraph{
    
    /* Maps class name to object reference for quick access */
    public HashMap<String,AST.class_> map;
    
    /* Checks if Input class list has a main class */
    boolean hasMain;

    /* Stores updated class list with updated child and parent pointers */
    public List<AST.class_> classList;
    
    /* Init Map, Update AST.class_ Nodes and Check if Main Class exists, and no class is defined twice. */
    public InheritGraph(AST.program program){
        hasMain = false;
        classList = program.classes;
        map = new HashMap<>();      

        

        /* First run on the classList for populating the map for obtaining parent references */
        for (AST.class_ curr : classList){
            /* Class redefined */
            if(map.containsKey(curr.name)) GlobalError.reportError(curr.filename, curr.lineNo, "Multiple definitions of same class: "+curr.name+"!");
            else{
                map.put(curr.name, curr);
                /* Main reported*/
                if(curr.name == "Main") hasMain = true;
            }
        }
        
        
        /* Main not found */
        if(!hasMain) GlobalError.reportError("", 0, "No Main Class found!");
        
        /* Updates the child and parent pointers */
        for( AST.class_ curr : classList){
            String parent = curr.parent;
            System.out.println(curr.name);
            
            /* TODO : Add object class parent null exception */
            if(map.containsKey(parent)){
                curr.parentClass = map.get(parent);
                
                map.get(parent).children.add(curr.name);
            }
        }
    }


    /* Checks if there is a cycle i.e. a child is a self conforming class */
    public boolean checkCycle(){
        return false;
    }


}