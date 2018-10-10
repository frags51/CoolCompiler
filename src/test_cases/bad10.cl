
-- Check for redefinition of methods/attrs in same class


class A {
    a : Int;
    a: String;
    b(c: Int): String {"F"};
    b (c: String) : String {"G"};
};



class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};

