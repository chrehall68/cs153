package backend.converter;

import intermediate.antlr4.Pcl_P2Parser.*;

public class ProgramDeclarations_P2 extends Converter_P2 {
    Object program(ProgramContext ctx) {
        code.emitLine("import java.util.HashMap;");
        code.emitLine();
        code.emitLine("public class " + programName);
        code.emitLine("{");
        code.indent();

        visitChildren(ctx);

        code.emitLine();
        code.emitLine("public static void main(String[] args)");
        code.emitLine("{");
        code.indent();
        code.emitLine("System.out.println(\"Done!\");");
        code.dedent();
        code.emitLine("}");

        code.dedent();
        code.emitLine("}");

        code.close();
        return null;
    }
}
