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
    private byte[] matchStream;
    private CompressedBlock literals;
    private CompressedBlock matches;
    private HashMap<Integer, Integer> hashTable;

    /**
     * Constructor, takes a Bank as input
     * @param inBank Bank with byte array of file to be compressed
     */
    public Compress(Bank inBank)
    {
        this.inStream = inBank.getBank();
        this.outStream = new byte[inStream.length * 2];
        this.matchStream = new byte[inStream.length];
        literals = new CompressedBlock();
        matches = new CompressedBlock();
        hashTable = new HashMap<>();
    }
    
    public CompressedBlock getLiterals()
    {
        return literals;
    }
    
    public CompressedBlock getMatches()
    {
        return matches;
    }
    
     /**
     * LZNF routine, based on LZP by Charles Bloom, modification idea by Lucas Marsh
     */
    public void compress()
    {
        int matchLen;
        int current = 0;
        int pointer;
        int outPointer = 0;
        int matchPointer = 0;
        
        
        // load first three bytes as literals
        for (int i = 0; i < 4; i++)
        {
            outStream[i] = inStream[i];
            literals.addFrequency((byte) inStream[i] & 0xff);
            current++;
            outPointer++;
        }
        
        // main compression loop
        while (current < inStream.length)
        {
            // get context
            byte[] contextBytes = { inStream[current - 4], inStream[current - 3], inStream[current - 2], inStream[current - 1]};
            int context = Utils.byteToInt(contextBytes);
            
            // see if it is already in the hashTable, if so, get last pointer
            
            pointer = hashTable.getOrDefault(context, 0);
            
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
                        matchStream[matchPointer] = (byte) 255;
                        matches.addFrequency((byte)255);
                        matchPointer++;
                        matchLen -= 255;
                    }
                    
                    matchStream[matchPointer] = (byte) (matchLen & 0xff);
                    matchPointer++;
                    matches.addFrequency((byte) matchLen);
                       
                }
                else
                {
                    matchStream[matchPointer] = (byte) 0;
                    matchPointer++;
                    matches.addFrequency((byte) 0);
                }
            }

            if (current >= inStream.length) break;
            outStream[outPointer] = inStream[current];
            literals.addFrequency((byte) inStream[current] & 0xff);
            current++;
            outPointer++;
        }
        
        outStream = Arrays.copyOfRange(outStream, 0, outPointer);
        System.out.println("literal lengths: " + outStream.length);
        matchStream = Arrays.copyOfRange(matchStream, 0, matchPointer);
        
        literals.encodeHuffman(outStream);
        matches.encodeHuffman(matchStream);

    }  
}
