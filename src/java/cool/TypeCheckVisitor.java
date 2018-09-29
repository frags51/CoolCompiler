package cool;

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

    @Override
    public void visit(AST.formal x) {

    }

    @Override
    public void visit(AST.feature x) {

    }

    @Override
    public void visit(AST.method x) {

    }

    @Override
    public void visit(AST.attr x) {

    }

    @Override
    public void visit(AST.class_ x) {
        GlobalData.curFileName = x.filename;
    }

    @Override
    public void visit(AST.program x) {

    }
}
