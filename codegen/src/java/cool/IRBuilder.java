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

}
