package atrai.interpreters.FUN;

import atrai.antlr.ANTLRTokenizer;
import atrai.antlr.GenericAntlrToUntypedTree;
import atrai.antlr.Location;
import atrai.core.InternalNode;
import atrai.core.Transformer;
import atrai.core.UntypedTree;
import atrai.interpreters.common.Environment;
import atrai.interpreters.common.Interpreter;
import atrai.interpreters.common.SemanticException;

import java.util.Arrays;



/**
 * FunInterpreter for the FUN language
 *
 * @author Koushik Sen
 * @author Alex Reinking
 */
public class FunInterpreter extends Interpreter {
    private String grammarName = "atrai.antlr.FUN";

    public UntypedTree parseFile(String pgm) {
        return null;
    }

    public Object interpret(UntypedTree st) {
        return null;
    }


    public UntypedTree parseString(String pgm) {
        return null;
    }
}
