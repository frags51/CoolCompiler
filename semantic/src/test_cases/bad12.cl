-- Checks if attr's body's type conforms with its expected type (initialization).
class Parent {

    g: Int;

};

class B inherits Parent{
    f: Int;
    t1: B <- new Parent;
    t2: B <- new B;
    t3: B <- new C;
    t4: B <- new Sibling;
};

class C inherits B{
    e : Int;
};
class Sibling inherits Parent{
    ll: Int;
};

class Main inherits IO {
    main() : Object {
          out_string("hello")
     };
};