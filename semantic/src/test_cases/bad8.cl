-- Arithmetic of wrong types
class A {
    a(b: String) : Int {2};
};


class Main inherits IO {
    main() : Object {{
          out_int(2 + "s");
          out_int(2 * "s");
          out_int(2 / "s");
          out_int(2 - "s");
          out_int(~true);

     }};
};