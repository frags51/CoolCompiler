package cool;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class CodeGenVisitor implements VisitorRet {

    @Override
    public void visit(AST.ASTNode x, StringBuilder res) {

    }

    @Override
    public void visit(AST.expression x, StringBuilder res) {

    }

    @Override
    public void visit(AST.no_expr x, StringBuilder res) {
        res.setLength(0);
    }

    @Override
    public void visit(AST.string_const x, StringBuilder res){
        IRBuilder.varNumb++;
        GlobalData.out.println(new StringBuilder("%").append(IRBuilder.varNumb-1).append(" = ").append(IRBuilder.gepString(x.value)).append("\n").toString());
        res.setLength(0);
        res.append("%").append(IRBuilder.varNumb-1);
    }

    @Override
    public void visit(AST.int_const x, StringBuilder res) {
        res.append(Integer.toString(x.value));
    }

    @Override
    public void visit(AST.object x, StringBuilder res) {

    }

    @Override
    public void visit(AST.comp x, StringBuilder res) {
        StringBuilder X = new StringBuilder();
        x.e1.accept(this, X);
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(" = xor i8 1, ").append(X).append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        res.setLength(0);
        res.append("%").append(IRBuilder.varNumb-1);
    }

    /**
     * res is now:
     * %1 = icmp eq i32 %L, %R
     * %2 = zext i1 %1 to i8
     * res contains "%2"
     */
    @Override
    public void visit(AST.eq x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this, L);
        x.e2.accept(this, R);

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(" = icmp eq i32 ");
        IRBuilder.temp.append(L).append(", ").append(R).append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genZext("i1", "i8", "%"+(IRBuilder.varNumb-1)));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        res.append("%").append(IRBuilder.varNumb-1);
    }

    /**
     * res is now:
     * %1 = icmp sle i32 %L, %R
     * %2 = zext i1 %1 to i8
     * res contains "%2"
     */
    @Override
    public void visit(AST.leq x, StringBuilder res) {

        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);
        IRBuilder.createBinary(L.toString(),R.toString(),"icmp sle","i32");

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genZext("i1", "i8", "%"+(IRBuilder.varNumb-1)));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        res.append("%").append(IRBuilder.varNumb-1);
    }

    /**
     * res is now:
     * %1 = icmp slt i32 %L, %R
     * %2 = zext i1 %1 to i8
     * res contains "%2"
     */
    @Override
    public void visit(AST.lt x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this, L);
        x.e2.accept(this, R);
        IRBuilder.createBinary(L.toString(),R.toString(),"icmp slt","i32");

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genZext("i1", "i8", "%"+(IRBuilder.varNumb-1)));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        res.append("%").append(IRBuilder.varNumb-1);
    }

    // ~x == 0-x
    @Override
    public void visit(AST.neg x, StringBuilder res) {
        StringBuilder X = new StringBuilder();
        x.e1.accept(this, X);
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(" = sub i32 0, ").append(X).append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        res.setLength(0);
        res.append("%").append(IRBuilder.varNumb-1);
    }

    @Override
    public void visit(AST.divide x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);
        IRBuilder.createBinary(L.toString(),R.toString(),"div","i32");
        res.append("%").append(IRBuilder.varNumb-1);

    }

    @Override
    public void visit(AST.mul x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);
        IRBuilder.createBinary(L.toString(),R.toString(),"mul","i32");
        res.append("%").append(IRBuilder.varNumb-1);

    }

    @Override
    public void visit(AST.sub x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);
        IRBuilder.createBinary(L.toString(),R.toString(),"sub","i32");
        res.append("%").append(IRBuilder.varNumb-1);

    }

    @Override
    public void visit(AST.plus x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);
        IRBuilder.createBinary(L.toString(),R.toString(),"add","i32");
        res.append("%").append(IRBuilder.varNumb-1);

    }

    @Override
    public void visit(AST.isvoid x, StringBuilder res) {
        StringBuilder L = new StringBuilder();
        x.e1.accept(this,L);

        IRBuilder.createBinary(L.toString(),null,"icmp eq",getType(x.e1.type));
        res.append("%").append(IRBuilder.varNumb-1);
    }

    // %1 = call malloc.
    // %2 = bitcast
    // call constructor
    // add typename!
    // res = %2
    @Override
    public void visit(AST.new_ x, StringBuilder res) {
        if(x.typeid.equals("Int")) res.append("0");
        else if(x.typeid.equals("String")) ;
        else if(x.typeid.equals("Bool")) res.append("0");
        else{ // not a primitive type
            int size = GlobalData.classtoSize.get(x.typeid);
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genMalloc(size));
            IRBuilder.varNumb++;
            IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(" = bitcast i8* %").append(IRBuilder.varNumb-1);
            IRBuilder.temp.append(" to %class.").append(x.typeid).append("*\n");
            IRBuilder.varNumb++;

            res.append("%").append(IRBuilder.varNumb-1);

            // Constructor
            IRBuilder.temp.append("\tcall void @").append(GlobalData.funMangledName(x.typeid, x.typeid)).append("(%class.");
            IRBuilder.temp.append(x.typeid).append("* ").append(res).append(")\n");

            // Set TypeName
            if(x.typeid.equals("Object")){
                IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append("= getelementptr inbounds %class.").append(x.typeid);
                IRBuilder.temp.append(", %class.").append(x.typeid).append("* ").append(res).append(", i32 0, i32 0\n");
                IRBuilder.varNumb++;

                IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.gepString(x.typeid)).append("\n");
                IRBuilder.varNumb++;

                IRBuilder.temp.append("\t store i8* %").append(IRBuilder.varNumb-1).append(", i8** %").append(IRBuilder.varNumb-2);
                IRBuilder.temp.append("\n");
            }
            else{
                IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genTypCastPtr(x.typeid, "Object", res.toString()));
                IRBuilder.varNumb++;

                IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append("= getelementptr inbounds %class.").append(x.typeid);
                IRBuilder.temp.append(", %class.").append(x.typeid).append("* ").append(res).append(", i32 0, i32 0\n");
                IRBuilder.varNumb++;

                IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.gepString(x.typeid)).append("\n");
                IRBuilder.varNumb++;

                IRBuilder.temp.append("\t store i8* %").append(IRBuilder.varNumb-1).append(", i8** %").append(IRBuilder.varNumb-2);
                IRBuilder.temp.append("\n");
            }

            GlobalData.out.println(IRBuilder.temp.toString());
        }
    }

    @Override
    public void visit(AST.assign x, StringBuilder res) {
        
    }

    // res is the result of last expr
    @Override
    public void visit(AST.block x, StringBuilder res) {
        StringBuilder R = new StringBuilder();
        for(AST.expression e: x.l1){
            e.accept(this, R);
        }
        res.append(R);
    }

    @Override
    public void visit(AST.loop x, StringBuilder res) {
        String whileCondLabel = "while.cond."+IRBuilder.whileNumb;
        String whileBodyLabel = "while.body"+IRBuilder.whileNumb;
        String whileEndLabel = "while.end"+IRBuilder.whileNumb;
        IRBuilder.whileNumb++;

        StringBuilder condBuilder = new StringBuilder();
        condBuilder.append("br label %").append(whileCondLabel);
        GlobalData.out.println(thenbreakBuilder.toString());

        StringBuilder whPred;
        x.predicate.accept(this,whPred);

        /* Buzzzzz slepttttttt after this */
    }

    @Override
    public void visit(AST.cond x, StringBuilder res) {
        /* Reqd labels */
        String ifLabel = "if.then."+ IRBuilder.ifNumb;
        String elseLabel = "if.else."+IRBuilder.ifNumb;
        String endLabel = "if.end."+IRBuilder.ifNumb;
        IRBuilder.ifNumb++;

        /* Visiting predicate for obtaining branch */
        StringBuilder cmp = new StringBuilder();
        x.predicate.accept(this,cmp);
        IRBuilder.createBinary()

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genZext("i1", "i8", "%"+(IRBuilder.varNumb-1)));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());
        cmp.append("%").append(IRBuilder.varNumb-1);

        /* Make the break instruction */
        StringBuilder builder = new StringBuilder();
        builder.append("br i1 ");
        builder.append(cmp).append(", ");
        builder.append("label %").append(ifLabel);
        builder.append(", label %").append(elseLabel);
        GlobalData.out.println(builder.toString());

        //IF THEN
        StringBuilder ifbuilder = new StringBuilder();
        GlobalData.out.println("\n"+ifLabel+":");
        x.ifbody.accept(this,ifbuilder);
        // TODO : LOAD STORES
        StringBuilder thenbreakBuilder = new StringBuilder();
        thenbreakBuilder.append("br label %").append(endLabel);
        GlobalData.out.println(thenbreakBuilder.toString());

        //IF ELSE
        StringBuilder elseBuilder = new StringBuilder();
        GlobalData.out.println("\n"+elseLabel+":");
        x.elsebody.accept(this,ifbuilder);
        //TODO : LOAD STORES
        StringBuilder elsebreakBuilder = new StringBuilder();
        elsebreakBuilder.append("br label %").append(endLabel);
        GlobalData.out.println(elsebreakBuilder.toString());

        /* END LABEL */
        StringBuilder endBuilder = new StringBuilder();
        GlobalData.out.println("\n"+endLabel+":");

        /* Build result type */
        String resultType = GlobalData.inheritGraph.lCA(x.ifbody.type,x.elsebody.type);
        /* Make allocas or load/store for res */




    }

    @Override
    public void visit(AST.let x, StringBuilder res) {

    }

    @Override
    public void visit(AST.dispatch x, StringBuilder res) {

    }

    @Override
    public void visit(AST.static_dispatch x, StringBuilder res) {

    }

    @Override
    public void visit(AST.typcase x, StringBuilder res) {

    }

    @Override
    public void visit(AST.branch x, StringBuilder res) {

    }

    @Override
    public void visit(AST.formal x, StringBuilder res) {

    }

    @Override
    public void visit(AST.bool_const x, StringBuilder res) {
        res.append(x.value?"1":"0");
    }

    @Override
    public void visit(AST.feature x, StringBuilder res) {

    }

    @Override
    public void visit(AST.method x, StringBuilder res) {

    }

    @Override
    public void visit(AST.attr x, StringBuilder res) {

    }

    @Override
    public void visit(AST.class_ x, StringBuilder res) {

    }

    @Override
    public void visit(AST.program x, StringBuilder res) {
        emitGlobalStrings();
        emitCFuncs();
        updateStructSize();

        emitConstructors(null);
    }


    // Emit IR for global Strings
    private void emitGlobalStrings(){
        /**
         * Add %d, %s to the map.
         */
        GlobalData.stringConstNames.put("%d", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("%s", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;

        GlobalData.out.println("; Global String Consts");
        Set<Map.Entry<String, String>> eS = GlobalData.stringConstNames.entrySet();

        for(Map.Entry<String, String> x : eS){
            StringBuilder temp = new StringBuilder();
            temp.append(x.getValue()).append(" = private unnamed_addr constant [");
            temp.append(x.getKey().length()+1);
            temp.append(" x i8] c\"");
            temp.append(x.getKey()).append("\\00\"");
            GlobalData.out.println(temp.toString());
        }
    }


    /**
     * Emit Decls for C funcs:
     * printf(), scanf(), exit() -> abort, malloc -> new
     */
    private void emitCFuncs(){
        GlobalData.out.println("\n; C Function Declarations << Used to implement COOL Funcs");
        GlobalData.out.println("declare i32 @printf(i8*, ...)\n");
        GlobalData.out.println("declare i32 @__isoc99_scanf(i8*, ...)\n");
        GlobalData.out.println("declare noalias i8* @malloc(i64)\n");
        GlobalData.out.println("declare i8* @strcpy(i8*, i8*)\n");
        GlobalData.out.println("declare i8* @strcat(i8*, i8*)\n");
        GlobalData.out.println("declare i64 @strlen(i8*)\n");
        GlobalData.out.println("declare void @exit(i32)\n");

    }

    private void updateStructSize(){
        GlobalData.classtoSize = new HashMap<>();
        GlobalData.attrToIndex = new HashMap<>();
        AST.class_ root = GlobalData.inheritGraph.getRoot();
        updateDefaultSize();
        GlobalData.out.println();
        GlobalData.out.println("; Class Declarations\n");
        GlobalData.out.println("%class."+root.name+ " = type {i8*}");
        for(String child : root.children)
            updateStructDFS(child);
    }

    private void updateStructDFS(String curr){
        AST.class_ currClass = GlobalData.inheritGraph.map.get(curr);

        int init = 8,index = 1;


        if(curr.equals("Int") || curr.equals("String") || curr.equals("Bool")) return ;
        init+= GlobalData.classtoSize.get(currClass.parent);
        StringBuilder builder = new StringBuilder("%class."+curr);
        builder.append((" = type { ")).append("%class."+ currClass.parent);
        for(AST.feature f : currClass.features){
            if(f instanceof AST.attr){
                AST.attr a = (AST.attr) f;
                init+= getSizeForStruct(a.typeid);
                GlobalData.attrToIndex.put(GlobalData.varMangledName(a.name,curr),index++);
                builder.append(", ").append(getType(a.typeid));
            }
        }
        builder.append('}');
        GlobalData.out.println(builder.toString());
        GlobalData.classtoSize.put(curr,init);
        for(String child : currClass.children) updateStructDFS(child);


    }

    private void updateDefaultSize() {
        // These are defined manually
        GlobalData.classtoSize.put("Int", 4);
        GlobalData.classtoSize.put("Bool", 1);
        GlobalData.classtoSize.put("String", 8);
        GlobalData.classtoSize.put("Object", 0);
        GlobalData.classtoSize.put("IO", 0);
    }

    private int getSizeForStruct(String type) {
        if(type.equals("Int")) return 4;
        else if(type.equals("Bool")) return 1;
        else return 8;
    }

    public static String getType(String type) {
        if(type.equals("String")) {
            return "i8*";
        }
        else if(type.equals("Int")) {
            return "i32";
        }
        else if(type.equals("Bool")) {
            return "i8";
        }
        return "%class."+type + "*";
    }

    static void emitConstructors(List<AST.class_> classList){
        GlobalData.out.println(IRBuilder.constructorObject());
        GlobalData.out.println(IRBuilder.constructorIO());
    }
}
