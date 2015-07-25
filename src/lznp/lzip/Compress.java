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
import lznp.huffman.HuffmanTree;
import lznp.util.Bank;
import lznp.util.BitStream;
import lznp.util.Utils;

/**
 * Compress class
 * Compresses file data using LZNF order-3 (LZP encoding using no flags for literal/length)
 * and then Huffman encodes the compressed data
 * @author /u/Philboyd_Studge
 */
public class Compress
{
    private final byte[] inStream;
    private byte[] outStream;
    private BitStream bitStream;
    private BitStream treeStream;
    private int[] frequency = new int[256];
    private HashMap<Integer, Integer> hashTable;

    /**
     * Constructor, takes a Bank as input
     * @param inBank Bank with byte array of file to be compressed
     */
    public Compress(Bank inBank)
    {
        this.inStream = inBank.getBank();
        this.outStream = new byte[inStream.length * 2];
        this.bitStream = new BitStream();
        hashTable = new HashMap<>();
    }
    
     /**
     * Add byte value as index to frequency table
     * check if count is getting quite large
     * @param index byte value of output stream
     */
    private void addFrequency(int index)
    {
        if (frequency[index & 0xff] >= Integer.MAX_VALUE/2) reduceFrequencies();
        frequency[index & 0xff]++;
    }

    /**
     * reduce frequency counts by half
     */
    private void reduceFrequencies()
    {

        for (int i = 0; i < frequency.length; i++)
        {
            if (frequency[i] > 10)
            {
                 frequency[i] /= 2;
            }          
        }
        
    }

    /**
     * LZNF routine, based on LZP by Charles Bloom
     */
    public void compress()
    {
        int matchLen;
        int current = 0;
        int pointer;
        int outPointer = 0;
        
        
        // load first three bytes as literals
        for (int i = 0; i < 3; i++)
        {
            outStream[i] = inStream[i];
            addFrequency((byte) inStream[i] & 0xff);
            current++;
            outPointer++;
        }
        
        // main compression loop
        while (current < inStream.length)
        {
            // get context
            byte[] contextBytes = { inStream[current - 3], inStream[current - 2], inStream[current - 1]};
            int context = Utils.byteToInt(contextBytes);
            
            // see if it is already in the hashTable, if so, get last pointer
            if (hashTable.containsKey(context))
            {
                pointer = hashTable.get(context);
            }
            else
            {
                hashTable.put(context, 0);
                pointer = 0;
            }
            
            // put current pointer to hash table
            hashTable.put(context, current);
            
            if (pointer > 0)
            {
                matchLen = 0;
                while (inStream[pointer] == inStream[current])
                {
                    pointer++;
                    current++;
                    matchLen++;
                    if (inStream.length - current <= 3) break;
                }
                if (matchLen > 0)
                {
                    while (matchLen >= 255)
                    {
                        outStream[outPointer] = (byte) 255;
                        addFrequency((byte)255);
                        outPointer++;
                        matchLen -= 255;
                    }

                    outStream[outPointer] = (byte) (matchLen & 0xff);
                    addFrequency((byte) matchLen);
                    outPointer++;
                        
                }
                else
                {
                    outStream[outPointer] = (byte) 0;
                    addFrequency((byte) 0);
                    outPointer++;
                }
            }

            if (current >= inStream.length) break;
            outStream[outPointer] = inStream[current];
            addFrequency((byte) inStream[current] & 0xff);
            current++;
            outPointer++;
        }
        
        outStream = Arrays.copyOfRange(outStream, 0, outPointer);
    }

    /**
     * Get Huffman tree based on frequency information from compress()
     * encode bytes to variable bit lengths and convert compressed output to BitStream
     */
    public void encodeHuffman()
    {
        // TODO: Logging
        HuffmanTree tree = new HuffmanTree(frequency);
        treeStream = tree.getBitTree();
        String[] codes = tree.getCodes();
        bitStream = new BitStream(outStream.length);
        for (int i = 0; i < outStream.length; i++)
        {
            bitStream.pushBits(codes[outStream[i] & 0xff]);
        }
        bitStream.close();
        
    }

    /**
     * get treeStream
     * @return BitStream of Huffman tree
     */
    public BitStream getTreeStream() { return treeStream; }
    
    /**
     * get bitStream
     * @return BitStream of Huffman-encoded data
     */
    public BitStream getBitStream() { return bitStream; }
    
    
}
