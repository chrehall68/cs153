package backend;

public class BackendMode 
{
    public enum Mode { CONVERTER, EXECUTOR, DEBUGGER, COMPILER };
    
    static public Mode mode;
}
