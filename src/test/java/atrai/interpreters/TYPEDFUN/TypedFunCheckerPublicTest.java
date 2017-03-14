package atrai.interpreters.TYPEDFUN;

import atrai.interpreters.common.SemanticException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TypedFunCheckerPublicTest {
    private static final TypedFunChecker inp = new TypedFunChecker();

    @Test
    public void testArithmetic() throws Exception {
        testCase("1+2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) + (%FUN 5 typed (%FUN 5 num 2%) INT%)%) INT%)");
        testCase("1-2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) - (%FUN 5 typed (%FUN 5 num 2%) INT%)%) INT%)");
        testCase("1*2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) * (%FUN 5 typed (%FUN 5 num 2%) INT%)%) INT%)");
        testCase("2/1", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 2%) INT%) / (%FUN 5 typed (%FUN 5 num 1%) INT%)%) INT%)");
        testCase("2 - \"foo\"", StaticTypeError.class);
    }

    private static void testCase(String program, String result) throws Exception {
        assertEquals(result, String.valueOf(inp.interpretString(program)));
    }

    private static void testCase(String program, Class<? extends Throwable> exType) throws Exception {
        try {
            inp.interpretString(program);
            fail("No exception thrown");
        } catch (Exception e) {
            if (!exType.isAssignableFrom(e.getClass())) {
                fail("Expected Exception: " + exType.getCanonicalName() + ", Got: " + e.getClass().getCanonicalName());
            }
        }
    }

    @Test
    public void testComparison() throws Exception {
        testCase("1 < 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) < (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 > 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) > (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 <= 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) <= (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 >= 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) >= (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 == 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) == (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 != 2", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) != (%FUN 5 typed (%FUN 5 num 2%) INT%)%) BOOL%)");
        testCase("1 < \"foo\"", StaticTypeError.class);
    }

    @Test
    public void testVariableDeclaration() throws Exception {
        testCase("let x : int = 3 in x", "(%FUN 1 typed (%FUN 1 expr let (%FUN 2 decllist (%FUN 3 decl (%FUN 4 iden x%) : INT = (%FUN 7 typed (%FUN 7 num 3%) INT%)%)%) in (%FUN 9 typed (%FUN 9 iden x%) INT%)%) INT%)");
        testCase("let x : string = \"foo\" in x", "(%FUN 1 typed (%FUN 1 expr let (%FUN 2 decllist (%FUN 3 decl (%FUN 4 iden x%) : STRING = (%FUN 7 typed (%FUN 7 string \"foo\"%) STRING%)%)%) in (%FUN 9 typed (%FUN 9 iden x%) STRING%)%) STRING%)");
        testCase("let x : string = 3 in x", StaticTypeError.class);
    }

    @Test
    public void testBadVariableReference() throws Exception {
        testCase("x", SemanticException.class);
        testCase("let x : int = 3 in y", SemanticException.class);
    }

    @Test
    public void testVariableAssignment() throws Exception {
        testCase("let x : int = 3 in x = 4", "(%FUN 1 typed (%FUN 1 expr let (%FUN 2 decllist (%FUN 3 decl (%FUN 4 iden x%) : INT = (%FUN 7 typed (%FUN 7 num 3%) INT%)%)%) in (%FUN 8 typed (%FUN 8 expr (%FUN 9 iden x%) = (%FUN 11 typed (%FUN 11 num 4%) INT%)%) INT%)%) INT%)");
        testCase("let x : int = 3 in x = null", StaticTypeError.class);
        testCase("let x : int = 3 in y = 4", SemanticException.class);
        testCase("y = 4", SemanticException.class);
    }

    @Test
    public void testConcatenation() throws Exception {
        testCase("\"foo\" + 1", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 string \"foo\"%) STRING%) + (%FUN 5 typed (%FUN 5 num 1%) INT%)%) STRING%)");
        testCase("1 + \"foo\"", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 num 1%) INT%) + (%FUN 5 typed (%FUN 5 string \"foo\"%) STRING%)%) STRING%)");
        testCase("\"foo\" + \"bar\"", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 string \"foo\"%) STRING%) + (%FUN 5 typed (%FUN 5 string \"bar\"%) STRING%)%) STRING%)");
        testCase("1 + (0 == 0)", StaticTypeError.class);
    }

    @Test
    public void testIf() throws Exception {
        testCase("if 0 == 0 then 3 else 5", "(%FUN 1 typed (%FUN 1 expr if (%FUN 2 typed (%FUN 2 expr (%FUN 4 typed (%FUN 4 num 0%) INT%) == (%FUN 6 typed (%FUN 6 num 0%) INT%)%) BOOL%) then (%FUN 8 typed (%FUN 8 num 3%) INT%) else (%FUN 10 typed (%FUN 10 num 5%) INT%)%) INT%)");
        testCase("if 0 then 3 else 5", StaticTypeError.class);
        testCase("if 0 == 0 then \"foo\" else 5", StaticTypeError.class);
    }

    @Test
    public void testWhile() throws Exception {
        testCase("while 0 == 0 do \"something\"", "(%FUN 1 typed (%FUN 1 expr while (%FUN 2 typed (%FUN 2 expr (%FUN 4 typed (%FUN 4 num 0%) INT%) == (%FUN 6 typed (%FUN 6 num 0%) INT%)%) BOOL%) do (%FUN 8 typed (%FUN 8 string \"something\"%) STRING%)%) NULL%)");
        testCase("while 0 do \"something\"", StaticTypeError.class);
    }

    @Test
    public void testSequence() throws Exception {
        testCase("{ 0; null; \"string\" }", "(%FUN 1 typed (%FUN 1 expr { (%FUN 2 exprseq (%FUN 4 typed (%FUN 4 num 0%) INT%) ; (%FUN 5 typed (%FUN 5 expr null%) NULL%) ; (%FUN 7 typed (%FUN 7 string \"string\"%) STRING%)%) }%) STRING%)");
    }

    @Test
    public void testPrint() throws Exception {
        testCase("print 0", "(%FUN 1 typed (%FUN 1 expr print (%FUN 3 typed (%FUN 3 num 0%) INT%)%) INT%)");
    }

    @Test
    public void testNullaryFunction() throws Exception {
        testCase("(fun () : int = 3)()", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 expr fun ( ) : INT = (%FUN 6 typed (%FUN 6 num 3%) INT%)%) ()->INT%) ( )%) INT%)");
        testCase("(fun () : int = 3)(0)", StaticTypeError.class);
        testCase("fun () : string = 0", StaticTypeError.class);
    }

    @Test
    public void testUnaryFunction() throws Exception {
        testCase("(fun (x:int) : int = x+3)(3)", "(%FUN 1 typed (%FUN 1 expr (%FUN 3 typed (%FUN 3 expr fun ( (%FUN 4 iden x%) : INT ) : INT = (%FUN 7 typed (%FUN 7 expr (%FUN 9 typed (%FUN 9 iden x%) INT%) + (%FUN 11 typed (%FUN 11 num 3%) INT%)%) INT%)%) (INT)->INT%) ( (%FUN 13 typed (%FUN 13 num 3%) INT%) )%) INT%)");
        testCase("(fun (x:int) : int = \"string\")(0)", StaticTypeError.class);
        testCase("(fun (x:string) : int = 3)(0)", StaticTypeError.class);
        testCase("(fun (x:string) : int = 3)()", StaticTypeError.class);
    }

    @Test
    public void testTypeSpec() throws Exception {
        testCase("let x:double = 1 in x", StaticTypeError.class);
        testCase("let f:(int)->int = fun(x:int):int=x in 0", "(%FUN 1 typed (%FUN 1 expr let (%FUN 2 decllist (%FUN 3 decl (%FUN 4 iden f%) : (INT)->INT = (%FUN 8 typed (%FUN 8 expr fun ( (%FUN 9 iden x%) : INT ) : INT = (%FUN 13 typed (%FUN 13 iden x%) INT%)%) (INT)->INT%)%)%) in (%FUN 15 typed (%FUN 15 num 0%) INT%)%) INT%)");
        testCase("let f:(int)->int = fun(x:int):string=\"\" in 0", StaticTypeError.class);
        testCase("let f:(int)->int = fun(x:string):int=1 in 0", StaticTypeError.class);
        testCase("let f:(int)->int = fun():int=1 in 0", StaticTypeError.class);
        testCase("let f:()->int = fun(x:string):int=1 in 0", StaticTypeError.class);
    }

    @Test
    public void testNull() throws Exception {
        testCase("null", "(%FUN 1 typed (%FUN 1 expr null%) NULL%)");
        testCase("null + 1", StaticTypeError.class);
    }

    @Test
    public void testFactorialFromSpec() throws Exception {
        testCase("let fact : (int) -> int = fun(x : int) : int =\n" +
                        "  let prod : int = 1\n" +
                        "   in { while x > 1 do {\n" +
                        "          prod = prod * x;\n" +
                        "          x = x - 1\n" +
                        "        }; prod }\n" +
                        "in fact(4)",
                "(%FUN 1 typed (%FUN 1 expr let (%FUN 2 decllist (%FUN 3 decl (%FUN 4 iden fact%) : (INT)->INT = (%FUN 8 typed (%FUN 8 expr fun ( (%FUN 9 iden x%) : INT ) : INT = (%FUN 12 typed (%FUN 12 expr let (%FUN 13 decllist (%FUN 14 decl (%FUN 15 iden prod%) : INT = (%FUN 18 typed (%FUN 18 num 1%) INT%)%)%) in (%FUN 19 typed (%FUN 19 expr { (%FUN 20 exprseq (%FUN 21 typed (%FUN 21 expr while (%FUN 22 typed (%FUN 22 expr (%FUN 24 typed (%FUN 24 iden x%) INT%) > (%FUN 26 typed (%FUN 26 num 1%) INT%)%) BOOL%) do (%FUN 27 typed (%FUN 27 expr { (%FUN 28 exprseq (%FUN 29 typed (%FUN 29 expr (%FUN 30 iden prod%) = (%FUN 31 typed (%FUN 31 expr (%FUN 33 typed (%FUN 33 iden prod%) INT%) * (%FUN 35 typed (%FUN 35 iden x%) INT%)%) INT%)%) INT%) ; (%FUN 36 typed (%FUN 36 expr (%FUN 37 iden x%) = (%FUN 38 typed (%FUN 38 expr (%FUN 40 typed (%FUN 40 iden x%) INT%) - (%FUN 42 typed (%FUN 42 num 1%) INT%)%) INT%)%) INT%)%) }%) INT%)%) NULL%) ; (%FUN 44 typed (%FUN 44 iden prod%) INT%)%) }%) INT%)%) INT%)%) (INT)->INT%)%)%) in (%FUN 45 typed (%FUN 45 expr (%FUN 47 typed (%FUN 47 iden fact%) (INT)->INT%) ( (%FUN 49 typed (%FUN 49 num 4%) INT%) )%) INT%)%) INT%)");

    }
}
