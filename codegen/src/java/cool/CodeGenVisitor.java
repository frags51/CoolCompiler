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
        if(x.name.equals("self")) { // self implicitly bound to this!
            res.append("%this");
            return;
        }
        if(GlobalData.isFormalP(x.name)){ // load this thing up in memory.
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("%").append(IRBuilder.nextVarNumb()).append(" = load ").append(IRBuilder.llvmTypeName(x.type))
                    .append(" , ").append(IRBuilder.llvmTypeName(x.type)).append("*").append(" %").append(x.name).append(".addr")
                    .append("\n");
            GlobalData.out.println(IRBuilder.temp);
            res.append("%").append(IRBuilder.varNumb-1);
        }
        else{
            StringBuilder gep = new StringBuilder(" = getelementptr inbounds ").append(IRBuilder.llvmTypeNameNotPtr(GlobalData.curClassName))
                    .append(" ").append(IRBuilder.llvmTypeName(GlobalData.curClassName)).append(" %this, ");
            int i = 0, ind=0;
            String tClass = GlobalData.curClassName;
            while(true){ // loop will definitely end!
                if(GlobalData.attrToIndex.containsKey(GlobalData.varMangledName(x.name, tClass))) {
                    ind = GlobalData.attrToIndex.get(GlobalData.varMangledName(x.name, tClass));
                    break;
                }
                else{
                    if(GlobalError.DBG) System.out.println("; In loop! CodeGenVisitor AST.assign!");
                    tClass = GlobalData.inheritGraph.map.get(tClass).parent;
                    i++;
                }
            }
            for(int g=0; g<i+1;g++) gep.append("i32 0, ");
            gep.append("i32 ").append(ind).append("\n");
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(gep);

            IRBuilder.temp.append("%").append(IRBuilder.nextVarNumb()).append(" = load ").append(IRBuilder.llvmTypeName(x.type))
                    .append(" , ").append(IRBuilder.llvmTypeName(x.type)).append("*").append(" %").append(IRBuilder.varNumb-2)
                    .append("\n");
            GlobalData.out.println(IRBuilder.temp);

            res.append("%").append(IRBuilder.varNumb-1);
        }
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
        // TODO: (Check) check div by zero.
        StringBuilder L = new StringBuilder();
        StringBuilder R = new StringBuilder();
        x.e1.accept(this,L);
        x.e2.accept(this,R);

        // check zero
        String ifL = "if.then."+ IRBuilder.ifNumb;

        String endL = "if.end."+IRBuilder.ifNumb;
        IRBuilder.ifNumb++;

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(" = icmp eq ")
                .append("i32").append(" ")
                .append(R).append(", 0\n");
        String cmpReg = "%"+(IRBuilder.varNumb-1);
        IRBuilder.temp.append("\tbr i1 ").append(cmpReg).append(", label %").append(ifL).append(", label %").append(endL);

        IRBuilder.temp.append("\n").append(ifL).append(":\n");
        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%s"));
        String pS = "%"+(IRBuilder.varNumb-1);
        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%d\n"));
        String pD = "%"+(IRBuilder.varNumb-1);
        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("ERROR: Div by zero on Line: "));
        String eS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pS).append(", i8* ").append(eS).append(")\n");
        IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pD).append(", i32 ").append(x.lineNo).append(")\n");
        IRBuilder.temp.append("\tcall void @exit(int32 1)\n");
        IRBuilder.temp.append("\tbr label %").append(endL).append("\n");

        IRBuilder.temp.append(endL).append(":\n");
        GlobalData.out.println(IRBuilder.temp);
        // end check zero

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
    //TODO: Remove/add class name setter after every call to new
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

            /*
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
            */
            GlobalData.out.println(IRBuilder.temp.toString());
        }
    }

    @Override
    public void visit(AST.assign x, StringBuilder res) {
        StringBuilder R = new StringBuilder();
        x.e1.accept(this, R);
        res.append(R);
        String asnToType = "";

        if(GlobalData.isFormalP(x.name)){ // store in %x.whatever
            // TODO: (Check) Set asnToType here after processing functions!
            asnToType = GlobalData.formalPMap.get(x.name);
            if(x.e1.type.equals(asnToType)){ // no need to typecast.
                IRBuilder.temp.setLength(0);
                IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.type)).append(" ").append(R).append(", ")
                        .append(IRBuilder.llvmTypeName(x.type)).append("* %").append(x.name).append(".addr").append("\n");
                GlobalData.out.println(IRBuilder.temp.toString());
            }
            else{ // need to typecast.
                IRBuilder.temp.setLength(0);
                IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(IRBuilder.genTypCastPtr(x.type, asnToType, R.toString()));
                IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(asnToType)).append(" %").append(IRBuilder.varNumb-1).append(", ")
                        .append(IRBuilder.llvmTypeName(asnToType)).append("* %").append(x.name).append(".addr").append("\n");
                GlobalData.out.println(IRBuilder.temp.toString());
            }
        }
        else{
            StringBuilder gep = new StringBuilder(" = getelementptr inbounds ").append(IRBuilder.llvmTypeNameNotPtr(GlobalData.curClassName))
                    .append(" ").append(IRBuilder.llvmTypeName(GlobalData.curClassName)).append(" %this, ");
            int i = 0, ind=0;
            String tClass = GlobalData.curClassName;
            while(true){ // loop will definitely end!
                if(GlobalData.attrToIndex.containsKey(GlobalData.varMangledName(x.name, tClass))) {
                    ind = GlobalData.attrToIndex.get(GlobalData.varMangledName(x.name, tClass));
                    asnToType = GlobalData.scpTable.lookUpGlobal(GlobalData.varMangledName(x.name, tClass));
                    break;
                }
                else{
                    if(GlobalError.DBG) System.out.println("; In loop! CodeGenVisitor AST.assign!");
                    tClass = GlobalData.inheritGraph.map.get(tClass).parent;
                    i++;
                }
            }
            for(int g=0; g<i+1;g++) gep.append("i32 0, ");
            gep.append("i32 ").append(ind).append("\n");
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(gep);

            if(x.e1.type.equals(asnToType)){ // no need to typecast.

                IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.type)).append(" ").append(R).append(", ")
                        .append(IRBuilder.llvmTypeName(x.type)).append("* %").append(IRBuilder.varNumb-1).append("\n");
                GlobalData.out.println(IRBuilder.temp.toString());
            }
            else{
                IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(IRBuilder.genTypCastPtr(x.type, asnToType, R.toString()));
                IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.type)).append(" %").append(IRBuilder.varNumb-1).append(", ")
                        .append(IRBuilder.llvmTypeName(x.type)).append("* %").append(IRBuilder.varNumb-2).append("\n");
                GlobalData.out.println(IRBuilder.temp.toString());
            }
        }
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

        /* Make the break */
        StringBuilder condBreak = new StringBuilder();
        condBreak.append("br label %").append(whileCondLabel);
        GlobalData.out.println(condBreak.toString());

        /* Make label */
        StringBuilder condLabel = new StringBuilder();
        condLabel.append("\n").append(whileCondLabel).append(":");
        GlobalData.out.println(condLabel.toString());

        StringBuilder pred = new StringBuilder();
        x.predicate.accept(this,pred);
        /* pred holds the result */
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genTrunc("i8", "i1", pred.toString()));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        /* Condition break */
        StringBuilder breakBuild = new StringBuilder();
        breakBuild.append("br i1 ");
        breakBuild.append("%").append(IRBuilder.varNumb).append(", ");
        breakBuild.append("label %").append(whileBodyLabel);
        breakBuild.append(", label %").append(whileEndLabel);
        GlobalData.out.println(breakBuild.toString());

        StringBuilder bodyLabel = new StringBuilder();
        bodyLabel.append("\n").append(whileBodyLabel).append(":");
        GlobalData.out.println(bodyLabel.toString());

        StringBuilder inter = new StringBuilder();
        x.body.accept(this,inter);

        /* Another break */
        condBreak.setLength(0);
        condBreak.append("br label %").append(whileCondLabel);
        GlobalData.out.println(condBreak.toString());

        StringBuilder endLabel = new StringBuilder();
        bodyLabel.append("\n").append(whileEndLabel).append(":");
        GlobalData.out.println(endLabel.toString());

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
        // cmp: register containing result of predicate: Boolean.
        // truncate bool i8 to i1
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.varNumb).append(IRBuilder.genTrunc("i8", "i1", cmp.toString()));
        IRBuilder.temp.append("\n");
        IRBuilder.varNumb++;
        GlobalData.out.println(IRBuilder.temp.toString());

        /* Make the break instruction */
        StringBuilder builder = new StringBuilder();
        builder.append("br i1 ");
        builder.append("%").append(IRBuilder.varNumb-1).append(", ");
        builder.append("label %").append(ifLabel);
        builder.append(", label %").append(elseLabel);
        GlobalData.out.println(builder.toString());

        //IF THEN
        StringBuilder ifbuilder = new StringBuilder();
        GlobalData.out.println("\n"+ifLabel+":");
        x.ifbody.accept(this,ifbuilder);

        StringBuilder thenbreakBuilder = new StringBuilder();
        thenbreakBuilder.append("br label %").append(endLabel);
        GlobalData.out.println(thenbreakBuilder.toString());

        //IF ELSE
        StringBuilder elseBuilder = new StringBuilder();
        GlobalData.out.println("\n"+elseLabel+":");
        x.elsebody.accept(this,elseBuilder);

        StringBuilder elsebreakBuilder = new StringBuilder();
        elsebreakBuilder.append("br label %").append(endLabel);
        GlobalData.out.println(elsebreakBuilder.toString());

        /* END LABEL */
        StringBuilder endBuilder = new StringBuilder();
        GlobalData.out.println("\n"+endLabel+":");

        /* Build result type */
        String resultType = GlobalData.inheritGraph.lCA(x.ifbody.type,x.elsebody.type);

        // if no typecasts reqruied:
        if(resultType.equals(x.ifbody.type) && resultType.equals(x.elsebody.type)){
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(" = phi ").append(resultType);
            IRBuilder.temp.append(" [ ").append(ifbuilder.toString()).append(", ").append(ifLabel).append(" ],");
            IRBuilder.temp.append(" [ ").append(elseBuilder.toString()).append(", ").append(elseLabel).append(" ]\n");
            GlobalData.out.println(IRBuilder.temp.toString());

            res.setLength(0);
            res.append("%").append(IRBuilder.varNumb-1);
        }
        else{
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(IRBuilder.genTypCastPtr(x.ifbody.type, resultType, ifbuilder.toString()));
            IRBuilder.temp.append("\n");

            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(IRBuilder.genTypCastPtr(x.elsebody.type, resultType, elseBuilder.toString()));
            IRBuilder.temp.append("\n");

            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(" = phi ").append(resultType);
            IRBuilder.temp.append(" [ ").append(IRBuilder.varNumb-2).append(", ").append(ifLabel).append(" ],");
            IRBuilder.temp.append(" [ ").append(IRBuilder.varNumb-1).append(", ").append(elseLabel).append(" ]\n");
            GlobalData.out.println(IRBuilder.temp.toString());

            res.setLength(0);
            res.append("%").append(IRBuilder.varNumb-1);
        }

    }

    @Override
    public void visit(AST.let x, StringBuilder res) {

    }

    @Override
    public void visit(AST.dispatch x, StringBuilder res) {

    }

    @Override
    public void visit(AST.static_dispatch x, StringBuilder res) {
        StringBuilder callerRes = new StringBuilder();
        x.caller.accept(this, callerRes);

        if(!GlobalData.isPrimitive(x.caller.type)){ // check isvoid
            String ifL = "if.then."+ IRBuilder.ifNumb;

            String endL = "if.end."+IRBuilder.ifNumb;
            IRBuilder.ifNumb++;

            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(" = icmp eq ")
                    .append(IRBuilder.llvmTypeName(x.caller.type)).append(" ")
                    .append(callerRes).append(", null\n");
            String cmpReg = "%"+(IRBuilder.varNumb-1);
            IRBuilder.temp.append("\tbr i1 ").append(cmpReg).append(", label %").append(ifL).append(", label %").append(endL);

            IRBuilder.temp.append("\n").append(ifL).append(":\n");
            IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%s"));
            String pS = "%"+(IRBuilder.varNumb-1);
            IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%d\n"));
            String pD = "%"+(IRBuilder.varNumb-1);
            IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("ERROR: Disp on Void->Line: "));
            String eS = "%"+(IRBuilder.varNumb-1);

            IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pS).append(", i8* ").append(eS).append(")\n");
            IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pD).append(", i32 ").append(x.lineNo).append(")\n");
            IRBuilder.temp.append("\tcall void @exit(int32 1)\n");
            IRBuilder.temp.append("\tbr label %").append(endL).append("\n");

            IRBuilder.temp.append(endL).append(":\n");
            GlobalData.out.println(IRBuilder.temp);
        } // check isvoid

        String ancestorWithFun = GlobalData.parentWithFun(x.name, x.typeid);
        // Need to tpecast
        if(!ancestorWithFun.equals(x.caller.type)){
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("%").append(IRBuilder.nextVarNumb())
                    .append(IRBuilder.genTypCastPtr(x.caller.type, ancestorWithFun, callerRes.toString())).append("\n");
            callerRes.setLength(0);
            callerRes.append("%").append(IRBuilder.varNumb-1);
            GlobalData.out.println(IRBuilder.temp);
        }

        StringBuilder fP = new StringBuilder("(").append(IRBuilder.llvmTypeName(ancestorWithFun)).append(" ");
        fP.append(callerRes);
        StringBuilder argRes = new StringBuilder();
        for(AST.expression gg : x.actuals){
            argRes.setLength(0);
            fP.append(", ");
            gg.accept(this, argRes);
            fP.append(IRBuilder.llvmTypeName(gg.type));
            fP.append(" ");
            fP.append(argRes);
        }
        fP.append(")");

        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("%").append(IRBuilder.nextVarNumb()).append(" = ")
                .append("call ").append(IRBuilder.llvmTypeName(x.type)).append(" @");
        IRBuilder.temp.append(GlobalData.funMangledName(x.name, ancestorWithFun))
                .append(fP).append("\n");
        GlobalData.out.println(IRBuilder.temp);
        res.setLength(0);
        res.append("%").append(IRBuilder.varNumb-1);
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
        // Bookkeeping
        GlobalData.formalPMap.clear();
        GlobalData.curFunName = x.name;
        IRBuilder.varNumb = 0;

        StringBuilder args = new StringBuilder("(");
        args.append(IRBuilder.llvmTypeName(GlobalData.curClassName)).append(" %this");
        for (AST.formal g : x.formals) {
            GlobalData.formalPMap.put(g.name, g.typeid);
            args.append(", ").append(IRBuilder.llvmTypeName(g.typeid)).append(" %").append(x.name);
        }
        args.append(") {\n");

        StringBuilder bodyRes = new StringBuilder();


        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("; Method name: ").append(x.name).append(" from class: ").append(GlobalData.curClassName);
        IRBuilder.temp.append("\n");
        IRBuilder.temp.append("define ").append(IRBuilder.llvmTypeName(x.typeid)).append(" @")
        .append(GlobalData.funMangledName(x.name,GlobalData.curClassName)).append(args).append("entry: \n");
        GlobalData.out.println(IRBuilder.temp);

        // Now need to alloc these formals onto the stack.
        for (AST.formal g : x.formals){
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(g.name).append(".addr  = alloca")
                    .append(IRBuilder.llvmTypeName(g.typeid)).append("\n");
            IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(g.typeid)).append(" %").append(g.name)
                    .append(", ").append(IRBuilder.llvmTypeName(g.typeid)).append("* %").append(g.name).append(".addr")
                    .append("\n");
            GlobalData.out.println(IRBuilder.temp);
        }

        x.body.accept(this, bodyRes);

        IRBuilder.temp.setLength(0);

        if(!x.body.type.equals(x.typeid)){
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb())
                    .append(IRBuilder.genTypCastPtr(IRBuilder.llvmTypeName(x.body.type), IRBuilder.llvmTypeName(x.typeid), bodyRes.toString()));
            IRBuilder.temp.append("\tret ").append(IRBuilder.llvmTypeName(x.typeid)).append(" %").append(IRBuilder.varNumb-1).append("\n");
        }
        else{
            IRBuilder.temp.append("\tret ").append(IRBuilder.llvmTypeName(x.typeid)).append(" ").append(bodyRes).append("\n");
        }
        IRBuilder.temp.append("}\n");
        GlobalData.out.println(IRBuilder.temp);
    }

    @Override
    public void visit(AST.attr x, StringBuilder res) {
        StringBuilder bodyRes = new StringBuilder();
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\t%").append(IRBuilder.nextVarNumb()).append(" = getelementptr inbounds ")
                .append(IRBuilder.llvmTypeNameNotPtr(GlobalData.curClassName)).append(", ")
                .append(IRBuilder.llvmTypeName(GlobalData.curClassName)).append(" %this, i32 0, i32 ")
                .append(GlobalData.attrToIndex.get(GlobalData.varMangledName(x.name, GlobalData.curClassName)))
                .append("\n");
        GlobalData.out.println(IRBuilder.temp);
        String storeReg = "%"+(IRBuilder.varNumb-1);

        x.value.accept(this, bodyRes);
        if(x.value.type.equals(GlobalData.NOTYPE)){
            String toStore;
            if(x.typeid.equals("Int")||x.typeid.equals("Bool")) toStore="0";
            else if(x.typeid.equals("String")){
                StringBuilder tmp = new StringBuilder("\t");
                tmp.append("%").append(IRBuilder.nextVarNumb()).append(IRBuilder.gepString(""));
                GlobalData.out.println(tmp);
                toStore="%"+(IRBuilder.varNumb-1);
            }
            else toStore="null";
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.typeid)).append(" ").append(toStore)
                    .append(" , ").append(IRBuilder.llvmTypeName(x.typeid)).append("* ").append(storeReg).append("\n");
            GlobalData.out.println(IRBuilder.temp);
        }
        else if(x.value.type.equals(x.typeid)){
            String toStore = bodyRes.toString();
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.typeid)).append(" ").append(toStore)
                    .append(" , ").append(IRBuilder.llvmTypeName(x.typeid)).append("* ").append(storeReg).append("\n");
            GlobalData.out.println(IRBuilder.temp);
        }
        else{ //TODO: (CHECK genTypCastPtr func) typecasting int/primitive to Object.
            IRBuilder.temp.setLength(0);
            IRBuilder.temp.append("%").append(IRBuilder.nextVarNumb())
                    .append(IRBuilder.genTypCastPtr(x.value.type, x.typeid, bodyRes.toString()));
            String toStore = "%"+(IRBuilder.varNumb-1);

            IRBuilder.temp.append("\tstore ").append(IRBuilder.llvmTypeName(x.typeid)).append(" ").append(toStore)
                    .append(" , ").append(IRBuilder.llvmTypeName(x.typeid)).append("* ").append(storeReg).append("\n");
            GlobalData.out.println(IRBuilder.temp);
        }
    }

    @Override
    public void visit(AST.class_ x, StringBuilder res) {
        GlobalData.curClassName=x.name;
        if(x.name.equals("Object")|| x.name.equals("Int")||x.name.equals("String")||x.name.equals("Bool")||x.name.equals("IO"))
            return;
        for(AST.feature g : x.features){
            if(g instanceof AST.method) g.accept(this, new StringBuilder());
        }
    }

    @Override
    public void visit(AST.program x, StringBuilder res) {
        emitGlobalStrings();
        emitCFuncs();
        updateStructSize();
        emitConstructors();

    }


    // Emit IR for global Strings
    private void emitGlobalStrings(){
        /**
         * Add %d, %s to the map.
         */
        GlobalData.stringConstNames.put("%d", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("%d\n", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("%s", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("%s\n", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("ERROR: Disp on Void->Line: ", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("ERROR: Div by zero on Line: ", IRBuilder.strGlobal + GlobalData.stringNameNum);
        GlobalData.stringNameNum++;
        GlobalData.stringConstNames.put("Abort Called!\n", IRBuilder.strGlobal + GlobalData.stringNameNum);
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

        int init = 0,index = 1;


        if(curr.equals("Int") || curr.equals("String") || curr.equals("Bool")) return ;
        init+= GlobalData.classtoSize.get(currClass.parent);
        StringBuilder builder = new StringBuilder("%class."+curr);
        builder.append((" = type { ")).append("%class.").append(currClass.parent);
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
        GlobalData.classtoSize.put("Object", 8);
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

    static void emitConstructors(){
        GlobalData.out.println(IRBuilder.constructorObject());
        GlobalData.out.println(IRBuilder.constructorIO());

        AST.class_ root = GlobalData.inheritGraph.getRoot();
        emitConstructorsDFS(root);
    }

    static void emitConstructorsDFS(AST.class_ currClass){
        GlobalData.curClassName = currClass.name;
        if(currClass.name.equals("Int") || currClass.name.equals("String") || currClass.name.equals("IO") || currClass.name.equals("Object") || currClass.name.equals("Bool")) return ;
        GlobalData.out.println("; Constructor for class "+currClass.name);
        IRBuilder.varNumb = 0;

        GlobalData.out.println("define void @" + GlobalData.funMangledName(currClass.name, currClass.name) + "(" + "%class."+currClass.name + "* %this) {");

        GlobalData.out.println("\nentry:");
        callParentConstructor(currClass,"%this");

        for(AST.feature f : currClass.features) {
            if(f instanceof AST.attr) {
                AST.attr a = (AST.attr) f;
                //a.accept(this);
            }
        }




    }

    static void callParentConstructor(AST.class_ childClass, String childRegister) {
        String parentType = childClass.parent;
        if(!parentType.equals(null)){
            // CREATE CONVERT INSTRUCTION
            StringBuilder builder = new StringBuilder();
            String storeRegister = "%"+IRBuilder.varNumb;
            IRBuilder.varNumb++;
            builder.append(storeRegister);
            builder.append(" = ").append("bitcast");
            builder.append(" ").append(childClass.name+"*");
            builder.append(" ").append(childRegister).append(" to ");
            builder.append(parentType+"*");
            GlobalData.out.println(builder.toString());

            builder.setLength(0);
            builder.append("call void @").append(GlobalData.funMangledName(parentType, parentType)).append("(").append("%class."+parentType).append("* ").append(storeRegister).append(")");
            GlobalData.out.println(builder.toString());
        }

    }

    private static void emitObjectFuncs(){
        IRBuilder.varNumb = 0;
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for Abort(): \n");
        IRBuilder.temp.append("define %class.Object* @").append(GlobalData.funMangledName("abort", "Object"))
                .append("(%class.Object* %this){\nentry:\n");

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%s"));
        String pS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("Abort Called!\n"));
        String eS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pS).append(", i8* ").append(eS).append(")\n");
        IRBuilder.temp.append("call void @exit(i32 0)");

        // cleanup, add :Object return code
        IRBuilder.temp.append("\t%dmyretval = call noalias i8* @malloc(i64 0)\n")
                .append("\t%dmyretval2 = bitcast i8* %dmyretval to %class.Object*\n")
                .append("\tret %class.Object* %dmyretval2\n}\n");
        GlobalData.out.println(IRBuilder.temp);
    }

    private static void emitIOFuncs(){
        IRBuilder.varNumb = 0;
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for IO.out_string(): \n");
        IRBuilder.temp.append("define %class.IO* @").append(GlobalData.funMangledName("out_string", "IO"))
                .append("(%class.IO* %this, i8* %toPrint){\nentry:\n");

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%s"));
        String pS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pS).append(", i8* ").append("%toPrint").append(")\n");

        // cleanup, add :Object return code
        IRBuilder.temp.append("\t%dmyretval = call noalias i8* @malloc(i64 8)\n")
                .append("\t%dmyretval2 = bitcast i8* %dmyretval to %class.IO*\n")
                .append("\tret %class.IO* %dmyretval2\n}\n");
        GlobalData.out.println(IRBuilder.temp);

        // out_int
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for IO.out_int(): \n");
        IRBuilder.temp.append("define %class.IO* @").append(GlobalData.funMangledName("out_int", "IO"))
                .append("(%class.IO* %this, i32 %toPrint){\nentry:\n");

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%d"));
        pS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\tcall i32 (i8*, ...) @printf(i8* ").append(pS).append(", i32 ").append("%toPrint").append(")\n");

        // cleanup, add :Object return code
        IRBuilder.temp.append("\t%dmyretval = call noalias i8* @malloc(i64 8)\n")
                .append("\t%dmyretval2 = bitcast i8* %dmyretval to %class.IO*\n")
                .append("\tret %class.IO* %dmyretval2\n}\n");
        GlobalData.out.println(IRBuilder.temp);

        // in_int
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for IO.in_int(): \n");
        IRBuilder.temp.append("define i32 @").append(GlobalData.funMangledName("in_int", "IO"))
                .append("(%class.IO* %this){\nentry:\n");

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%d"));
        pS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\t%store = alloca i32\n");

        IRBuilder.temp.append("\n\t%cal = call i32 (i8*, ...) @__isoc99_scanf(i8* ").append(pS).append(", i32* ").append("%store").append(")\n");

        // cleanup, add :Object return code
        IRBuilder.temp.append("\t%dmyretval = load i32, i32* %store\n")
                .append("\tret i32 %dmyretval\n}\n");
        GlobalData.out.println(IRBuilder.temp);

        // in_string
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for IO.in_string(): \n");
        IRBuilder.temp.append("define i8* @").append(GlobalData.funMangledName("in_string", "IO"))
                .append("(%class.IO* %this){\nentry:\n");

        IRBuilder.temp.append("\n\t%").append(IRBuilder.nextVarNumb()).append(" = ").append(IRBuilder.gepString("%s"));
        pS = "%"+(IRBuilder.varNumb-1);

        IRBuilder.temp.append("\n\t%store1 = alloca i8*\n");
        IRBuilder.temp.append("\t%store = load i8*, i8** %0\n");
        IRBuilder.temp.append("\n\t%cal = call i32 (i8*, ...) @__isoc99_scanf(i8* ").append(pS).append(", i8* ").append("%store").append(")\n");

        // cleanup, add :Object return code
        IRBuilder.temp.append("\t%dmyretval = load i8*, i8** %store\n")
                .append("\tret i8* %dmyretval\n}\n");
        GlobalData.out.println(IRBuilder.temp);
    }

    private static void emitStringFuncs(){
        // length()
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for String.length(): \n");
        IRBuilder.temp.append("define i32 @").append(GlobalData.funMangledName("length", "String"))
                .append("(i8* %this){\nentry:\n");

        IRBuilder.temp.append("\t%dmyretval = call i64 (i8*) @strlen(i8* %this)\n")
                .append("\tret i64 %dmyretval\n}\n");
        GlobalData.out.println(IRBuilder.temp);

        // concat()
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for String.concat(): \n");
        IRBuilder.temp.append("define i8* @").append(GlobalData.funMangledName("concat", "String"))
                .append("(i8* %this, i8* s2){\nentry:\n");

        IRBuilder.temp.append("\t%l1 = call i64 @strlen(i8* %this)\n");
        IRBuilder.temp.append("\t%l2 = call i64 @strlen(i8* %s2)\n");
        IRBuilder.temp.append("\t%t1 = add i64 %l1, %l2\n");
        IRBuilder.temp.append("\t%tot = add i64 %t1, 1\n");
        IRBuilder.temp.append("\t%news = call noalias i8* @malloc(i64 %tot)\n");
        IRBuilder.temp.append("\t%call1 = call i8* @strcpy(i8* news, i8* %this)\n");
        IRBuilder.temp.append("\t%call2 = call i8* @strcat(i8* news, i8* %s2)\n");

        IRBuilder.temp.append("\tret i8* %news\n}\n");
        GlobalData.out.println(IRBuilder.temp);

        // substr()
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("\n; Code for String.substr(), using strncpy: \n");
        IRBuilder.temp.append("define i8* @").append(GlobalData.funMangledName("concat", "String"))
                .append("(i8* %this, i32 %s, i32 %e){\nentry:\n");

        IRBuilder.temp.append("\t%l1 = zext i32 %e to i64\n");
        IRBuilder.temp.append("\t%ptrfwd = getelementptr inbounds i8, i8* %this, i32 %s");
        IRBuilder.temp.append("\t%news = call noalias i8* @malloc(i64 %e)\n");
        IRBuilder.temp.append("\t%call1 = call i8* @strncpy(i8* news, i8* %ptrfwd, i64 %l1)\n");

        IRBuilder.temp.append("\tret i8* %news\n}\n");
        GlobalData.out.println(IRBuilder.temp);
    }

    private static void emitLLVMmain(){
        IRBuilder.temp.setLength(0);
        IRBuilder.temp.append("; Driver Main\n");
        IRBuilder.temp.append("define i32 @main() {\nentry:\n");
        IRBuilder.temp.append("\t%m1 = call noalias i8* @malloc(i64 ").append(GlobalData.classtoSize.get("Main"))
            .append(")\n");
        IRBuilder.temp.append("\t%m = bitcast i8* %m1 to %class.Main*\n");
        IRBuilder.temp.append("\tcall void @").append(GlobalData.funMangledName("Main", "Main"))
                .append("(%class.Main* %m)\n");
        IRBuilder.temp.append("\t%call = call ").append(IRBuilder.llvmTypeName(GlobalData.mainType))
                .append(" @").append(GlobalData.funMangledName("main", "Main"))
                .append("(%class.Main* %m)\n").append("ret i32 0\n}\n");
    }
}
