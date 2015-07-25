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
import lznp.exception.NotValidFileException;

/**
 * FileHeader class
 * file utils for LZNF
 * create unique file header for compressed file
 * @author /u/Philboyd_Studge
 */
public class FileHeader
{
    private final byte[] HEADER_TAG = { 0x4c, 0x5a, 0x4e, 0x46 }; // header 'LZNF'
    private byte[] header;
    
    // TODO : get rid of magic numbers

    private String fileName;
    private int crc32;
    private int fileLength;
    private int nameLength;
    private int dataOffset;

    /**
     * constructor for compress
     * @param fileName name of file
     * @param fileBank bank with file data
     */
    public FileHeader(String fileName, Bank fileBank)
    {
        this.fileName = fileName;
        this.nameLength = fileName.length();
        this.fileLength = fileBank.size();
        this.crc32 = (int) fileBank.getCRC32() & 0xffffffff;

        makeHeader(fileBank);
    }

    /**
     * Create FileHeader from existing file as Bank
     * Assumes validity of file already checked
     * @param fileBank Bank with compressed file
     */
    public FileHeader(Bank fileBank)
    {
        this.isLZNF(fileBank);
        this.extract(fileBank);
    }

    /**
     * Get byte array with header information
     * @return byte array
     */
    public byte[] getHeader()
    {
        return header;
    }

    /**
     * test if first four bytes are 'LZNF'
     * @param fileBank Bank with compressed file
     */
    private void isLZNF(Bank fileBank)
    {
        byte[] tag = Arrays.copyOfRange(fileBank.getBank(), 0, 4);
        if (!Arrays.equals(tag, HEADER_TAG))
        {
            throw new NotValidFileException("Not a valid LZNF File.");
        }
              
    }

    /**
     * get value of position in file where actual
     * compressed data begins
     * @return integer offset for file data
     */
    public int getDataOffset()
    {
        return dataOffset;
    }
 
    /**
     * get original file length
     * @return integer file length
     */
    public int getFileLength()
    {
        return fileLength;
    }
    
    public int getCRC32()
    {
        return crc32;
    }

    /**
     * get original file name
     * @return String file name
     */
    public String getFileName() { return fileName; }

    /**
     * get length of file name
     * @return integer length of file name
     */
    public int getNameLength() { return nameLength; }

    /**
     * set data offset
     * this must be done manually after the huffman tree is generated
     * @param offset integer value for position in file of data
     */
    public void setDataOffset(int offset)
    {
        this.dataOffset = offset;
        System.arraycopy(Utils.intToByte(offset), 0, header, 16, 4);
    }

    /**
     * make the file header
     * @param fileBank byte array of header information
     */
    private void makeHeader(Bank fileBank)
    {
        byte[] name = fileName.getBytes();
        byte[] fileLen = Utils.intToByte(fileLength);
        byte[] crc = Utils.intToByte((int) crc32 &0xffffffff);
        byte[] nameLen = Utils.intToByte(nameLength);

        header = new byte[20 + name.length];
        System.arraycopy(HEADER_TAG, 0, header, 0, 4);
        System.arraycopy(fileLen, 0, header, 4, 4);
        System.arraycopy(crc, 0, header, 8, 4);
        System.arraycopy(nameLen, 0, header, 12, 4);
        //System.arraycopy(Utils.intToByte(24 + name.length), 0, header, 16, 4);
        System.arraycopy(name, 0, header, 20, name.length);

    }

    /**
     * extract header information from compressed file
     * @param fileBank Bank of file byte array
     */
    private void extract(Bank fileBank)
    {
        byte[] inBank = fileBank.getBank();
        this.fileLength = Utils.byteToInt(Arrays.copyOfRange(inBank, 4, 8));
        this.crc32 = Utils.byteToInt(Arrays.copyOfRange(inBank, 8, 12));
        this.nameLength = Utils.byteToInt(Arrays.copyOfRange(inBank, 12, 16));
        this.dataOffset = Utils.byteToInt(Arrays.copyOfRange(inBank, 16, 20));
        this.fileName = new String(Arrays.copyOfRange(inBank, 20, 20 + nameLength));

    }

}
