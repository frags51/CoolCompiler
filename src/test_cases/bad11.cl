-- Checks if method's body's type conforms with its return type.
class A {

    b(c: Int): A {new B};

};

class B inherits A{
    g: Int;
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};