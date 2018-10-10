#Semantic Analyzer for COOL Language

### Designed by: 
Mayank Hooda

Supreet Singh

##Introduction

This program takes cool file(s) as input, and outputs a type-annotated 
Abstract Syntax Tree, if the program conforms to COOL's semantics, and
error message(s) otherwise. It is assumed that there is no SELF_TYPE in the program
(however inbuilt functions with type SELF_TYPE are handled).
 
## Design Decisions

### Overview

The semantic analyzer does the following: 
+ Look at all the classes and build an inheritance graph. The inheritance
graph is then checked for cycles. If any cycles/ill-formed class hierarchies
are found, the semantic analysis is halted after printing appropriate 
error messages.
+ The previous step checks whether main is well defined, and also adds 
the _features_ of each class to a global scope table, since there 
may be forward references to those in some other class.
+ In the next phase, type checking is done. The type of each node
of the AST is determined after determining the type(s) of its children.
Any variables that are declared in a node are added to the global scope
table, after incrementing scope wherever necessary.
+ Both these steps are implemented using the **Visitor Pattern.**
+ The class `AST` defined in `AST.java` and its subclasses were modified 
to implement the interface `VisitableElement` to define an `accept`
method.

### Storage of State (Context)

The state of the semantic analyzer, and some functions for 
 global use are stored in the class `GlobalData`
as static fields. By state (type environment), we mean:
+ The scope table `scpTable` containing the names of methods/variables that are
valid in the current scope. 
+ A reference `inheritGraph` to an instance of the Inheritance Graph.
+ The current file/class name for proper error messages, as well as
lookups in the scope table.
+ Functions to get the mangled name of functions/variables for lookup
from the scope table.
+ Functions to get return type and argument types of the functions from their
mangled names.


### Phase 1

This involves the formation of the inheritance graph, stored in 
`GlobalData.inheritGraph`. 

### Phase 2: Type Checking
This involves annotating the type of each node of the AST. 
This is done using a visitor pattern. The visitor interface is defined
in `Visitor.java`. The type checking is done in a bottom-up fashion,
in the sense that type of child node(s) is determined before determining
the type of the current node. The class `TypeCheckVisitor` implements
this interface and does the type-checking. The process starts 
by calling `AST.program.accept(an instance of TypeCheckVisitor)`.
 
 An
AST.program node is the root of the AST. The type
 environment is stored in `GlobalData`.

Whenever a new name is introduced, it is checked if it is indeed possible to declare
a new name in that expression, and if yes (for example in `AST.let` node), the value of scope is incremented by 1
and that name is mangled and inserted into the scope table (reasons for mangling are explained below).

Type checking of children first is necessary because it is needed in most cases to determine
the type and correctness of the parent expression (example, arithmetic).

Any errors are reported when they are encountered, and the semantic analyser tries to 
recover, for semantic check in the rest of the file. An example is we give the type 
**"Object"** to nodes that can't be given a type otherwise. If a type can be given for
enabling further semantic checks, it is given, for example in arithmetic expressions
`int` type is given (Issue #11 on Github).

This is the final pass of the semantic analysis. If no errors were found, 
a type annotated AST should be output. If not, a set of error messages
will be output.

### Scope Table 

The provided implementation of the scope table is used. The scope table
`GlobalData.scpTable` is a hashmap, mapping mangled names of attributes
to their types and mangled name of methods to a string that stores 
its argument types and return type.

Only one scope table is used. At _level 0_, the scope table stores 
all the features (attributes+methods) of all the classes, mangled with 
the class name. This scope is not exited at any time, so 
these entries stay intact during all of the semantic analysis.
This was done to ease the lookup of class fields when accessed 
from another class (only methods allowed in cool via dispatch, but
attribute names also mangled to have a **modular** code that might possibly
be re-used for some language that allows attributes to be accessed 
from other classes too).

For ease of implementation, any variables (names) declared in let,
formal arguments, etc are also mangled with the current class
 name.
Whenever such expressions are encountered, the value of scope 
is incremented 
by 1, and then those names are added to the scope table. Once
type checking for those expressions is done, the value of scope
is decremented and those names are removed (we exit the scope) 
because they are no longer needed.

The variable **self** is bound in every class implicitly - this 
is done when the TypeCheckVisitor visits an `AST.class_` node.

#### Entering and exiting a scope
+ All class features (attributes + methods) are stored in `scpTable`
by their mangled name, at level 0. This level is persistent for 
the whole semantic analysis, to support ease of implementation 
of forward references.
+ Scope is increased  (entered) 
on encountering functions, let expressions
and branches of case expressions.
+ Scope is decreased (exited) on coming out of the above mentioned
expressions, and any names are lost - they are not needed any 
more.
+ All insertions into the scope table check if there is duplication 
of names, and an error is printed then.
+ If any name is found, it is looked up in the scope table
starting from the current scope and going to the outermost scope.
If not found, an error is printed.

### Name Mangling

Name mangling means, storing extra information with names.
We have mangled Class names along with attribute/method names.

#### Reasons for name Mangling
+ For methods, provides an easy way to find the correct method 
and match its signature with the given argument types. 
Also, it can be checked easily if a dispatch is legal or not.
+ For attributes too it provides easy look up in accordance with 
our persistent level-0 scope table design. It would also be easier
to port this scope table to languages/extensions that allow 
accessing of attributes from other classes, just like dispatch.
+ The names in inner expressions, like function formals, let and case
are mangled to provide uniformity of code and ease of 
implementation.

### An overview of other used Classes

#### AST
+ Contains definition of each node. 
+ Modified to implement `VisitableElement`.
+ Also modified to provide copy constructors for some nodes.

#### GlobalError (For Error Handling)
+ To report errors from other classes, this class is used.
+ It provides static error reporting methods similar to those
in Semantic.java, so we don't need to pass a reference to an
object of Semantic class to report errors.
+ A debug flag, `DBG` is defined that can be used to print debug 
messages.

#### InheritGraph
+ Contains methods to form, and check for well-formed inheritance graph.
+ Contains methods to mangle class feature names and insert them
into the global scope table.
+ Contains methods to check if a typename exists in the classlist.
+ Contains methods to find Least Common Ancestor type and to check
if a type is subtype of another.

#### Semantic
+ This just has a driver method that calls the accept function 
of the `AST.program` node on the two visitors.
+ Also, it checks for error messages reported by `GlobalError` 
and sets its error flag accordingly, which is required by the
Semantic analyzer to exit.

## Testing

We have included some test cases. Bad test cases (error producing)
are named `bad*.cl`. Good test cases are all the other files.

We have tried to break/test each rule of COOL. What a test case is 
supposed to do is commented in that file.


+ Parent of object is null
+ Class Inheritance: In case parent class is not declared 
or parent is a non inheritable class, parent is set to object.
