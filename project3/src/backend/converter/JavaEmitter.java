package backend.converter;

import java.io.FileWriter;
import java.io.PrintWriter;

public class JavaEmitter {
    private PrintWriter objectFile;
    private String objectFileName;

    private String indentation;
    private boolean needLineFeed;

    JavaEmitter(String programName, String suffix) {
        try {
            objectFileName = programName + "." + suffix;
            objectFile = new PrintWriter(new FileWriter(objectFileName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        indentation = "";
        needLineFeed = false;
    }

    String getObjectFileName() {
        return objectFileName;
    }

    void close() {
        objectFile.close();
    }

    public void emit(String code) {
        objectFile.print(code);
        objectFile.flush();
        needLineFeed = true;
    }

    public void emitLine() {
        lineFeedIfNeeded();
        objectFile.println();
        objectFile.flush();
        needLineFeed = false;
    }

    public void emitLine(String code) {
        lineFeedIfNeeded();
        objectFile.println(indentation + code);
        objectFile.flush();
        needLineFeed = false;
    }

    public void lineFeedIfNeeded() {
        if (needLineFeed) {
            objectFile.println();
            objectFile.flush();
            needLineFeed = false;
        }
    }

    public void emitStart() {
        lineFeedIfNeeded();
        emit(indentation);
    }

    public void emitStart(String code) {
        lineFeedIfNeeded();
        emit(indentation + code);
    }

    public void emitEnd(String code) {
        objectFile.println(code);
        objectFile.flush();
        needLineFeed = false;
    }

    public void emitCommentLine(String text) {
        emitLine(indentation + "// " + text);
        needLineFeed = false;
    }

    public void indent() {
        indentation += "    ";
    }

    public void dedent() {
        if (indentation.length() >= 4) {
            indentation = indentation.substring(4);
        }
    }
}
