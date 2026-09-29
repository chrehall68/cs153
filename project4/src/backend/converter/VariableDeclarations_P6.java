package backend.converter;

import static intermediate.type.Typespec_P6.Form.*;

import intermediate.antlr4.Pcl_P6Parser.*;
import intermediate.type.Typespec_P6;
import intermediate.type.Typespec_P6.Form;

import java.util.ArrayList;

public class VariableDeclarations_P6 extends Converter_P6 {
    boolean first = true;
    int anonymousClassCount = 0;

    Object variableDeclarations(VariableDeclarationsContext ctx) {
        VariableIdentifierListContext varListCtx = ctx.variableIdentifierList();
        TypeSpecificationContext typespecCtx = ctx.typeSpecification();
        Typespec_P6 typespec = typespecCtx.typespec;
        Form typeForm = typespec.getForm();
        String typeName = javaTypeName(typespec);

        if (first) {
            code.emitLine();
            first = false;
        }

        code.emitStart();
        if (typeName == null) {
            // should only happen for unnamed records?
            // so we'll name them
            if (typeForm != RECORD && typeForm != ARRAY) {
                throw new RuntimeException("Failed");
            }
            // $ isn't allowed in pascal variables
            // therefore this won't collide with any pascal variables
            // and then since we always increment our count, this won't collide
            // with anything that we output either
            String id = "$" + anonymousClassCount + "$";
            anonymousClassCount++;
            typeName = id + varListCtx.identifier().get(0).entry.getName() + "Class";

            // copied from TypeDefinitions_P6.java
            code.emitStart();
            code.emit("private static class " + typeName + "{");
            code.indent();
            recordFields = true;
            visit(ctx.typeSpecification());
            recordFields = false;
            code.dedent();
            code.emitStart();
            code.emit("}");
            code.emitLine();
            code.emitStart();
        }
        if (programVariables && !recordFields) {
            code.emit("private static ");
        }
        code.emit(typeName);

        String separator = " ";
        for (IdentifierContext idCtx : varListCtx.identifier()) {
            // removed the .toLowerCase() here since this allows us to easily
            // treat the entry's name as the canonical name and avoids
            // cases of us forgetting to .toLowerCase() something, for instance
            String variableName = idCtx.entry.getName();
            code.emit(separator + variableName);

            if (typeForm == ARRAY) array(typeName, typespecCtx);
            if (typeForm == RECORD) record(typeName);

            separator = ", ";
        }

        code.emitEnd(";");
        return null;
    }

    private void record(String typeName) {
        // initialize objects with the default constructor instead of null
        code.emit(" = new " + typeName + "()");
    }

    private void array(String typeName, TypeSpecificationContext typespecCtx) {
        Typespec_P6 typespec = typespecCtx.typespec;

        emitArraySpecifier(typespec);
        code.emit(" = ");

        typespec = typespecCtx.typespec;

        // TODO - be careful about arrays of objects since we want those to be initialized
        // with the default constructor, not as null
        ArrayList<Integer> dimentionSizes = new ArrayList<>();
        while (typespec.getForm() == ARRAY) {
            int elmtCount = typespec.getArrayElementCount();
            dimentionSizes.add(elmtCount);
            typespec = typespec.getArrayElementType();
        }

        String result;
        if (typespec.getForm() == RECORD) {
            // special case; because we're converting to Java, we can't just
            // declare a fixed size array because arrays in Java get initialized
            // with the default value of the type, and the default value for any
            // object type is null
            // so instead, we'll manually construct this
            String prevDimension = "new " + typeName + "()";
            for (int i = dimentionSizes.size() - 1; i >= 0; --i) {
                StringBuilder curDimension = new StringBuilder();
                int elmtCount = dimentionSizes.get(i);

                curDimension.append("{");
                for (int j = 0; j < elmtCount; ++j) {
                    curDimension.append(prevDimension);
                    if (j + 1 < elmtCount) {
                        curDimension.append(", ");
                    }
                }
                curDimension.append("}");

                prevDimension = curDimension.toString();
            }
            result = prevDimension;
        } else {
            // not a special case; just have [ elmtCount ] for each
            StringBuilder builder = new StringBuilder();
            builder.append("new " + typeName);
            for (int elmtCount : dimentionSizes) {
                builder.append("[" + elmtCount + "]");
            }
            result = builder.toString();
        }
        code.emit(result);
    }
}
