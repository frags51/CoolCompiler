# Code Generator for COOL Language

### Designed by: 

Supreet Singh

Mayank Hooda
## Introduction

This program takes cool file(s) as input, and outputs the semantically equivalent LLVM 
IR. It is assumed that there is no SELF_TYPE,dynamic dispatch and let constructs in the
program. No compile time errors need to get flagged as they would be caught be semantic
analyser and two runtime checks are handled( Division by zero and isvoid function call)
 
## Design Decisions

### Overview

The code generator does the following: 
+ First, all the string constants used are stored in a map and declared globally for future
reference to value.
+ Next all the C functions like printf, scanf are declared for mapping corresponding default
COOL functions like out_int and in_int
+ Now all the classes are visited in a DFS fashion and a type signature consisting of their attributes
is declared. In the same pass the size of the classes is also updated for future memory
allocation.
+ Now the constructors of all the classes are called in the same fashion. The attributes are visited
in this run and initialised with the expression provided, if it is.
+ The classes are now visited in the same context of source code and the code for method definitions 
is printed to IR. A visitor pattern is followed wherein formals and expressions are also 
visited.
+ Method defintions for default classes like Object, String follow.
+ Finally a driver function main is written which calls the main function from the Main class
and initialises execution.
### Global Storage State

The state of the code generator and some functions for 
 global use are stored in the class `GlobalData`
as static fields. The type environments include : 
+ A reference `inheritGraph` to an instance of the Inheritance Graph.
+ The current file/class name for proper error messages, as well as
lookups in the scope table.
+ Functions to get the mangled name of functions/variables for lookup
from the scope table.
+ Functions to get return type and argument types of the functions from their
mangled names.
+ A map `stringConstNames` for storing string value to name, which is a prefix followed by 
linearly increasing index
+ A map `attrToIndex` to save the index of attributes for `getelementpointer`
+ A map `classToSize` which stores the size of every class construct 
+ A map `formalPMap` for mapping formal names o typeid

###IR build
For passing the result of an expression to be used by another, a StringBuilder `res` is used 
as argument in visitor pattern which in essence passes the register associated. 


### Visitor Pattern
#### 1.AST.attr
AST.attribute is visited while defining the constructors. If assignment is not null, a store operation
has been invoked. Appropriate `bitcast` has been done to the conforming types if not of
the same type. For primitive classes, default values have been initialized in case of no expression.

#### 2. AST.method
The methods are defined with their mangled names. Initially the formal parameters are added
for checking possible collisions with attribute names. If a variable `a` is a formal parameter 
as well as `attribute`, we first check the map for `a`, if not found then we proceed with attribute
use. The formals are allocated on the stack and stored in memory.

#### 3. AST.no_expr
Returns null. Used to check on null, if no assignment has been made on declaration

#### 4. AST.cond
Basic Blocks are created with labels corresponding to then, else and end. Return type of the expression
is the join of the then and else blocks. The predicate is visited and cmp instruction is added. Now 
ifbody and elsebody are visited and their expressions are added to IR. Finally join is returned.
Bitcasting is done if necessary.

#### 5. AST.loop
Again basic blocks are created for condition, body and end respectively. After making the labels. 
condition break is added. After visiting the body another break is added to cond.

#### 6. `AST.mul` `AST.plus` `AST.plus` `AST.divide` `AST.comp` `AST.leq`
A generalised binary instruction is created and operator type mul, plus etc. are supplied.

#### 7. AST.neg
Simply the expression after visiting is subtracted from 0.

#### 8. AST.static_dispath
First caller is visited and checks are made on the type. If not a primitive type, then a
check is made whether caller is `null` or not through cmp instruction. If null then runtime
error is thrown. Now the closest parent is found recursively where function is actually defined
The formal parameters are then checked against actuals, if not equal then typecasting is done.
Default class functions are seperately handled. Now the function expression is called with 
the recursively found definition.

#### 9. AST.assign
Expression on right hand side is visited. If left hand side is a method parameter. If body does
notmatch the return type of parameter then bitcast is performed. If the left side is an 
attribute then appropriate address is caught by getelementpointer, for which `attrToIndex`
map was formed. Result from right hand side is then stored to memory and returned.

#### 10. AST.block
  After visiting every expression, last expression is returned to `res` register.
  
 #### 11. AST.new_
 If it is a primitive type then default value is returned. Now size of type is computed
 via classToSize function. Now C function `malloc` is called. Typecasting is performed
 to the type called. Now the constructor of the class type is called.






### An overview of other used Classes

#### AST
+ Contains definition of each node. 
+ Modified to implement `VisitorRet`
+ Also modified to provide copy constructors for some nodes.

#### IRBuilder
+ Holds utility functions required for emitting IR code like templates forcreating binary 
instructions, typecasting, extensions and truncations.

