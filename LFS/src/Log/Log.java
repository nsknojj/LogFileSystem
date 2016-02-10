package Log;

enum Type {NEW, APPEND, ERROR}

public class Log {
    int logNum;
    String File;
    String Data;
    Type Type;

    public Log(int lognum, String file, String data, Type type){
        logNum = lognum;
        File = file;
        Data = data;
        Type = type;
    }
}