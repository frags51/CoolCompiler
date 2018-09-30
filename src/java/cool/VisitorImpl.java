package cool;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
public class VisitorImpl implements Visitor {
    public InheritGraph graph;
    public HashSet<String> methodSet;

    public void visit(AST.program program){
        graph = new InheritGraph(program);
        if(graph.inValidInheritanceGraph()){
		    // Do NO MORE SEMANTIC Analysis
			return;
		}
		GlobalData.classCopy = new HashMap<>();
        graph.mangleNames();
		for(AST.class_ curr: graph.classList){
		    GlobalData.classCopy.put(curr.name, new AST.class_(curr));
        }

		/* DFS starting off from Object */
		AST.class_ root = graph.getRoot();

		/* DFS over child classes */
        classDFS(root);
    }

    /*Preorder DFS for updating parent first*/
    public void classDFS(AST.class_ currClass){
        currClass.accept(this);

        for( String child : currClass.children){
            for(AST.feature ftr : currClass.features){
                GlobalData.classCopy.get(child).features.add(ftr);
            }
            classDFS(graph.map.get(child));
        }
    }

    /*Implementation of visit class */
    public void visit(AST.class_ clas){
        // Visit and Add in scope table
        for(int i = 0;i<2;i++) {
            /** Done twice to check for local same signatures in first run and report errors if found same.
             *  On second run parent features are mangled with child class and checked if any argument mismatches
              */
            methodSet = new HashSet<>();
            for (AST.feature ftr : (i==0)? clas.features : GlobalData.classCopy.get(clas.name).features) {
                if (ftr instanceof AST.attr) {
                    /* Check rules for attribute */
                    AST.attr atr = (AST.attr) ftr;
                    checkAttribute(atr,i);
                } else {
                    /* Check rules for method */
                    AST.method method = (AST.method) ftr;
                    checkMethod(method,i);
                }

            }
        }


        boolean hasMainFunc = false;
            for (AST.feature ftr : clas.features) {
                if (ftr instanceof AST.attr) {
                    AST.attr atr = (AST.attr) ftr;
                    if (atr.name.equals("main")) {
                        //REPORT ERROR
                    }
                } else {
                    AST.method mthd = (AST.method) ftr;
                    if (mthd.name.equals("main") ) {
                        if(hasMainFunc==false) hasMainFunc = true;
                        else{
                            //THROW ERROR MULTIPLE MAIN FOUND
                        }
                    }
                }
            }
        if (hasMainFunc == true) {
            if(clas.name.equals("Main")) {}
            else {
            /*THROW ERROR*/}
        }
        else if(clas.name.equals("Main"){/*REPORT NO MAIN IN MAIN CLASS*/}

        for(AST.feature f : clas.features){
            f.accept(this);
        }

    }


    public void visit(AST.attr attr){






    }


    @Override
    public void visit(AST.feature x) {

    }

    public void visit(AST.method method){







    }


    public void checkAttribute(AST.attr attr,int i){
        if(graph.hasClass(attr.typeid)) {
            /* Return type exists */

            if (GlobalData.scpTable.lookUpLocal(GlobalData.nameMap.get(graph.getMangledKey(GlobalData.curClassName, attr.name, false))) == null) {
                /* All clear */
                String key = graph.getMangledKey(GlobalData.curClassName, attr.name, false);
                GlobalData.scpTable.insert(key, GlobalData.nameMap.get(key));
            } else {
                // already defined
                if(i==0){/* REPORT LOCAL REDEFINITION ERROR */}
                else {/* REPORT PARENT CLASH ERROR */}
                GlobalError.reportError(GlobalData.curFileName, graph.map.get(GlobalData.curClassName).lineNo, " Attribute refifintion");
            }
        }
        else{
            /* REPORT TYPE UNDEFINED ERROR */
        }
    }



    public void checkMethod(AST.method method,int i){
        if(graph.hasClass(method.typeid)) {
            String key = graph.getMangledKey(GlobalData.curClassName, method.name, true);
            if(GlobalData.scpTable.lookUpGlobal(key) != null){
                if(i==0){

                    //ERROR LOCAL DEFINITION REPEAT
                }
                else if(GlobalData.nameMap.get(key).equals(GlobalData.scpTable.lookUpGlobal(key))){
                    //ALL OK REDEFINTIONS
                    // NOT overwriting to symbol table as parent is added later so it need not overwrite child method
                }
                else{
                    //ERROR INCORRECT DEFINITIONS
                }
            }
            else{
                GlobalData.scpTable.insert(key, GlobalData.nameMap.get(key));
            }
        }

        else{
            //REPORT TYPE MISMATCH
        }
    }

    public void visit(AST.formal form) {

        if (!GlobalData.inheritGraph.hasClass(form.typeid)) {
            // using undefined type
            //REPORT ERROR
        } else {
            // valid type
            GlobalData.scpTable.insert(form.name, form.typeid);
        }
    }
























    }
