package cool;

public interface VisitorRet {
    void visit(AST.ASTNode x, StringBuilder res);
    void visit(AST.expression x, StringBuilder res);
    void visit(AST.no_expr x, StringBuilder res);
    void visit(AST.string_const x, StringBuilder res);
    void visit(AST.int_const x, StringBuilder res);
    void visit(AST.object x, StringBuilder res);
    void visit(AST.comp x, StringBuilder res);
    void visit(AST.eq x, StringBuilder res);
    void visit(AST.leq x, StringBuilder res);
    void visit(AST.lt x, StringBuilder res);
    void visit(AST.neg x, StringBuilder res);
    void visit(AST.divide x, StringBuilder res);
    void visit(AST.mul x, StringBuilder res);
    void visit(AST.sub x, StringBuilder res);
    void visit(AST.plus x, StringBuilder res);
    void visit(AST.isvoid x, StringBuilder res);
    void visit(AST.new_ x, StringBuilder res);
    void visit(AST.assign x, StringBuilder res);
    void visit(AST.block x, StringBuilder res);
    void visit(AST.loop x, StringBuilder res);
    void visit(AST.cond x, StringBuilder res);
    void visit(AST.let x, StringBuilder res);
    void visit(AST.dispatch x, StringBuilder res);
    void visit(AST.static_dispatch x, StringBuilder res);
    void visit(AST.typcase x, StringBuilder res);
    void visit(AST.branch x, StringBuilder res);
    void visit(AST.formal x, StringBuilder res);
    void visit(AST.bool_const x, StringBuilder res);
    void visit(AST.feature x, StringBuilder res);
    void visit(AST.method x, StringBuilder res);
    void visit(AST.attr x, StringBuilder res);
    void visit(AST.class_ x, StringBuilder res);
    void visit(AST.program x, StringBuilder res);
}
