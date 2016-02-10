import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

public class Update {
	static String server_path = "hdfs://127.0.0.1:9000";
	public static void setServerPath(String _path) {
		server_path = _path;
	}
    public static void appendContent(String path, String inpath) {
    	if (path.substring(0, 1) != "/")
    		path = '/' + path;
        String hdfs_path = server_path + path;//文件路径
        Configuration conf = new Configuration();
        conf.setBoolean("dfs.support.append", true);

        FileSystem fs = null;
        try {
            fs = FileSystem.get(URI.create(hdfs_path), conf);
            //要追加的文件流，inpath为文件
            InputStream in = new 
                  BufferedInputStream(new FileInputStream(inpath));
            OutputStream out = fs.append(new Path(hdfs_path));
            IOUtils.copyBytes(in, out, 4096, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}