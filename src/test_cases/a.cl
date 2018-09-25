-- Check for Multiple Class definitions, and Existence of Main class. 
class MainB inherits IO {
  main() : Object {
      out_string("hello")
    };
  };
 -- Main

class B inherits A {
  
  a : Int;
};

class A {
  c : Int;
  main() : Object {{
    c <- 2;
  }};
};

class A{
  d : Int;
};