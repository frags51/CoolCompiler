package cool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeCheckVisitor implements Visitor{
    @Override
    public void visit(AST.ASTNode x) {
        if(GlobalError.DBG) GlobalError.reportError("", 0, "TypeCHecker visiter called on ASTnode");
    }

    @Override
    public void visit(AST.expression x) {
        if(GlobalError.DBG) GlobalError.reportError("", 0, "TypeCHecker visiter called on expression");
    }

    @Override
    public void visit(AST.no_expr x) {
        ; // Do nothing for this type check.
    }

    @Override
    public void visit(AST.string_const x) {
        x.type="String";
    }

    @Override
    public void visit(AST.bool_const x) {
        x.type="Bool";
    }

    @Override
    public void visit(AST.int_const x) {
        x.type="Int";
    }

    @Override
    public void visit(AST.object x) {
        String typGot = GlobalData.scpTable.lookUpGlobal(GlobalData.varMangledName(x.name, GlobalData.curClassName));
        if(typGot==null) {
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: "+x.name+" not declared!");
            x.type= "Object";
        }
        else x.type = typGot;
    }

    /**
     * The complement expression
     * @param x : X's inner expression (~expression) Must have static type Int
     */
    @Override
    public void visit(AST.comp x) {
        x.e1.accept(this); // Generate type for its subexpression, which must be int
        if(!x.e1.type.equals("Int")) {
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: "+": Complement is defined only on Ints!");
            x.type="Int"; // Continue Checks
        }
        else x.type="Int";
    }

    /**
     * a = b
     * Can compare Int with only Int, Bool with only Bool, String with only String, rest any object with any object.
     * @param x
     */
    @Override
    public void visit(AST.eq x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if( (t1.equals("Int") ^ t2.equals("Int")) || (t1.equals("Bool") ^ t2.equals("Bool"))
                || (t1.equals("String") ^ t2.equals("String"))){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '=' Comparison b/w "+t1+" and "
                   + t2 + "!");
            x.type = "Bool";
        }
        else{
            x.type="Bool";
        }

    }

    /**
     * a <= b
     * @param x
     */
    @Override
    public void visit(AST.leq x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '<=' Comparison b/w "+t1+" and "+t2+"!");
            x.type="Bool";
        }
        else x.type="Bool";
    }

    /**
     * a < b
     * @param x
     */
    @Override
    public void visit(AST.lt x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '=' Comparison b/w "+t1+" and "+t2+"!");
            x.type="Bool";
        }
        else x.type="Bool";
    }

    /**
     * not a
     * @param x
     */
    @Override
    public void visit(AST.neg x) {
        x.e1.accept(this);
        String t1 = x.e1.type;
        if(!t1.equals("Bool")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid Negation of type: "+t1+"!");
            x.type="Bool";
        }
        else x.type="Bool";
    }

    @Override
    public void visit(AST.divide x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '/' Operation b/w "+t1+" and "+t2+"!");
            x.type="Int";
        }
        else x.type="Int";
    }

    @Override
    public void visit(AST.mul x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '*' Operation b/w "+t1+" and "+t2+"!");
            x.type="Int";
        }
        else x.type="Int";
    }

    @Override
    public void visit(AST.sub x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '-' Operation b/w "+t1+" and "+t2+"!");
            x.type="Int";
        }
        else x.type="Int";
    }

    @Override
    public void visit(AST.plus x) {
        x.e1.accept(this);
        x.e2.accept(this);
        String t1 = x.e1.type;
        String t2 = x.e2.type;
        if(!t1.equals("Int") || !t2.equals("Int")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid '+' Operation b/w "+t1+" and "+t2+"!");
            x.type="Int";
        }
        else x.type="Int";
    }

    /**
     * Just type check its expression. This is a Bool.
     * @param x
     */
    @Override
    public void visit(AST.isvoid x) {
        x.e1.accept(this);
        x.type="Bool";
    }

    /**
     * x.typeID should be checked for in the symbol table.
     * @param x
     */
    @Override
    public void visit(AST.new_ x) {
        boolean exists = GlobalData.inheritGraph.doesTypeExist(x.typeid);
        if(!exists) {
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid new expr. Type "+x.typeid+" does not exist!");
            x.type = "Object";
        }
        else x.type = x.typeid;
    }

    /**
     * x.name <- x.e1
     * Change to Mangled NAME!!
     * @param x
     */
    @Override
    public void visit(AST.assign x) {
        String t1 = GlobalData.scpTable.lookUpGlobal(GlobalData.varMangledName(x.name, GlobalData.curClassName));
        if(t1 == null){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Declaration of "+x.name+" not found!");
            x.type="Object";
            // Type check this expression though.
            x.e1.accept(this);
        }
        else{ //t1!=null
            x.e1.accept(this);
            if(!GlobalData.inheritGraph.isSubType(x.e1.type, t1)){
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Type "+x.e1.type+" can't be assigned to variable" +
                        "of type: "+t1+"!");
                x.type = "Object";
            }
            else x.type = x.e1.type;
        }
    }

    /**
     * Type is that of last expression in this list.
     * @param x
     */
    @Override
    public void visit(AST.block x) {
        for(AST.expression e : x.l1){
            e.accept(this);
        }
        x.type = x.l1.get(x.l1.size()-1).type;
    }

    @Override
    public void visit(AST.loop x) {
        x.predicate.accept(this);
        if(!x.predicate.type.equals("Bool")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Predicate of loop must be boolean!");
        }
        x.body.accept(this);
        x.type="Object";
    }

    @Override
    public void visit(AST.cond x) {
        x.predicate.accept(this);
        if(!x.predicate.type.equals("Bool")){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Predicate of loop must be boolean!");
        }
        x.ifbody.accept(this);
        x.elsebody.accept(this);
        x.type=GlobalData.inheritGraph.lCA(x.ifbody.type, x.elsebody.type);
    }

    /**
     * let x.name : x.typeid (<- x.value) in x.body
     * Checks:
     * - x.typeid exists
     * - if assignment then it is subtype of x.typeid
     */
    @Override
    public void visit(AST.let x) {
        x.value.accept(this);
        GlobalData.scpTable.enterScope();
        if(!GlobalData.inheritGraph.doesTypeExist(x.typeid)) {
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: TypeID: "+x.typeid+" doesn't exist!");
            x.typeid="Object";
        }
        GlobalData.scpTable.insert(GlobalData.varMangledName(x.name, GlobalData.curClassName), x.typeid);
        if(!x.value.type.equals(GlobalData.NOTYPE)) {
            if (!GlobalData.inheritGraph.isSubType(x.value.type, x.typeid)) {
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid assignment from " +
                        x.value.type + " to " + x.typeid + " !");
            }
        }
        x.body.accept(this);
        x.type = x.body.type;

        GlobalData.scpTable.exitScope();
    }

    @Override
    public void visit(AST.dispatch x) {
        x.caller.accept(this);

        String callerType = x.caller.type;
        if(GlobalError.DBG) System.out.println("ADDED>>"+callerType+" | "+GlobalData.funMangledName(x.name, callerType));

        String fRetType = GlobalData.getReturnType(GlobalData.funMangledName(x.name, callerType));
        if(fRetType==null){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Function "+x.name+" not declared!");
            x.type = "Object";
            return;

        }
        List<String> argTypes = GlobalData.argTypesFromFun(GlobalData.funMangledName(x.name, callerType));

        Iterator<AST.expression> actIt = x.actuals.iterator();
        Iterator<String> typIt = argTypes.iterator();
        AST.expression nextExpr;
        String nextTyp;

        while(actIt.hasNext() && typIt.hasNext()){
            nextExpr = actIt.next();
            nextExpr.accept(this);
            nextTyp = typIt.next();
            if(!GlobalData.inheritGraph.isSubType(nextExpr.type, nextTyp)){
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Expected arg of type: "+nextTyp +"" +
                        " but got: " + nextExpr.type );
            }
        }

        if(x.actuals.size()!=argTypes.size()) GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: " +
                "Mismatch in number of arguments to "+x.name);

        x.type = fRetType;
    }

    @Override
    public void visit(AST.static_dispatch x) {
        x.caller.accept(this);
        String callerType = x.typeid;
        if(!GlobalData.inheritGraph.isSubType(x.caller.type, x.typeid)){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Incorrect typing in static dispatch" +
                    ": "+x.caller.type+" does not conform to "+x.typeid);
            x.type="Object";
            return;
        }
        String fRetType = GlobalData.getReturnType(GlobalData.funMangledName(x.name, callerType));
        if(fRetType==null){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Function "+x.name+" not declared!");
            x.type = "Object";
            return;

        }
        List<String> argTypes = GlobalData.argTypesFromFun(GlobalData.funMangledName(x.name, callerType));

        Iterator<AST.expression> actIt = x.actuals.iterator();
        Iterator<String> typIt = argTypes.iterator();
        AST.expression nextExpr;
        String nextTyp;

        while(actIt.hasNext() && typIt.hasNext()){
            nextExpr = actIt.next();
            nextExpr.accept(this);
            nextTyp = typIt.next();
            if(!GlobalData.inheritGraph.isSubType(nextExpr.type, nextTyp)){
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Expected arg of type: "+nextTyp +"" +
                        " but got: " + nextExpr.type );
            }
        }
        if(actIt.hasNext() || typIt.hasNext()) GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: " +
                "Mismatch in number of arguments to "+x.name);

        x.type = fRetType;
    }

    @Override
    public void visit(AST.typcase x) {
        x.predicate.accept(this);
        x.branches.get(0).accept(this);
        String t1 = x.branches.get(0).type;

        List<String> typesFound = new ArrayList<>();
        AST.branch br;
        Iterator<AST.branch> branchIterator = x.branches.iterator();
        for(; branchIterator.hasNext(); ){
            br = branchIterator.next();
            if(typesFound.contains(br.type)){
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Duplicate Variable Type: "+br.type+" " +
                        "found in case statement!");
            }
            br.accept(this);
            typesFound.add(br.type);
            t1 = GlobalData.inheritGraph.lCA(t1, br.value.type);
        }
        x.type=t1;
    }

    @Override
    public void visit(AST.branch x) {
        GlobalData.scpTable.enterScope();
        if(!GlobalData.inheritGraph.doesTypeExist(x.type)){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Type "+ x.type+" does not exist!");
            x.type="Object";
        }
        GlobalData.scpTable.insert(x.name, x.type);
        x.value.accept(this);
        GlobalData.scpTable.exitScope();
    }

    /**
     * Just insert all these into the symbol table
     * @param x The formal argument
     */
    @Override
    public void visit(AST.formal x) {
        if(!GlobalData.inheritGraph.doesTypeExist(x.typeid)){
            GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Type "+ x.typeid+" doesn't exist!");
            x.typeid="Object";
        }
        GlobalData.scpTable.insert(GlobalData.varMangledName(x.name, GlobalData.curClassName), x.typeid);
    }

    /**
     * Technically this should never be called.
     */
    @Override
    public void visit(AST.feature x) {
        if(GlobalError.DBG) GlobalError.reportError(GlobalData.curFileName, x.lineNo, "DEBUG: TypeCheckVisit on feature!");
    }

    @Override
    public void visit(AST.method x) {
        GlobalData.scpTable.enterScope();

        // Add these into the symbol table
        for(AST.formal formal : x.formals){
            formal.accept(this);
        }
        // body of out_string etc is null here
        if(x.body!=null) x.body.accept(this);
        GlobalData.scpTable.exitScope();

    }

    /**
     * The attr is already declared. Now this is as good as an assign expression.
     */
    @Override
    public void visit(AST.attr x) {
        x.value.accept(this);
        if(!x.value.type.equals(GlobalData.NOTYPE)) {
            if (!GlobalData.inheritGraph.isSubType(x.value.type, x.typeid)) {
                GlobalError.reportError(GlobalData.curFileName, x.lineNo, "ERROR: Invalid assignment from " +
                        x.value.type + " to " + x.typeid + " !");
            }
            // else assign?
        }
    }

    @Override
    public void visit(AST.class_ x) {
        GlobalData.curFileName = x.filename;
        GlobalData.curClassName = x.name;
        //if(GlobalError.DBG) System.out.println("In class: "+x.name);
        GlobalData.scpTable.insert(GlobalData.varMangledName("self", GlobalData.curClassName), x.name);

        // Evaluate each feature
        for (AST.feature blah :
                x.features) {
            blah.accept(this);
        }

        GlobalData.scpTable.removeKey(GlobalData.varMangledName("self", GlobalData.curClassName));
    }

    @Override
    public void visit(AST.program x) {
        for (AST.class_ blah :
                x.classes) {
            blah.accept(this);
        }
    }
}
