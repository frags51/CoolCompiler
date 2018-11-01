
class Main inherits IO {


  main() : Object {
      out_string("hello")
    };
  };
 -- Main

class B inherits A {
  
  c : Int;
};

class A {
  c : Int;
  main() : Object {{
    c <- 2;
  }};
};

