-- Cheks decl before use

class Main inherits IO {
    main() : IO {{
        (new B).func();
        out_string("hello");
    }};
};

class B inherits IO{
    func(y : String) : IO {{
        x <- 5;
        out_int(x);
    }};
    x : Int <- 6;
};

class C inherits B{
    y : String;
    main : Int;
    y() : Int{
        2
    };
    func(y: String): IO{
        out_int(2)
    };

};
