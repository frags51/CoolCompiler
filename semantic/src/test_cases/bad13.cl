-- Checks if self is handled properly.
class A {

    b(c: Int): A {new B};

};

class B inherits A{
    g: Int;
    gg: String <- let self : String in {
        "hello";
    };
    evenMoreGG: Object <- case g of
        self: A => "helo";
        this: B => "NVM";
    esac;
};

class Main inherits IO {
    self: Int;
    main() : Object {{
          self <- new Main;
          out_string("hello");
          self;
     }};
};
