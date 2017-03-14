package atrai.interpreters.common;

import atrai.core.InternalNode;
import atrai.core.UntypedTree;

import javax.tools.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.TreeMap;

/**
 * Created by ksen on 3/8/17.
 */
public class CodeTemplate {
    public static String region1 = "class Value {\n" +
            "    public int i;\n" +
            "    public boolean b;\n" +
            "    public Lambda l;\n" +
            "    public String s;\n" +
            "\n" +
            "    @Override\n" +
            "    public String toString() {\n" +
            "        if (s != null) return s;\n" +
            "        if (l != null) return \"Lambda\";\n" +
            "        return \"\"+i;\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "abstract class Lambda {\n" +
            "    public static final int STACK_SIZE = 128;\n" +
            "    public static final int FRAME_SIZE = 32;\n" +
            "    public static Value[] stack;\n" +
            "    public static int sp = 0;\n" +
            "    public static Value a0 = new Value();\n" +
            "    public static Value t1 = new Value();\n" +
            "    public static Value t2 = new Value();\n" +
            "    public static Value t3 = new Value();\n" +
            "    public static Value t4 = new Value();\n" +
            "    static {\n" +
            "        stack = new Value[STACK_SIZE];\n" +
            "        for (int i=0; i<STACK_SIZE; i++) {\n" +
            "            stack[i] = new Value();\n" +
            "        }\n" +
            "    }\n" +
            "    public Value[] env;\n" +
            "\n" +
            "    public Lambda(Value[] env, int fp) {\n" +
            "        this.env = new Value[fp];\n" +
            "        System.arraycopy(env, 0, this.env, 0, fp);\n" +
            "    }\n" +
            "\n" +
            "    public abstract void apply();\n" +
            "}\n" +
            "\n";
    public static String region2 = " extends Lambda {\n" +
            "    public Main(Value[] env, int fp) {\n" +
            "        super(env, fp);\n" +
            "    }\n" +
            "\n" +
            "    public void apply() {\n" +
            "        Value[] frame = new Value[FRAME_SIZE];\n" +
            "        int fp = env.length;\n" +
            "        System.arraycopy(env, 0, frame, 0, env.length);\n" +
            "        int label = 0;\n" +
            "        while(true) {\n" +
            "            switch(label) {\n" +
            "                case 0:\n";
    public static String region3 = "                    return;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
    public static String region4 = "{\n" +
            "    public static String execute(String[] args) {\n" +
            "        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();\n" +
            "        return Lambda.a0.toString();\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        (new Main(new Value[Lambda.FRAME_SIZE],0)).apply();\n" +
            "        System.out.println(Lambda.a0);\n" +
            "    }\n" +
            "}\n";

    private TreeMap<String, String> lambdasNameToCode = new TreeMap<>();
    private String name;


    public CodeTemplate(String name) {
        this.name = name;
    }

    public void addLambda(String name, UntypedTree body) {
        InternalNode n = (InternalNode)body.getRoot();
        StringBuilder code = new StringBuilder();
        n.iterate((child, initial, context) -> {
            InternalNode n2 = (InternalNode)child;
            n2.iterate((child1, initial1, context1) -> {
                code.append(child1).append(' ');
                return null;
            }, null, null);
            code.append('\n');
            return null;
        }, null , null);
        addLambda(name, code.toString());
    }

    public void addLambda(String name, String code) {
        lambdasNameToCode.put(name, code);
    }

    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(region1);
        for (String name : lambdasNameToCode.keySet()) {
            sb.append("class ").append(name).append(" ").append(region2).append(lambdasNameToCode.get(name)).append(region3);
        }
        sb.append("public class ").append(name);
        sb.append(region4);
        return sb.toString();
    }

    public void writeCode() {
        try {
            PrintWriter out = new PrintWriter(name+".java");
            out.println(generateCode());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean compileCode() throws IOException {
        File[] files1 = new File[] {new File(name+".java")} ; // input for first compilation task
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits1 =            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
        JavaCompiler.CompilationTask task =  compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1);
        if (!task.call()) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(),
                        diagnostic.getSource().toUri());
            }
            return false;
        }
        fileManager.close();
        return true;
    }

    public String executeCode() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, MalformedURLException {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
        Class<?> cls = classLoader.loadClass(name);
        Method meth = cls.getMethod("execute", String[].class);
        return (String)meth.invoke(null, (Object) null);
    }
}