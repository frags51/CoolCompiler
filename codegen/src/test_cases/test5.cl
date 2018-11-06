-- Loop test

class A{
    ctr: Int <- 5;
    getCtr(): Int{
        ctr
    };
    incCtr(): Int{
        ctr <- ctr-1
    };
};

class Main inherits IO{
   lp:A <- new A;
   main() : IO{
   {
        self@IO.out_string("Loop test!\n");
        while not lp@A.getCtr() <= 0 loop
        {self@IO.out_int(lp@A.getCtr()); lp@A.incCtr();} pool;
        self@IO.out_string("\nloop end\n");
   }
   };
};