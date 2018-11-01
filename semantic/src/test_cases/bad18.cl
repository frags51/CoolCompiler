-- Predicate check in if and loop

class GG{
    b: String <- "hello";
};

class A{
    d: String <- "hello";
    c: Int <- if d then 120 else 2 fi;
    q: Int <- while d loop c <- c*2 pool; -- Note here type of loop is object
};

class Main{
    main(): Object{
        2
    };
};