package backend;

public class BackendMode {
    public enum Mode {
        CONVERTER,
        EXECUTOR,
        DEBUGGER,
        COMPILER
    };

    public static Mode mode;
}
