package mythread;

import java.io.IOException;

import Log.LogSys;
import localfile.LFSystem;
import localfile.Lfile;

public class LogSave extends Thread{
	
	public LFSystem lfs;
	
	public LogSave(LFSystem the_lfs)
	{
		setDaemon(true);
		lfs = the_lfs;
	}

	public void run()
	{
		while(true){
			// addzwt
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				for(int i = 0; i < lfs.all_files.size(); i++){
					lfs.all_files.elementAt(i).my_lock.lock();
					LogSys.shareInstance.SingleLogFile(lfs.all_files.elementAt(i).this_file.getPath());
					lfs.all_files.elementAt(i).my_lock.unlock();
				}
		}
	}
	
}