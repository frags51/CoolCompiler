-- in_funcs
-- String funcs

class Main inherits IO{
   a: Int <- 4;
   b: Int <- 5;
   c: Int <- 4;
   d : String;
   main() : IO{
   {

        self@IO.out_string("Enter a string: ");
        d <- self@IO.in_string();
        self@IO.out_string(d);
        self@IO.out_string("\n");

        self@IO.out_string("Length is: ");
        self@IO.out_int(d@String.length());
        self@IO.out_string("\n");

        self@IO.out_string("Concat with @blah is: ");
        self@IO.out_string(d@String.concat("@blah"));
        self@IO.out_string("\n");

        self@IO.out_string("Substr (1,3) is: ");
        self@IO.out_string(d@String.substr(1, 3));
        self@IO.out_string("\n");
   }
   };
};