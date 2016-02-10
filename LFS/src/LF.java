import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

public class LF {
	public String server_path;
	public String local_path;
	public String filename;
	public FileSystem fs = null;
	public OutputStream out = null;
	public InputStream in = null;
	
	public LF(String hdfs_path)
	{
		local_path = "/home/nsknojj/log.txt";
		server_path = hdfs_path;
		Configuration conf = new Configuration();
		
		try {
			System.out.println(URI.create(server_path));
			fs = FileSystem.get(URI.create(server_path), conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void finalize()
	{
		try {
			fs.close();
			if (in != null) in.close();
			if (out != null) out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public OutputStream out_stream()
	{
		if (out == null)
			try {
				out = new 
				        BufferedOutputStream(new FileOutputStream(local_path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return out;
	}
	
	public void upload()
	{
		try {
			if (out != null) out.close();
			InputStream get_loacal_in = new 
			        BufferedInputStream(new FileInputStream(local_path));
			OutputStream server_out = fs.append(new Path(server_path));
			IOUtils.copyBytes(get_loacal_in, server_out, 4096, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public InputStream in_stream()
	{
//		InputStream _ret = null;
		if (in == null)
			try {
				upload();
	//			InputStream server_in = fs.open(new Path(server_path));
				in = fs.open(new Path(server_path));
	//			_ret = server_in;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//		return _ret; 
		return in;
	}
	
	public void reformat()
	{
		try {
			upload();
			FSDataOutputStream fo = fs.create(new Path(server_path), true);
//			fo.write(2);
			fo.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
