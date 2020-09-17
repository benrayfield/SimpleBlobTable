# SimpleBlobTable
simple storage and caching of many byte[] on 1 computer by its concat(sha3_256,int64BitLen) id, in 4096 dirs named as the first 3 hex digits of those ids. Ok for storing a few million files but for more you should use a more scaleable nosql software. Has been tested up to 300gB and 700k files, with biggest file being 70gB.

File dir = ...;

BlobTable bt = new BlobTable(dir);

byte[] key = ...40 bytes...;

long valSizeInBits = bt.pointsAtBitLen(key);

byte[] val = bt.get(key);

byte[] otherVal = ...;

byte[] otherKey = bt.put(otherVal);

bt.delete(key);

//Close the program. Run again. Its still there except those deleted.
