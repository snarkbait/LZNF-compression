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

import lznp.util.Bank;
import lznp.util.FileIO;
import lznp.util.LZNFFile;

/**
 * LZNF main class for LZNF compression/decompression
 * @author /u/Philboyd_Studge
 */
public class LZNF {
    
    String fileName;
    String outFile;
    Bank inputBank;
    Bank outputBank;

    /**
     * Creates instance for decompression from fileName
     * @param fileName 
     */
    public LZNF(String fileName)
    {
        this.fileName = fileName;
        this.inputBank = FileIO.getBankFromFile(fileName);
    } 
    
    /**
     * Creates instance for compression from fileName, 
     * to be stored as outFile
     * @param fileName
     * @param outFile 
     */
    public LZNF(String fileName, String outFile)
    {
        this.fileName = fileName;
        this.outFile = outFile;
        this.inputBank = FileIO.getBankFromFile(fileName);
    }

    /**
     * Compress and save
     */
    public void Compress()
    {
        Compress zip = new Compress(inputBank);
        zip.compress();
        zip.encodeHuffman();
        
        byte[] outStream = new LZNFFile(fileName, inputBank, zip.getTreeStream(), zip.getBitStream()).pack();
        FileIO.bufferToFile(outStream, outFile);
        //System.out.println("after huffman " + outputBank.size());
    }

    /**
     * Decompress and save
     */
    public void Decompress()
    {
        LZNFFile ifile = new LZNFFile(inputBank);
        Decompress unzip = new Decompress(ifile);
        unzip.decompress();
        FileIO.bufferToFile(unzip.getOutstream(), ifile.getHeader().getFileName());
    }
    
    public static void main(String[] args)
    {

        LZNF lz = new LZNF("enwik6", "enwik6.lzp");
        lz.Compress();
        LZNF dz = new LZNF("enwik6.lzp");
        dz.Decompress();
    }
}
