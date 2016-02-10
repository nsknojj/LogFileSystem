import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Log.Log;
import localfile.LFSystem;
import localfile.Lfile;
import mythread.LogSave;
import mythread.Update;

public class Test {
	
	static LFSystem lfs = new LFSystem();
	static Update update = new Update(lfs);
	static Thread update_thread = new Thread(update);
	static LogSave logsave = new  LogSave(lfs);
	static Thread logsave_thread = new Thread(logsave);
	static long read_time = 0, write_time = 0;
	
	
	public static class test_thread extends Thread
	{
		int id;
		byte[] buffer = new byte[1000];
		
		public test_thread(int _id)
		{
			id = _id;			
		}
		
		@SuppressWarnings("static-access")
		public void run()
		{
			try {
//				String path = "hdfs://127.0.0.1:9000/a.txt" + String.valueOf(id);				
				Lfile a = new Lfile("log/" + id + ".txt", lfs);
				// op_count = operation amount, p = rate of write / total 
				int op_write = 10000;
				int op_read = 100;
				int op_read_time = op_write / op_read;
				
				String s = "0123456789", big = "";
				for (int i = 0;i < 1000;i ++) big += s;
				
				for (int i = 0;i < op_write;i ++)
				{
					long t0 = System.currentTimeMillis();
					
//					a.lf_write("0123456789");
					a.lf_write(big);
					long t1 = System.currentTimeMillis();
					write_time += t1 - t0;
					
//					if ((i+1)% 200 == 0) Thread.sleep(1000);
				
					if ((i + 1) % op_read_time == 0)
					{
						t0 = System.currentTimeMillis();
						a.lf_read(buffer, 0, 1000);
						t1 = System.currentTimeMillis();
						read_time += t1 - t0;
					}
				}
				
//				a.close();
			}
			catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException  {
		
		// for test
		update_thread.start();
		logsave_thread.start();
		List<test_thread> thread_list = new ArrayList<test_thread>();
		
		long t0 = System.currentTimeMillis();
		
		for (int i = 0;i < 1;i ++) {
			test_thread thread = new test_thread(i);
//			thread.run();
			thread.start();
//			thread.join();
			thread_list.add(thread);
		}
		
		for (test_thread t : thread_list)
			t.join();
		
		long t1 = System.currentTimeMillis();
//		Thread.currentThread().join();
		System.out.println("read:" + read_time + "ms");
		System.out.println("write:" + write_time + "ms");
		System.out.println("total time:" + (t1 - t0) + "ms");
		System.out.println(lfs.all_files.size());
	}
}