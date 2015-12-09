/*
 * Copyright (C) 2015 tim
 *
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

import java.nio.ByteBuffer;
import lznp.huffman.HuffmanTree;
import lznp.util.BitStream;

/**
 *
 * @author tim
 */
public class CompressedBlock
{
    private BitStream treeStream;
    private BitStream stream;
    private int[] frequencies;
    
    public CompressedBlock()
    {
        frequencies = new int[256];
    }
    
    public void addFrequency(int index)
    {
        if (frequencies[index & 0xff] >= Integer.MAX_VALUE / 2)
        {
            reduceFrequencies();
        }
        frequencies[index & 0xff]++;
    }
    
    private void reduceFrequencies()
    {
        for (int i = 0; i < frequencies.length; i++)
        {
            if (frequencies[i] > 10)
            {
                frequencies[i] /= 2;
            }
        }
    }
    
    public BitStream getTreeStream()
    {
        return treeStream;
    }
    
    public int getTreeStreamLength()
    {
        return treeStream.length();
    }
    
    public BitStream getStream()
    {
        return stream;
    }
    
    public int getStreamLength()
    {
        return stream.length();
    }
    
    public void encodeHuffman(byte[] outStream)
    {
        HuffmanTree tree = new HuffmanTree(frequencies);
        treeStream = tree.getBitTree();
        int[][] codes = tree.getCodes();
        stream = new BitStream(outStream.length);
        for (int i = 0; i < outStream.length; i++)
        {
            stream.pushBits(codes[0][outStream[i] & 0xff], codes[1][outStream[i] & 0xff]);
        }
        stream.close();        
    }
    
    public ByteBuffer pack()
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(treeStream.length() + stream.length() + 8);
        bb.putInt(treeStream.length());
        bb.put(treeStream.getBank());
        bb.putInt(stream.length());
        bb.put(stream.getBank());
        bb.flip();
        return bb;
    }
}
