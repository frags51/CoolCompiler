package cool;

import java.util.Map;
import java.util.Set;

public class CodeGenVisitor implements Visitor {
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

    @Override
    public void visit(AST.formal x) {

    }

    @Override
    public void visit(AST.bool_const x) {

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

    }

    @Override
    public void visit(AST.program x) {
        emitGlobalStrings();
        emitCFuncs();
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
}
