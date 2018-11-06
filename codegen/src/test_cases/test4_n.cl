class A {
	h: Int <-2;
    a: A;

};

class B inherits A{
	s: Int <- 10;
	t: Int <- 15;
    g(f: A): A {
        a <- a
    };
};


class E {
   

    e2 : Int <- new Int;
    --e6 : Object <- new Int;
    e3 : Object <- new IO;
    --e4 : Object <- new String;
    --e5 : Object <- new Bool;
    --f : IO <- new IO;

    
    checkTypes() : Int {
        {

            0;
        }  
    };
};

class Main inherits IO {
    ff:A;
    e : E <- new E;
    a : B <- new B;
    b: Int <- 3;
    d: Bool <- true;
    main() : IO {
        {
            a@B.g(a);          
        	--self@Main.out_int(5+6);
        	self@IO.out_string("helloasfas");
            -- e@E.checkTypes();
        }
    };
};
