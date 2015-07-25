/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lznp.lzip;

import java.util.Arrays;
import java.util.HashMap;
import lznp.exception.InvalidChecksumException;
import lznp.huffman.HuffmanTree;
import lznp.util.BitStream;
import lznp.util.FileIO;
import lznp.util.LZNFFile;
import lznp.util.Utils;

/**
 * Decompress class
 * @author /u/Philboyd_Studge
 */
public class Decompress
{
    private final BitStream treeStream;
    private final BitStream bitStream;
    private final int origCRC32;
    private final int origSize;
    private byte[] inStream;
    private byte[] outStream;
    
    private HashMap<Integer, Integer> hashTable;
    
    public Decompress(LZNFFile inFile)
    {
        treeStream = inFile.getTreeStream();
        bitStream = inFile.getBitStream();       
        inStream = new byte[inFile.getHeader().getFileLength() * 2];  
        origCRC32 = inFile.getHeader().getCRC32();
        origSize = inFile.getHeader().getFileLength();
        hashTable = new HashMap<>();
    }

    /**
     * get output byte array
     * @return byte array of output stream
     */
    public byte[] getOutstream() { return outStream; }

    /**
     * decompress file
     */
    public void decompress()
    {
        decodeHuffman();
        decode();
        checkCRC32();
    }

    /**
     * decode LZNF data
     */
    private void decode()
    {
        outStream = new byte[origSize * 2];
        
        int matchLen;
        int current = 0;
        int outPointer = 0;
        int pointer;
        
        for (int i = 0; i < 3; i++)
        {
            outStream[i] = inStream[i];
            current++;
            outPointer++;
        }
        
        while (current < inStream.length)
        {
            byte[] contextBytes = { outStream[outPointer - 3], outStream[outPointer - 2], outStream[outPointer - 1] };
            int context = Utils.byteToInt(contextBytes);
            if (hashTable.containsKey(context))
            {
                pointer = hashTable.get(context);
            }
            else
            {
                pointer = 0;
            }
            hashTable.put(context, outPointer);
            
            if (pointer > 0)
            {
                matchLen = (int) (inStream[current] & 0xff);
               if (matchLen == 255)
                {
                    int lenCount = 1;
                    int nextLen = matchLen;
                    while (nextLen == 255)
                    {
                        nextLen = (int) inStream[current + lenCount] & 0xff;
                        matchLen += nextLen;
                        lenCount++;
                    }
                    current += lenCount - 1;
                }
                
                while (matchLen > 0)
                {
                    outStream[outPointer] = outStream[pointer];
                    pointer++;
                    outPointer++;
                    matchLen--;
                }
                current++;
                if (current > inStream.length - 1) { System.out.println("op " + outPointer); break; }
                outStream[outPointer] = inStream[current];
                outPointer++;
                current++;
            }
            else
            {
                outStream[outPointer] = inStream[current];
                outPointer++;
                current++;                            
            }
        }
        outStream = Arrays.copyOfRange(outStream, 0, outPointer);
        //FileIO.bufferToFile(outStream, "tmp.txt");
    }

    /**
     * rebuild Huffman tree and decode BitStream
     */
    private void decodeHuffman()
    {
        HuffmanTree ht = new HuffmanTree(treeStream);
        int current = 0;
        while (!bitStream.EOB())
        {
            inStream[current] = (byte) (ht.getCode(bitStream) & 0xff);
            current++;
        }
        
        //trim
        inStream = Arrays.copyOf(inStream, current);
        //System.out.println("instream length " + inStream.length);
        //System.out.println(Utils.bankToString(inStream));
        
   }
    
   private void checkCRC32()
   {
       int newCRC = (int) (FileIO.getCRC32(outStream) &0xffffffff);
       if (origCRC32 != newCRC) throw new InvalidChecksumException("Error in decompressing file or corrupted file.");
   }
}
