package Log;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogSys {
    List<Log> logList = new CopyOnWriteArrayList<Log>();
    private int logTopNum;
    private String LogHistoryPath = "/Users/Sunzy/LogHistory.txt";

    /*
    * MARK: Singleton
    */
    public static LogSys shareInstance = new LogSys();

    public LogSys(){
        logTopNum = 1;
    }

    public LogSys(String loghistorypath){
        logTopNum = 1;
        LogHistoryPath = loghistorypath;
    }

    public void List(){
        for(Log log : logList) {
            System.out.println("LogNum:" + log.logNum + " Type:" + log.Type + " File:" + log.File + " Data:" + log.Data);
        }
    }

    public void New(String file, String Data){
        Log log = new Log(logTopNum, file, Data, Type.NEW);
        logTopNum++;
        logList.add(log);
    }

    public void Append(String file, String Data){
        Log log = new Log(logTopNum, file, Data, Type.APPEND);
        logTopNum++;
        logList.add(log);
        //System.out.println("$$LogNum:" + log.logNum + " Type:" + log.Type + " File:" + log.File + " Data:" + log.Data);
    }

    public void Delete(int lognum){
        for(int i=0;i<logList.size();i++){
            if(logList.get(i).logNum == lognum){
                logList.remove(i);
            }
        }
    }

    public void Realize(String filePath) throws IOException {
        List<Log> tmpList = new CopyOnWriteArrayList<Log>();
        try {
        	// addzwt
        	SingleLogFile(filePath);
        	
        	System.out.println(filePath);
        	
            String FilePath = filePath + "_Real";
            //System.out.println(FilePath);
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()) { //ÅÐ¶ÏÎÄŒþÊÇ·ñŽæÔÚ
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//¿ŒÂÇµœ±àÂëžñÊœ
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                //lineTxt = bufferedReader.readLine();
                //logTopNum = Integer.parseInt(lineTxt);
                //System.out.println(logTopNum);
                while((lineTxt = bufferedReader.readLine()) != null){
                    System.out.println(lineTxt);
                    if (lineTxt.equals("\n") || lineTxt.equals("")){
                        continue;
                    }
                    String str[] = lineTxt.split(":%:");
                    Log log = new Log(Integer.parseInt(str[0]), filePath, str[2], TypeTransfer(str[1]));
                    tmpList.add(log);
                }
                read.close();
                for (Log aLogList : tmpList) {
                    //System.out.println(aLogList.Type);
                    switch (aLogList.Type) {
                        case NEW:
                            NewFile(FilePath, aLogList.Data);
                            break;
                        case APPEND:
                            AppendFile(FilePath, aLogList.Data);
                            break;
                        default:
                            System.out.println("Realize Error: Type!");
                            break;
                    }
                }
                CleanLog(filePath);
            }else{
                System.out.println("blank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SingleLogFile(String filePath){
//        for (Log aLogList : logList) {
////            System.out.println(aLogList.File + "::" + filePath);
////            String data = "LogNum:" + aLogList.logNum + " Type:" + aLogList.Type + " File:" + aLogList.File + " Data:" + aLogList.Data + "\n";
//            if (aLogList.File.equals(filePath)){
//                CleanLog(aLogList.File);
//
//            }
//        }

        for (Log aLogList : logList) {
            //System.out.println(aLogList.Type);
            if (aLogList.File.equals(filePath)){
                String data = aLogList.logNum + ":%:" + aLogList.Type + ":%:" + aLogList.Data + "\n\n";
                //System.out.println("test");
                PrintLog(aLogList.File, data);
            }
        }


        for(int i=0;i<logList.size();i++){
            //System.out.println("Sin:LogNum:" + logList.get(i).logNum + " Type:" + logList.get(i).Type + " File:" + logList.get(i).File + " Data:" + logList.get(i).Data);
            if(logList.get(i).File.equals(filePath)){
                logList.remove(i);
                i--;
            }
        }


    }

    public void LogFile() throws IOException {

        for (Log aLogList : logList) {
            //System.out.println(aLogList.Type);
            //String data = "LogNum:" + aLogList.logNum + " Type:" + aLogList.Type + " File:" + aLogList.File + " Data:" + aLogList.Data + "\n";
            CleanLog(aLogList.File);
        }

        for (Log aLogList : logList) {
            //System.out.println(aLogList.Type);
            String data = aLogList.logNum + ":%:" + aLogList.Type + ":%:" + aLogList.Data + "\n\n";
            PrintLog(aLogList.File, data);
        }

        for(int i=0;i<logList.size();i++){
            logList.remove(i);
        }
    }

    public void CrashSave() throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(LogHistoryPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert out != null;
        PrintStream p=new PrintStream(out);
        p.print(logTopNum + "\n");
        for(Log log : logList) {
            p.println(log.logNum + ":%:" + log.Type + ":%:" + log.File + ":%:" + log.Data);
        }
        out.close();
    }

    public void CrashRecover(){
        try {
            String encoding="GBK";
            File file=new File(LogHistoryPath);
            if(file.isFile() && file.exists()){ //ÅÐ¶ÏÎÄŒþÊÇ·ñŽæÔÚ
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//¿ŒÂÇµœ±àÂëžñÊœ
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                lineTxt = bufferedReader.readLine();
                logTopNum = Integer.parseInt(lineTxt);
                //System.out.println(logTopNum);
                while((lineTxt = bufferedReader.readLine()) != null){
                    System.out.println(lineTxt);
                    if (lineTxt.equals("\n")){
                        continue;
                    }
                    String str[] = lineTxt.split(":%:");
                    Log log = new Log(Integer.parseInt(str[0]), str[2], str[3], TypeTransfer(str[1]));
                    logList.add(log);
                }
                read.close();
            }else{
                System.out.println("ÕÒ²»µœÖž¶šµÄÎÄŒþ");
            }
        } catch (Exception e) {
            System.out.println("¶ÁÈ¡ÎÄŒþÄÚÈÝ³öŽí");
            e.printStackTrace();
        }
    }

    private void NewFile(String filePath, String Data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert out != null;
        PrintStream p=new PrintStream(out);
        p.print(Data);
        out.close();
    }

    private void AppendFile(String filePath, String Data) throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true)));
            out.write(Data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void PrintLog(String filePath, String Data){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, true)));
            out.write(Data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void CleanLog(String filePath){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath)));
            //out.write("File: " + filePath + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Type TypeTransfer(String type){
        switch (type){
            case "NEW":
                return Type.NEW;
            case "APPEND":
                return Type.APPEND;
            default:
                System.out.println("TypeTransfer Error!");
                return Type.ERROR;
        }
    }
}