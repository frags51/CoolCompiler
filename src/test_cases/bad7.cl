-- Check for redefinition of methods in an inherited class.
class A {
    a(b: String) : Int {2};
};

class B inherits A{
    a(c: String) : String {2};
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};