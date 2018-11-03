class A {
    a : A;
};

class Main {
    a : A <- new A;
	b : Int <- 5;
	c : Int <- 6;
    main() : Int {
        b+c
    };
};
