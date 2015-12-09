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

import java.nio.ByteBuffer;
import java.util.Arrays;
import lznp.lzip.Compress;
import lznp.lzip.CompressedBlock;

/**
 * LZNFFile class
 * 
 * @author /u/Philboyd_Studge
 */
public class LZNFFile
{
    private final FileHeader header;
    private CompressedBlock literals;
    private CompressedBlock matches;
    private BitStream treeStream;
    private BitStream bitStream;
    private BitStream matchStream;
    private BitStream matchTreeStream;

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
    public LZNFFile(String fileName, Bank inBank, Compress zip)
    {
        this.header = new FileHeader(fileName, inBank);
        this.literals = zip.getLiterals();
        this.matches = zip.getMatches();
    }

    /**
     * pack all parts of file together for saving
     * @return byte array of complete compressed data with header
     */
    public byte[] pack()
    {
        ByteBuffer bbLits = literals.pack();
        ByteBuffer bbMatches = matches.pack();
        byte[] lits = new byte[bbLits.capacity()];
        byte[] mats = new byte[bbMatches.capacity()];
        bbLits.get(lits);
        bbMatches.get(mats);
        int length = header.getHeader().length + lits.length + mats.length;
        header.setDataOffset(header.getHeader().length);
        byte[] outBank = new byte[length];
        System.arraycopy(header.getHeader(), 0, outBank, 0, header.getHeader().length);
        System.arraycopy(lits, 0, outBank, header.getHeader().length, lits.length);
        System.arraycopy(mats, 0, outBank, header.getHeader().length + lits.length, mats.length);
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
