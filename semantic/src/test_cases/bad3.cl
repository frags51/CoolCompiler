-- Check for non inheritable (IO, Int etc) and non defined parents.
class A inherits Bullshit{
    a : Int;
};

class B inherits Int{
    c : Int;
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};