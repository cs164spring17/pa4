package atrai.interpreters.TYPEDLET;

import atrai.interpreters.common.SemanticException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ksen on 3/9/17.
 */
public class TypedLetCheckerTest {

    @Test
    public void check1() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("1");
        assertEquals("(%LET 2 typed (%LET 2 num 1%) INT%)", res.toString());
    }

    @Test(expected = SemanticException.class)
    public void check2() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("x");
    }

    @Test
    public void check3() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("1 + 2");
        assertEquals("(%LET 1 typed (%LET 1 expr (%LET 3 typed (%LET 3 num 1%) INT%) + (%LET 5 typed (%LET 5 num 2%) INT%)%) INT%)", res.toString());
    }

    @Test
    public void check4() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("1 / 2");
        assertEquals("(%LET 1 typed (%LET 1 expr (%LET 3 typed (%LET 3 num 1%) INT%) / (%LET 5 typed (%LET 5 num 2%) INT%)%) INT%)", res.toString());
    }

    @Test
    public void check5() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("(1 + 2) * 2");
        assertEquals("(%LET 1 typed (%LET 1 expr (%LET 3 typed (%LET 3 expr (%LET 5 typed (%LET 5 num 1%) INT%) + (%LET 7 typed (%LET 7 num 2%) INT%)%) INT%) * (%LET 9 typed (%LET 9 num 2%) INT%)%) INT%)", res.toString());
    }

    @Test
    public void check6() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("1 > 2");
        assertEquals("(%LET 1 typed (%LET 1 expr (%LET 3 typed (%LET 3 num 1%) INT%) > (%LET 5 typed (%LET 5 num 2%) INT%)%) BOOL%)", res.toString());
    }

    @Test
    public void check7() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("1 / 2 <= 3");
        assertEquals("(%LET 1 typed (%LET 1 expr (%LET 2 typed (%LET 2 expr (%LET 4 typed (%LET 4 num 1%) INT%) / (%LET 6 typed (%LET 6 num 2%) INT%)%) INT%) <= (%LET 8 typed (%LET 8 num 3%) INT%)%) BOOL%)", res.toString());
    }


    @Test
    public void check8() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("let x: int = 4 in x");
        assertEquals("(%LET 1 typed (%LET 1 expr let (%LET 2 iden x%) : INT = (%LET 5 typed (%LET 5 num 4%) INT%) in (%LET 7 typed (%LET 7 iden x%) INT%)%) INT%)", res.toString());
    }

    @Test
    public void check9() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("let x: int = 4 in x > 1");
        assertEquals("(%LET 1 typed (%LET 1 expr let (%LET 2 iden x%) : INT = (%LET 5 typed (%LET 5 num 4%) INT%) in (%LET 6 typed (%LET 6 expr (%LET 8 typed (%LET 8 iden x%) INT%) > (%LET 10 typed (%LET 10 num 1%) INT%)%) BOOL%)%) BOOL%)", res.toString());
    }

    @Test
    public void check10() throws Exception {
        TypedLetChecker inp = new TypedLetChecker();
        Object res = inp.interpretString("if 2 > 3 then 3 else 4");
        assertEquals("(%LET 1 typed (%LET 1 expr if (%LET 2 typed (%LET 2 expr (%LET 4 typed (%LET 4 num 2%) INT%) > (%LET 6 typed (%LET 6 num 3%) INT%)%) BOOL%) then (%LET 8 typed (%LET 8 num 3%) INT%) else (%LET 10 typed (%LET 10 num 4%) INT%)%) INT%)", res.toString());
    }


}