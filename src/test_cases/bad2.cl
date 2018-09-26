-- Check for cycles in inherit graph
class A inherits B{
    a : Int;
};

class B inherits A{
    c : Int;
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};