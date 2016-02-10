package localfile;

import mythread.*;
import java.io.*;
import java.net.URI;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import Log.LogSys;
import mythread.Update;

public class Lfile {
	public File this_file;
	public LFSystem lfs;
	public Lock my_lock;
	public static String hdfs_server = "hdfs://127.0.0.1:9000/";
	public static FileSystem fs = null;
	public static Lock fs_lock = new ReentrantLock();
	Configuration conf = null;
	
	public Lfile(String path, LFSystem the_lfs)
	{
		conf = new Configuration();
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER"); 
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true"); 
		
		lfs = the_lfs;
		if(lfs.find_file(path) != null){
			this_file = lfs.find_file(path).this_file;
			my_lock = lfs.find_file(path).my_lock;
		}
		else{
			my_lock =  new ReentrantLock();
			creat_file(path);
//			my_lock =  new ReentrantLock();
			register(this,lfs);
		}
	}
	
	String get_dir(String path)
	{
		int p = path.lastIndexOf('/');
		return path.substring(0, p);
	}
	
	public void creat_file(String path)
	{
		this_file = new File(path);
		path = hdfs_server + path;
		System.out.println(path);
		if (!this_file.getParentFile().exists()) {  
            if (!this_file.getParentFile().mkdirs()) {  
            }  
        }
		if(!this_file.exists()){
			try {
				this_file.createNewFile();
				create_hdfs_dir(get_dir(path));
				create_hdfs_file(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void create_hdfs_dir(String server_path) throws IOException
	{
//		local_path = "/home/nsknojj/log.txt";		
		System.out.println("Server_path:" + URI.create(server_path));
		fs = FileSystem.get(URI.create(server_path), conf);
		fs.mkdirs(new Path(server_path));
//		fs.close();
	}
	
	public void create_hdfs_file(String path) throws IOException
	{
		fs = FileSystem.get(URI.create(path), conf);
		OutputStream out = fs.create(new Path(path));
		out.close();
//		fs.close();
	}
	
	public void register(Lfile file, LFSystem the_lfs)
	{
		the_lfs.all_files.addElement(file);
	}
	
	public void close()
	{
		try {
			my_lock.lock();
			LogSys.shareInstance.LogFile();
			fs.close();
			my_lock.unlock();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void lf_write(String data)
	{
		fs_lock.lock();
		my_lock.lock();
		LogSys.shareInstance.Append(this_file.getPath(), data);
		my_lock.unlock();
		fs_lock.unlock();
	}
	
	public void lf_read(byte[] buffer, int start, int len) throws IOException
	{
		fs_lock.lock();
		my_lock.lock();
		do_update();
//		my_lock.unlock();
				
		String path = hdfs_server + this_file.getPath();
		InputStream in = null;
		try {		
			in = fs.open(new Path(path));
		}
		catch (Exception e) {
			fs = FileSystem.get(URI.create(path), conf);
			in = fs.open(new Path(path));
		}
		in.read(buffer, start, len);
		in.close();
		my_lock.unlock();
		fs_lock.unlock();
	}
	
	void update_hdfs_file(String path) throws IOException
	{
		String outpath =  Lfile.hdfs_server + path;
		String inpath = path + "_Real"; 
		
		InputStream get_local_in = new BufferedInputStream(new FileInputStream(inpath));
		
		OutputStream server_out = null;
		try {
			server_out = fs.append(new Path(outpath));
		}
		catch (Exception e) {
			fs = FileSystem.get(URI.create(outpath), conf);
			server_out = fs.append(new Path(outpath));
		}
		
		IOUtils.copyBytes(get_local_in, server_out, 4096, true);
		server_out.close();
		get_local_in.close();
		server_out.close();
		
		File f = new File(inpath);
		if (f.exists()) f.delete();
		
//		fs.close();
	}
	
	public void do_update()
	{
		// for simple, we clear overcover this file.
//		my_lock.lock();
		try {
			LogSys.shareInstance.Realize(this_file.getPath());
			update_hdfs_file(this_file.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		my_lock.unlock();
	}	
	
	public void list()
	{
		LogSys.shareInstance.List();
	}
	
	public void delete(int num)
	{
		LogSys.shareInstance.Delete(num);
	}
}