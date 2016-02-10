import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;

/**
 * blog: http://www.iteblog.com/
 * Date: 14-1-2
 * Time: 下午6:09
 */
public class WordCount {
    public static void main(String[] args) {
        String hdfs_path = "hdfs://127.0.0.1:9000/a.txt";//文件路径
        Configuration conf = new Configuration();
        conf.setBoolean("dfs.support.append", true);

        String inpath = "/home/nsknojj/append.txt";
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