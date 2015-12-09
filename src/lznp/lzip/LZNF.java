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

import lznp.exception.NotValidFileException;
import lznp.util.Bank;
import lznp.util.FileIO;
import lznp.util.LZNFFile;

/**
 * LZNF main class for LZNF compression/decompression
 * Based of LZP modification idea by Lucas Marsh
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
        if (this.inputBank == null) throw new NotValidFileException("File not found.");
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
        if (this.inputBank == null) throw new NotValidFileException("File not found.");
    }

    /**
     * Compress and save
     */
    public void Compress()
    {
        Compress zip = new Compress(inputBank);
        long time = System.currentTimeMillis();
        zip.compress();
        //zip.encodeHuffman();
        System.out.println("Elapsed no file funcs:" + (System.currentTimeMillis()- time));
        
        byte[] outStream = new LZNFFile(fileName, inputBank, zip).pack();
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

    /*
    * test code
    */
    public static void main(String[] args)
    {

        LZNF lz = new LZNF("sharnd.out", "sharnd.tst");
        lz.Compress();
       // LZNF dz = new LZNF("arrays.lzp");
        //dz.Decompress();
    }
}
