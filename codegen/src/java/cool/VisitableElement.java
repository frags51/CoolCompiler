package cool;

public interface VisitableElement {
    void accept(Visitor x);
    void accept(VisitorRet x, StringBuilder res);
}
