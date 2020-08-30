/** Ben F Rayfield offers this SimpleBlobTable software opensource MIT license */
package simpleblobtable.util;

public class Binary{
	
	public static void copyLongIntoByteArray(byte[] b, int byteIndex, long j){
		for(int i=7; i>=0; i--){
			b[byteIndex+i] = (byte)j;
			j>>=8;
		}
	}
	
	public static long readLongFromByteArray(byte[] b, int byteIndex){
		long j = 0;
		for(int i=0; i<8; i++){
			j = (j<<8)|(b[byteIndex+i]&0xff);
		}
		return  j;
	}

}
