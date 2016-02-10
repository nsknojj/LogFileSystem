import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

public class LFS {
	public String server_path;
	public String local_path;
	public String filename;
	public FileSystem fs = null;
	public OutputStream out = null;
	public InputStream in = null;
	public Configuration conf = null;
	
	public LFS(String hdfs_path) throws IOException
	{
		local_path = "/home/nsknojj/log.txt";
		server_path = hdfs_path;
		conf = new Configuration();
		
		conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER"); 
		conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true"); 
		
		System.out.println(URI.create(server_path));
		fs = FileSystem.get(URI.create(server_path), conf);
	
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
	
	public OutputStream out_stream() throws IOException
	{
		if (out == null)
				out = new BufferedOutputStream(new FileOutputStream(local_path));
		return out;
	}
	
//	public void upload()
//	{
//		try {
//			if (out != null) out.close();
//			InputStream get_loacal_in = new 
//			        BufferedInputStream(new FileInputStream(local_path));
//			OutputStream server_out = fs.append(new Path(server_path));
//			IOUtils.copyBytes(get_loacal_in, server_out, 4096, true);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public InputStream in_stream() throws IOException
	{
//		InputStream _ret = null;
//				upload();
//			InputStream server_in = fs.open(new Path(server_path));
			in = fs.open(new Path(server_path));
//			_ret = server_in;
 
		return in;
	}
	
//	public void upFile(String localFile,String hdfsPath) throws IOException{  
//        InputStream in=new BufferedInputStream(new FileInputStream(localFile));  
//        OutputStream out=fs.create(new Path(hdfsPath));  
//        IOUtils.copyBytes(in, out, conf);  
//    }
	
	public void create() throws IOException
	{
		out = fs.create(new Path(server_path));
		out.close();
	}
	
	public void del() throws IOException
	{
		fs.delete(new Path(server_path), true);
	}
	
	public void reformat() throws IOException
	{
		del();
		create();
	}
	
	public void read(int start, int len) throws IOException
	{
		byte[] b = new byte[5000];
		in_stream();
		in.read(b, start, len);
		in.close();
//		String s = new String(b, "UTF-8");
//		System.out.println(s);
	}
	
	public void write(String s) throws IOException
	{
//		if (out != null) out.close();
		InputStream get_local_in = new ByteArrayInputStream(s.getBytes("UTF-8"));
		OutputStream server_out = fs.append(new Path(server_path));
		IOUtils.copyBytes(get_local_in, server_out, 4096, true);
		server_out.close();
	}
	
}
