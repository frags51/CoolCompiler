-- Wrong comparison operators, AND Bad function return type.
class A {
    a(b: String) : Int {{
        "String" < "Another String?";
        "Anotheranother string" <= 4;
        "WYYYYYYYYYYY" = true;
        4;
        "GG";
    }};
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};