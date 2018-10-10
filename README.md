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
by calling `AST.program.accept(an instance of TypeCheckVisitor)`. An
AST.program node is the root of the AST.

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


+ Parent of object is null
+ Type of copy() etc is "Object" for now.
+ Class Inheritance: In case parent class is not declared 
or parent is a non inheritable class, parent is set to object.
+ The context for typechecking is saved in GlobalData as 
static variables.
