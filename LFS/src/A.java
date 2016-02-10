import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import Log.LogSys;
import localfile.LFSystem;
import localfile.Lfile;
import mythread.LogSave;
import mythread.Update;

public class A {
	public static void main(String[] args) throws IOException  {
		
		// for test
		LFSystem lfs = new LFSystem();
		Update update = new Update(lfs);
		Thread update_thread = new Thread(update);
		LogSave logsave = new  LogSave(lfs);
		Thread logsave_thread = new Thread(logsave);
		update_thread.start();
		logsave_thread.start();

		Lfile[] a = new Lfile[20];
		for (int i = 0;i < 10;i ++)
			a[i] = new Lfile("log/" + i + ".txt", lfs);
		
		for(int j = 0; j < 10; j++) {
			for(int i = 0; i < 10; i++) {
				a[i].lf_write("this is a test" + i + "," + j);
//				LogSys.shareInstance.List();
//				System.out.println("");
//				if (j % 10 == 1)
//				{
////					System.out.println("aaafwefaewf   " + j);
//					byte[] buffer = new byte[4096];
//					a.lf_read(buffer, 0, 4096);
//					
//			        String s = new String(buffer, "UTF-8");
//			        System.out.println(s);
//				}
			}
		}
		
//		LogSys.shareInstance.List();
		
//		System.out.println(a[0].this_file.getPath());
		
		for (int i = 0;i< 10; i ++)
		{
			byte[] buffer = new byte[4096];
			a[i].lf_read(buffer, 0, 4096);
			
	        String s = new String(buffer, "UTF-8");
	        System.out.println(s);
	        
//	        a[i].close();
		}

		System.out.println(lfs.all_files.size());
		return;
	}
}