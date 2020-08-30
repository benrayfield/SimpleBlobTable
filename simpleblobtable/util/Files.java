/** Ben F Rayfield offers this SimpleBlobTable software opensource MIT license */
package simpleblobtable.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Files{
	
	public static final File dirWhereThisProgramStarted = new File(System.getProperty("user.dir")).getAbsoluteFile();
	
	/** returns null if not exist or is a dir*/
	public static byte[] read(File f){
		if(!f.exists() || !f.isFile()) return null;
		long len = f.length();
		if(len > Integer.MAX_VALUE) throw new Error("File is too big to fit in byte[]: "+len+" bytes.");
		//it may still run out of memory even if it could fit in a byte[]
		InputStream in = null;
		try{
			in = new FileInputStream(f);
			return in.readAllBytes();
		}catch(IOException e){
			throw new Error(e);
		}finally{
			if(in != null) try{ in.close(); }catch(IOException e){ throw new Error(e); }
		}
	}
	
	public static void write(File f, byte[] val){
		f.getParentFile().mkdirs();
		OutputStream out = null;
		try{
			out = new FileOutputStream(f);
			out.write(val);
		}catch(IOException e){
			throw new Error(e);
		}finally{
			if(out != null) try{ out.close(); }catch(IOException e){ throw new Error(e); }
		}
	}
	
	public static void delete(File f){
		if(!f.exists()) return;
		if(f.isFile()){
			f.delete();
		}else if(f.isDirectory()){
			throw new Error("TODO code to delete dir");
		}else{
			throw new Error("Unknown kind of File object: "+f);
		}
		if(f.exists()) throw new Error("Failed delete "+f);
	}

}
