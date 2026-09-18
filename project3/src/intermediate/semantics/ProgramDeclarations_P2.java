package intermediate.semantics;

import static intermediate.symtab.SymtabEntry.Kind.*;

import intermediate.antlr4.Pcl_P2Parser.*;
import intermediate.symtab.SymtabEntry;

public class ProgramDeclarations_P2 extends Semantics_P2 {
    Object program(ProgramContext ctx) {
        visit(ctx.programHeader());
        visit(ctx.block().declarations());

        return null;
    }

    SymtabEntry programHeader(ProgramHeaderContext ctx) {
        IdentifierContext idCtx = ctx.identifier();
        String programName = idCtx.getText();
        SymtabEntry programEntry = symtab.enter(programName, PROGRAM);

        programEntry.appendLineNumber(ctx.getStart().getLine());
        idCtx.entry = programEntry;

        return programEntry;
    }
}
