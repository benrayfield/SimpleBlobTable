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

Also has InputStream (get) and File (put) for blobs too big to fit in byte[]. TODO OutputStream to temp file then move to file name by its hash.

//Close the program. Run again. Its still there except those deleted.

(Ben Rayfield is planning to use this or a variant of it in occamsfuncer for blob storage using tree hashing of a hash function given as a param since occamsfuncer generates its own id functions as its very self-referencing, and variants of this have been useful in a few earlier but smaller projects, and for increased security, there should probably be a param that maps id to dir instead of just using the first 12 bits of sha3_256 since someone could intentionally create many blobs that have the same first 12 bits which they might be motivated to if they knew thats how you were storing them. You want a mostly random spread of files into dirs else it may slow the OS. Also might have a param for number of dirs being a powOf2 since I might want 64k dirs in occamsfuncer cuz larger scale.)
