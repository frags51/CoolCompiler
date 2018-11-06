package cool;

class IRBuilder {
    // Name prefix for Global String Consts.
    final static String strGlobal = "@.str_";

    // For variable/label naming etc
    static int varNumb = 0;
    static int ifNumb = 1;
    static int whileNumb = 1;

    static StringBuilder temp = new StringBuilder();

    static int nextVarNumb(){
        varNumb++;
        return varNumb-1;
    }

    static String gepString(String g) {
        StringBuilder temp = new StringBuilder();
        temp.append("getelementptr inbounds [").append(g.length() + 1).append(" x i8]");
        temp.append(", [").append(g.length() + 1).append(" x i8]* ").append(GlobalData.stringConstNames.get(g));
        temp.append(", i32 0, i32 0");
        return temp.toString();
    }

    static String constructorClass(AST.class_ cls) {
        StringBuilder tmp = new StringBuilder();

        return tmp.toString();
    }

    /**
     * Constructor for object. Does nothing.
     *
     * @return IR for Object Constructor.
     */
    static String constructorObject() {
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
     *
     * @return IR for Object Constructor.
     */
    static String constructorIO() {
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

    static String genZext(String t1, String t2, String vName) {
        return " = zext " + t1 + " " + vName + " to " + t2;
    }
    static String genTrunc(String t1, String t2, String vName) {
        return " = trunc " + t1 + " " + vName + " to " + t2;
    }

    static String genMalloc(long size){
        return " = call noalias i8* @malloc(i64 "+size+")\n";
    }

    // vname contains % here
    // t1 vname to t2
    static String genTypCastPtr(String t1, String t2, String vName){
        if(GlobalData.isPrimitive(t1)){ // is primitive t1 Will alwways be typecasted to Object
            StringBuilder lTemp = new StringBuilder("\t");
            String tpRes = "%dmytpcast"+GlobalData.stringNameNum;
            lTemp.append(tpRes).append(IRBuilder.genMalloc(GlobalData.classtoSize.get(t2)));
            GlobalData.stringNameNum++;
            GlobalData.out.println(lTemp);
            return " = bitcast i8* " +" " + tpRes+" to "+llvmTypeName(t2)+"\n";
        }
        return " = bitcast "+ llvmTypeName(t1) +" " + vName+" to "+llvmTypeName(t2)+"\n";

        //return " = bitcast %class."+t1+"* " + vName+" to %class."+t2+"*\n";
    }
    static String getIR(AST.bool_const x) {
        return x.value ? "1" : "0";
    }

    static void createBinary(String L, String R, String op,String type) {
        temp.setLength(0);
        temp.append("\t%").append(varNumb).append(" = ").append(op).append(" ").append(type).append(" ");
        temp.append(L).append(", ").append(R).append("\n");
        varNumb++;
        GlobalData.out.println(temp.toString());
    }

    static String llvmTypeName(String typ){
        if(typ.equals("Int")) return "i32";
        if(typ.equals("Bool")) return "i8";
        if(typ.equals("String")) return "i8*";
        return "%class."+typ+"*";
    }
    static String llvmTypeNameNotPtr(String typ){
        if(typ.equals("Int")) return "i32";
        if(typ.equals("Bool")) return "i8";
        if(typ.equals("String")) return "i8*";
        return "%class."+typ;
    }
}