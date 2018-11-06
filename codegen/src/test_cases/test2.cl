-- if check
-- LT, GT, EQ etc test.

class Main inherits IO{
   a: Int <- 4;
   b: Int <- 5;
   c: Int <- 4;
   main() : IO{
   {

        self@IO.out_int(if a=b then 2 else 3 fi);
        self@IO.out_int(if a<=c then 2 else 3 fi);
        self@IO.out_int(if a<b then 2 else 3 fi);

   }
   };
};