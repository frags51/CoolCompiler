package cool;

public interface Visitor {
    /*
    void visit(AST.ASTNode x);
    void visit(AST.expression x);
    void visit(AST.no_expr x);
    void visit(AST.string_const x);
    void visit(AST.int_const x);
    void visit(AST.object x);
    void visit(AST.comp x);
    void visit(AST.eq x);
    void visit(AST.leq x);
    void visit(AST.lt x);
    void visit(AST.neg x);
    void visit(AST.divide x);
    void visit(AST.mul x);
    void visit(AST.sub x);
    void visit(AST.plus x);
    void visit(AST.isvoid x);
    void visit(AST.new_ x);
    void visit(AST.assign x);
    void visit(AST.block x);
    void visit(AST.loop x);
    void visit(AST.cond x);
    void visit(AST.let x);
    void visit(AST.dispatch x);
    void visit(AST.static_dispatch x);
    void visit(AST.typcase x);
    void visit(AST.branch x);
    void visit(AST.formal x);
    */
    void visit(AST.feature x);
    void visit(AST.method x);
    void visit(AST.attr x);
    void visit(AST.class_ x);
    void visit(AST.program x);
}
