package localfile;

import java.io.File;
import java.util.Vector;

public class LFSystem {
	public Vector<Lfile> all_files = new Vector<Lfile>();
	
	public Lfile find_file(String path)
	{
		for(int i = 0; i < all_files.size(); i++){
//			System.out.println(all_files.elementAt(i).this_file.getPath()  + "?=?" + path);
			if(all_files.elementAt(i).this_file.getPath().equals(path)){
//				System.out.println(all_files.elementAt(i).this_file.getPath()  + "?=?" + path);
				return all_files.elementAt(i);
			}
		}
		return null;
	}
}