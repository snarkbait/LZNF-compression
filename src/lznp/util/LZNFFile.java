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
package lznp.util;

import java.util.Arrays;

/**
 * LZNFFile class
 * 
 * @author /u/Philboyd_Studge
 */
public class LZNFFile
{
    private final FileHeader header;
    private BitStream treeStream;
    private BitStream bitStream;

    /**
     * for decompress
     * @param fileBank Bank of compressed data
     */
    public LZNFFile(Bank fileBank)
    {
	header = new FileHeader(fileBank);
        getStreams(fileBank);
    }

    /**
     * for compress
     * @param fileName name of original file
     * @param inBank bank of bytes of original file
     * @param treeStream BitStream of Huffman tree
     * @param bitStream BitStream of encoded data
     */
    public LZNFFile(String fileName, Bank inBank, BitStream treeStream, BitStream bitStream)
    {
        this.header = new FileHeader(fileName, inBank);
        this.treeStream = treeStream;
        this.bitStream = bitStream;
        System.out.println("header length " + header.getHeader().length);
        System.out.println("tree stream len " + treeStream.length());
    }

    /**
     * pack all parts of file together for saving
     * @return byte array of complete compressed data with header
     */
    public byte[] pack()
    {
        int length = header.getHeader().length + treeStream.length() + bitStream.length();
        header.setDataOffset(header.getHeader().length + treeStream.length());
        byte[] outBank = new byte[length];
        System.arraycopy(header.getHeader(), 0, outBank, 0, header.getHeader().length);
        System.arraycopy(treeStream.getBank(), 0, outBank, header.getHeader().length, treeStream.length());
        System.arraycopy(bitStream.getBank(), 0, outBank, header.getHeader().length + treeStream.length(), bitStream.length());
        return outBank;
    }

    /**
     * get individual streams from file
     * @param fileBank Bank of file to decompress
     */
    private void getStreams(Bank fileBank)
    {
        int offset = header.getDataOffset();
        treeStream = new BitStream(Arrays.copyOfRange(fileBank.getBank(), 20 + header.getNameLength(), offset));
        bitStream = new BitStream(Arrays.copyOfRange(fileBank.getBank(),offset,fileBank.size()));
    }

    /**
     * get Huffman tree
     * @return BitStream of Huffman tree
     */
    public BitStream getTreeStream()
    {
        return treeStream;
    }

    /**
     * get encoded data
     * @return BitStream of encoded data
     */
    public BitStream getBitStream()
    {
        return bitStream;
    }

    /**
     * get FileHeader
     * @return FileHeader
     */
    public FileHeader getHeader()
    {
        return header;
    }
                        
}
