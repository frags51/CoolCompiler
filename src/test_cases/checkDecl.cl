-- Cheks decl before use

class Main inherits IO {
    main() : IO {{
        (new B).func();
        out_string("hello");
    }};
};

class B inherits IO{
    func() : IO {{
        -- x : Int;
        x <- 5;
        out_int(x);
    }};
    x : Int <- 6;
};