-- Arithmetic test
-- Also divide by zero.

class Main inherits IO{
   main() : IO{
   {
        self@IO.out_int(5+6);
        self@IO.out_int(5-6);

        self@IO.out_int(5*6);
        self@IO.out_int(~5);
        self@IO.out_int(~6);
        self@IO.out_int(5/0);
   }
   };
};