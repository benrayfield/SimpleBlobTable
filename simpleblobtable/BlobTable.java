/** Ben F Rayfield offers this SimpleBlobTable software opensource MIT license */
package simpleblobtable;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import simpleblobtable.util.Binary;
import simpleblobtable.util.Files;
import simpleblobtable.util.Text;

public class BlobTable{
	
	public BlobTable(File dir){
		this.dir = dir;
		System.out.println("BlobTable at "+dir);
	}
	
	public final String fileNameSuffix = ".sha3_256_bitlen64";
	
	/** map of hex(key) to Blob whose key that is.
	TODO WeakHashMap? Might not want to keep all of them in memory, but they are small so probably ok to keep a few million in memory.
	*/
	protected Map<String,Blob> blobs = new HashMap();
	
	protected final File dir;
	
	protected Blob getBlobByVal(byte[] val){
		byte[] key = keyOf(val);
		return getBlobByKeyAndVal(key, val);
	}
	
	/** file may exist or not. Its the file it would go in. */
	public File file(Blob b){
		return new File(new File(dir,b.keyHex.substring(0,3)),b.keyHex+fileNameSuffix);
	}
	
	protected Blob getBlobByKey(byte[] key){
		String keyHex = Text.bytesToHex(key); //a substring of Blob.keyAsString
		Blob b;
		synchronized(blobs){
			b = blobs.get(keyHex);
			if(b == null){
				//TODO optimize by doing this outside the synchronized since it might be delayed by file system just by creating File object
				//even though it probably wont touch harddrive to do that?
				b = new Blob(this, key);
				blobs.put(keyHex, b);
			}
		}
		return b;
	}
	
	/** it will break things if key is not the bytes keyOf(val) */
	protected Blob getBlobByKeyAndVal(byte[] key, byte[] val){
		Blob b;
		synchronized(blobs){
			String keyHex = Text.bytesToHex(key);
			b = blobs.get(keyHex);
			if(b == null){
				b = new Blob(this, key, val);
				blobs.put(keyHex, b);
			}
		}
		return b;
	}
	
	/** returns val as string from hex of key */
	public String getStr(String keyHex){
		byte[] key = Text.hexToBytes(keyHex);
		byte[] val = get(key);
		return Text.bytesToStr(val);
	}
	
	public byte[] get(String keyHex){
		byte[] key = Text.hexToBytes(keyHex);
		return get(key);
	}
	
	/** retuSAERFasfferns hex of key */
	public String put(String valAsString){
		byte[] val = Text.strToBytes(valAsString);
		byte[] key = put(val);
		return Text.bytesToHex(key);
	}
	
	public byte[] get(byte[] key){
		return getBlobByKey(key).val();
	}
	
	/** returns key */
	public byte[] put(byte[] val){
		byte[] key = keyOf(val);
		put(key, val);
		return key;
	}
	
	public void delete(byte[] key){
		getBlobByKey(key).delete();
	}
	
	/** FIXME sha3_256 hash val */
	public byte[] keyOf(byte[] val){
		try{
			//TODO optimize by sharing a single instance or cloning it? Or is this faster?
			//This is faster for multithreaded. the other may be faster for single threaded. so keep it this way at least for now.
			long bitLen = ((long)val.length)<<3;
			byte[] hash = MessageDigest.getInstance("SHA3-256").digest(val);
			byte[] key = new byte[hash.length+8];
			System.arraycopy(hash, 0, key, 0, hash.length);
			Binary.copyLongIntoByteArray(key, hash.length, bitLen);
			return key;
		}catch (NoSuchAlgorithmException e){ throw new Error(e); }
	}
	
	/** key's last 64 bits are bit len of content it points at */
	public long pointsAtBitLen(byte[] key){
		return Binary.readLongFromByteArray(key, key.length-8);
	}
	
	/** not a real key. nextKey(keyAll0s()) is the first key */
	public byte[] keyAll0s(){
		return new byte[40];
	}
	
	/** For looping through the keys. Returns null if there is no more. */
	public byte[] nextKey(byte[] any40Bytes){
		throw new Error("TODO");
	}
	
	/** how many keys would nextKey loop through */
	public long numKeys(){
		throw new Error("TODO count all the files in the 4096 dirs whose name ends with .SHA3_256_bitlen64, or could keep a count in memory but what if someone manually deletes certain files");
	}
	
	/** in bits, how much data+wastedSpace is stored for everything (maybe just for this class BlobTable?) */
	public long storageSize(){
		throw new Error("TODO");
	}
	
	/** dont need this cuz Blob saves as soon as its created.
	any blobs that are only in memory, store them to harddrive *
	public static void saveAll(){
		TODO
	}*/
	
	/** WARNING: will break things if key is not the same bytes as keyOf(val),
	but you can call this for efficiency if you already have the key. Else use put(byte[] val).
	*/
	public void put(byte[] key, byte[] val){
		getBlobByKeyAndVal(key, val);
	}
	
	
	public static void main(String... args){
		
		BlobTable bt = new BlobTable(new File(Files.dirWhereThisProgramStarted,"blob"));
		
		boolean createInsteadOfGet = true;
		String keyOfAbc, keyOfAabbcc;
		if(createInsteadOfGet){
			keyOfAbc = bt.put("abc"); //must do this once to create the files
			keyOfAabbcc = bt.put("aabbcc"); //must do this once to create the files
		}else{
			keyOfAbc = Text.bytesToHex(bt.keyOf(Text.strToBytes("abc"))); //then do this
			keyOfAabbcc = Text.bytesToHex(bt.keyOf(Text.strToBytes("aabbcc"))); //then do this
		}
		String abc = bt.getStr(keyOfAbc);
		String aabbcc = bt.getStr(keyOfAabbcc);
		System.out.println("keyOfAabbcc="+keyOfAabbcc+" val="+aabbcc);
		System.out.println("bitlen(keyOfAabbcc)="+bt.pointsAtBitLen(Text.hexToBytes(keyOfAabbcc)));
		if(!abc.equals("abc")) throw new Error("test failed: abc");
		if(!aabbcc.equals("aabbcc")) throw new Error("test failed: aabbcc");
		System.out.println("blobtable tests passed");
		
		bt.delete(bt.keyOf(Text.strToBytes("aabbcc")));
	}
  
}
