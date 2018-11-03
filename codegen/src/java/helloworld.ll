; LLVM-IR generated as a part of Compilers-2 IITH.
; Global String Consts
@.str_0 = private unnamed_addr constant [14 x i8] c"Hello world!
\00"
@.str_2 = private unnamed_addr constant [3 x i8] c"%s\00"
@.str_1 = private unnamed_addr constant [3 x i8] c"%d\00"

; C Function Declarations << Used to implement COOL Funcs
declare i32 @printf(i8*, ...)

declare i32 @__isoc99_scanf(i8*, ...)

declare noalias i8* @malloc(i64)

declare i8* @strcpy(i8*, i8*)

declare i8* @strcat(i8*, i8*)

declare i64 @strlen(i8*)

declare void @exit(i32)

