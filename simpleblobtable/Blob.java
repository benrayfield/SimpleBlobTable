/** Ben F Rayfield offers this SimpleBlobTable software opensource MIT license */
package simpleblobtable;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Delayed;

import simpleblobtable.util.Files;
import simpleblobtable.util.Text;

/** a byte array that may be stored on harddrive and gets cached into memory as needed.
Dont modify the byte arrays.
*/
public class Blob{
	
	/** sha3_256 of val, for example. or might want to use multicodec/multihash prefix with that (TODO). */
	public final byte[] key;
	
	public final String keyHex;
	
	/** JVM deletes val bytes when jvm needs memory. val() can load again from File. */
	private SoftReference<byte[]> valCache;
	
	/** may exist or not. its where it would be stored */
	public final File file;

	public Blob(BlobTable bt, byte[] key, byte[] val){
		this(bt, key);
		Files.write(file, val);
		valCache = new SoftReference(val);
	}
	
	/** blobDir has 4096 dirs named as the first 3 hex digits of key.
	Inside that is file whose name is the full hex string of key concat ".sha3_256_bitlen64" and whose content is the bytes that hash to that key.
	*/
	public Blob(BlobTable bt, byte[] key){
		this.key = key;
		this.keyHex = Text.bytesToHex(key);
		this.file = bt.file(this);
	}
	
	public byte[] val(){
		byte[] val = null;
		if(valCache != null && (val=valCache.get()) != null){
			return val;
		}
		if(file.exists()){
			valCache = new SoftReference(val = Files.read(file));
			return val;
		}
		throw new Error("Couldnt find val (in memory or file) of "+this);
	}
	
	/** size in bits */
	public long valSize(){
		if(file.exists()) return file.length()<<3;
		byte[] valFromMemory = valCache.get();
		if(valFromMemory != null) return ((long)valFromMemory.length)<<8;
		throw new Error("Couldnt find val (in memory or file) of "+this);
	}
	
	/** deletes the File and cache in memory. Marks this Blob as deleted, but the java object still exists.
	Dont call this if you just want to let this Blob be garbcoled but keep the file.
	*/
	public void delete(){
		Files.delete(file);
		valCache.clear();
	}
	
	public String toString(){
		return keyHex;
	}

}