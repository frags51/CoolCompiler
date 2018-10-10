-- Cheeck dispatches

class B{
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
};

class Main{
    a: Int <- 2;
    main(): Object{{
        new B.foo1();
        new B.foo2();
        new B.foo2(2);
        new B.foo3(a);
        new B.foo4(a);


    }};
};