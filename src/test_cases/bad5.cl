-- Check for wrong definition of in Main class
class A {
    a : Int;
};

class B {
    c : Int;
};

class Main inherits IO {
    main(a: Int) : Object {2};
};