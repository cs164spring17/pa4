package atrai.interpreters.runtime;



//begin 1
class Value {
    public int i;
    public boolean b;
    public Lambda l;
    public String s;

    @Override
    public String toString() {
        if (s != null) return s;
        if (l != null) return "Lambda";
        return ""+i;
    }
}

abstract class Lambda {
    public static final int STACK_SIZE = 128;
    public static final int FRAME_SIZE = 32;
    public static Value[] stack;
    public static int sp = 0;
    public static Value a0 = new Value();
    public static Value t1 = new Value();
    public static Value t2 = new Value();
    public static Value t3 = new Value();
    public static Value t4 = new Value();
    static {
        stack = new Value[STACK_SIZE];
        for (int i=0; i<STACK_SIZE; i++) {
            stack[i] = new Value();
        }
    }
    public Value[] env;

    public Lambda(Value[] env, int fp) {
        this.env = new Value[fp];
        System.arraycopy(env, 0, this.env, 0, fp);
    }

    public abstract void apply();
}

//end 1

class Main
//begin 2
        extends Lambda {
    public Main(Value[] env, int fp) {
        super(env, fp);
    }

    public void apply() {
        Value[] frame = new Value[FRAME_SIZE];
        int fp = env.length;
        System.arraycopy(env, 0, frame, 0, env.length);
        int label = 0;
        while(true) {
            switch(label) {
                case 0:
//end 2
//begin 3
                    return;
            }
        }
    }
}
//end 3

public class
Runtime
//begin 4
{
    public static String execute(String[] args) {
        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();
        return Lambda.a0.toString();
    }

    public static void main(String[] args) {
        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();
        System.out.println(Lambda.a0);
    }
}
//end 4

