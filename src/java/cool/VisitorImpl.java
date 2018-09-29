package cool;

public class VisitorImpl implements Visitor {
    public InheritGraph graph;
    public ScopeTable<String>
    public void visit(AST.program program){
        graph = new InheritGraph(program);
        if(graph.inValidInheritanceGraph()){
		    // Do NO MORE SEMANTIC Analysis
			return;
		}
		AST.class_ root = graph.getRoot();


        classDFS(root);
    }


    /*Preorder DFS for updating parent first*/
    public void classDFS(AST.class_ currclass){
        mangleClassName()




    }





















}
