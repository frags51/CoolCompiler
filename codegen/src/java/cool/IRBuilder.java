package cool;

class IRBuilder {
    // Name prefix for Global String Consts.
    final static String strGlobal = "@.str_";

    // For variable/label naming etc
    static int varNumb = 0;
    static int ifNumb = 0;

    private static StringBuilder temp = new StringBuilder();

    static String constructorClass(AST.class_ cls){
        StringBuilder tmp = new StringBuilder();

        return tmp.toString();
    }

    /**
     * Constructor for object. Does nothing.
     * @return IR for Object Constructor.
     */
    static String constructorObject(){
        temp.setLength(0);
        temp.append("\n; #### CONSTR for Object\n");
        temp.append("define void @").append(GlobalData.funMangledName("Object", "Object"));
        temp.append("(%class.Object* %this){\n").append("entry:\n");
        temp.append("\t ret void");
        temp.append("\n}");
        return temp.toString();
    }

    /**
     * Constructor for object. Does nothing.
     * @return IR for Object Constructor.
     */
    static String constructorIO(){
        temp.setLength(0);
        temp.append("\n; #### CONSTR for IO\n");
        temp.append("define void @").append(GlobalData.funMangledName("IO", "IO"));
        temp.append("(%class.IO* %this){\n").append("entry:\n");
        temp.append("\t %0 = bitcast %class.IO* %this to %class.Object*\n");
        temp.append("\t call void @").append(GlobalData.funMangledName("Object", "Object")).append("(%class.Object* %0)");
        temp.append("\n\t ret void");
        temp.append("\n}");
        return temp.toString();
    }


    static String getIR(AST.no_expr x) {
        return null;
    }

    static String getIR(AST.string_const x) {
        return null;
    }

    static String getIR(AST.int_const x) {
        return null;
    }

    static String getIR(AST.object x) {
        return null;
    }

    static String getIR(AST.comp x) {
        return null;
    }

    static String getIR(AST.eq x) {
        return null;
    }

    static String getIR(AST.leq x) {
        return null;
    }

    static String getIR(AST.lt x) {
        return null;
    }

    static String getIR(AST.neg x) {
        return null;
    }

    static String getIR(AST.divide x) {
        return null;
    }

    static String getIR(AST.mul x) {
        return null;
    }

    static String getIR(AST.sub x) {
        return null;
    }

    static String getIR(AST.plus x) {
        return null;
    }

    static String getIR(AST.isvoid x) {
        return null;
    }

    static String getIR(AST.new_ x) {
        return null;
    }

    static String getIR(AST.assign x) {
        return null;
    }

    static String getIR(AST.block x) {
        return null;
    }

    static String getIR(AST.loop x) {
        return null;
    }

    static String getIR(AST.cond x) {
        return null;
    }

    static String getIR(AST.let x) {
        return null;
    }

    static String getIR(AST.dispatch x) {
        return null;
    }

    static String getIR(AST.static_dispatch x) {
        return null;
    }

    static String getIR(AST.typcase x) {
        return null;
    }

    static String getIR(AST.branch x) {
        return null;
    }

    static String getIR(AST.bool_const x) {
        return null;
    }
}
