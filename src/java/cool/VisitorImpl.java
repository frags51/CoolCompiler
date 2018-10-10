package cool;
import java.util.HashMap;
import java.util.HashSet;

//import jdk.nashorn.internal.objects.Global;

import java.util.ArrayList;
public class VisitorImpl implements Visitor {
    public InheritGraph graph;
    public HashSet<String> methodSet;

    public void visit(AST.program program){
        graph = new InheritGraph(program);
        if(graph.inValidInheritanceGraph()){
            GlobalError.invalidIGraph=true;
			return;
		}
		GlobalData.classCopy = new HashMap<>();
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
        graph.mangleNames(currClass.name);
        currClass.accept(this);

        for( String child : currClass.children){
            for(AST.feature ftr : currClass.features){
                /* Checks whether to change return type or not*/
                boolean flag = true;
                if(ftr instanceof AST.method){
                    /* Handle self types in case of IO method calls*/
                    AST.method mthd = (AST.method) ftr;
                    if(mthd.name.equals("copy") || mthd.name.equals("out_string") || mthd.name.equals("out_int")){
                        //((AST.method)ftr).typeid = c;
                        //if(GlobalError.DBG) System.out.println("ADDED>>"+mthd.name);
                        AST.method selfCopy = new AST.method(mthd);
                        selfCopy.typeid = child;
                        GlobalData.classCopy.get(child).features.add(selfCopy);
                        flag = false;
                    }
                }
                if(flag) GlobalData.classCopy.get(child).features.add(ftr);
            }
            classDFS(graph.map.get(child));
        }
    }

    /*Implementation of visit class */
    public void visit(AST.class_ clas){
        GlobalData.curClassName=clas.name;
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
                            GlobalError.reportError(GlobalData.curFileName, clas.lineNo, "ERROR: Multiple main() functions found!");
                        }
                    }
                }
            }
        if (hasMainFunc == true) {
            if(clas.name.equals("Main")) {}
            else {
                /*THROW ERROR*/
                GlobalError.reportError(GlobalData.curFileName, clas.lineNo, "ERROR: main() function found in class " + clas.name + "!");
            }
        }
        else if(clas.name.equals("Main")){
            /*REPORT NO MAIN IN MAIN CLASS*/
            GlobalError.reportError(GlobalData.curFileName, clas.lineNo, "ERROR: No main() in Main class!");
        }

        /*
        for(AST.feature f : clas.features){
            f.accept(this);
        }
        */

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
                if(i==0){
                    /* REPORT LOCAL REDEFINITION ERROR */
                    GlobalError.reportError(GlobalData.curFileName, attr.lineNo, "ERROR: Multiple definitions of "+attr.name+"!");
                }
                else {/* REPORT PARENT CLASH ERROR */}
                GlobalError.reportError(GlobalData.curFileName, graph.map.get(GlobalData.curClassName).lineNo, "ERROR: Attribute: " + attr.name + " redefined in inherited class!");
            }
        }
        else{
            /* REPORT TYPE UNDEFINED ERROR */
            GlobalError.reportError(GlobalData.curFileName, attr.lineNo, "ERROR: Type "+attr.typeid+" is not defined!");
        }
    }



    public void checkMethod(AST.method method,int i){
        if(graph.hasClass(method.typeid)) {
            String key = graph.getMangledKey(GlobalData.curClassName, method.name, true);
            if(GlobalData.scpTable.lookUpGlobal(key) != null){
                if(i==0){

                    //ERROR LOCAL DEFINITION REPEAT
                    GlobalError.reportError(GlobalData.curFileName, method.lineNo, "ERROR: Multiple definitions of "+method.name+"!");
                }
                else if(GlobalData.nameMap.get(key).equals(GlobalData.scpTable.lookUpGlobal(key))){
                    //ALL OK REDEFINTIONS
                    // NOT overwriting to symbol table as parent is added later so it need not overwrite child method

                }
                else{
                    //ERROR INCORRECT DEFINITIONS
                    GlobalError.reportError(GlobalData.curFileName, method.lineNo, "ERROR: incorrect override of "+method.name+" in inherited class!");
                }
            }
            else{
                //if(GlobalError.DBG) System.out.println("CHECK >> "+key+" | "+GlobalData.nameMap.get(key));
                GlobalData.scpTable.insert(key, GlobalData.nameMap.get(key));
            }
        }

        else{
            //REPORT TYPE MISMATCH
            GlobalError.reportError(GlobalData.curFileName, method.lineNo, "ERROR: Multiple definitions of "+method.name+"!");
        }
    }

    @Override
    public void visit(AST.ASTNode x) {

    }

    @Override
    public void visit(AST.expression x) {

    }

    @Override
    public void visit(AST.no_expr x) {

    }

    @Override
    public void visit(AST.string_const x) {

    }

    @Override
    public void visit(AST.int_const x) {

    }

    @Override
    public void visit(AST.object x) {

    }

    @Override
    public void visit(AST.comp x) {

    }

    @Override
    public void visit(AST.eq x) {

    }

    @Override
    public void visit(AST.leq x) {

    }

    @Override
    public void visit(AST.lt x) {

    }

    @Override
    public void visit(AST.neg x) {

    }

    @Override
    public void visit(AST.divide x) {

    }

    @Override
    public void visit(AST.mul x) {

    }

    @Override
    public void visit(AST.sub x) {

    }

    @Override
    public void visit(AST.plus x) {

    }

    @Override
    public void visit(AST.isvoid x) {

    }

    @Override
    public void visit(AST.new_ x) {

    }

    @Override
    public void visit(AST.assign x) {

    }

    @Override
    public void visit(AST.block x) {

    }

    @Override
    public void visit(AST.loop x) {

    }

    @Override
    public void visit(AST.cond x) {

    }

    @Override
    public void visit(AST.let x) {

    }

    @Override
    public void visit(AST.dispatch x) {

    }

    @Override
    public void visit(AST.static_dispatch x) {

    }

    @Override
    public void visit(AST.typcase x) {

    }

    @Override
    public void visit(AST.branch x) {

    }

    public void visit(AST.formal form) {


    }

    @Override
    public void visit(AST.bool_const x) {

    }

    }
