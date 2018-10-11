-- Cheeck dispatches

class B inherits A{
    b: String <- "hello";
    foo1(): Bar {
        2
    }; -- non existant

    foo2(): A {
        new A
    };

    foo3(c: Int): Int {
        2
    };
    foo4(c:Int, d: String): String {
        "hello"
    };
};

class A{
    f: Int;
    foo3(): Int{
        4
    };
};

class C{
    blah: Int;
};

class Main{
    a: Int <- 2;
    gs: B <- new B;
    main(): Object{{

        new B.foo1();
        new B.foo2();
        new B.foo2(2);
        new B.foo3(a);
        new B.foo4(a);
        a <- gs@A.foo3();
        a <- gs@A.foo3(5);
        a <- gs@C.foo3("HELLO");
    }};
};