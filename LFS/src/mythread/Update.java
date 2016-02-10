package mythread;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import Log.LogSys;
import localfile.LFSystem;
import localfile.Lfile;

public class Update extends Thread{
	
//		public static long FILE_MAX_SIZE = 64*1024*1024;
		public static long FILE_MAX_SIZE = 64000;   // for test
	
		public LFSystem lfs;
		
		public Update(LFSystem the_lfs)
		{
			setDaemon(true);
			lfs = the_lfs;
		}

		public void run()
		{
			while(true){
				for(int i = 0; i < lfs.all_files.size(); i++)
				{
					long file_size = -1;
					file_size = lfs.all_files.elementAt(i).this_file.length();
//					if(file_size!=0)
//						System.out.println(i + ": " + file_size);     // for test
					if(file_size > FILE_MAX_SIZE){
						do_update(lfs.all_files.elementAt(i));
//						try {
//							wait(100000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					if(file_size!=0)
//						System.out.println(i + ": " + lfs.all_files.elementAt(i).this_file.length());     // for test
					}
					
				}
			}
		}
		
		void update_hdfs_file(String path) throws IOException
		{
			String outpath =  Lfile.hdfs_server + path;
			String inpath = path + "_Real";
			
			Configuration conf = new Configuration();
			
			conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER"); 
			conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true"); 
			
			FileSystem fs = FileSystem.get(URI.create(outpath), conf);
			
			InputStream get_local_in = new BufferedInputStream(new FileInputStream(inpath));
			OutputStream server_out = fs.append(new Path(outpath));
			IOUtils.copyBytes(get_local_in, server_out, 4096, true);
			server_out.close();
			get_local_in.close();
			server_out.close();
			
			File f = new File(inpath);
			if (f.exists()) f.delete();
			
			fs.close();
		}
		
		public void do_update(Lfile file)
		{
			// for simple, we clear overcover this file.
			file.my_lock.lock();
			try {
				LogSys.shareInstance.Realize(file.this_file.getPath());
				update_hdfs_file(file.this_file.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.my_lock.unlock();
		}
}