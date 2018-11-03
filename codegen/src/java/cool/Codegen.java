package cool;

import java.io.PrintWriter;

public class Codegen{
	public Codegen(AST.program program, PrintWriter out){
		//Write Code generator code here
		GlobalData.out = out;
        out.println("; LLVM-IR generated as a part of Compilers-2 IITH.");
        CodeGenVisitor vis = new CodeGenVisitor();
        program.accept(vis, null);
	}
}
