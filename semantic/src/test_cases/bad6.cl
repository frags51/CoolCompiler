-- Check for redefinition of attributes in an inherited class.
class A {
    a : Int;
};

class B inherits A{
    a : String;
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};