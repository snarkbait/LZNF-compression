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

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;

/**
 * Static File Utilities
 * @author /u/Philboyd_Studge
 */
public class FileIO {
    /**
     * 
     * @param fileName
     * @return File object
     */
    public static File getFile(String fileName)
    {
        return new File(fileName);
    }

    /**
     * 
     * @param fileName
     * @return Bank of file bytes
     */
    public static Bank getBankFromFile(String fileName)
    {
        byte[] buffer;
        try
        {
            File input = getFile(fileName);    
            Path inputPath = input.toPath();
            buffer = Files.readAllBytes(inputPath);
        }
        catch (IOException ioe)
        {
                // TODO : change to Logger
                //System.err.println(ioe.getMessage());
                return null;
        }
        return new Bank(buffer);        
    }

    /**
     * Creates a new file of name filename
     * @param outStream byte array to push to file
     * @param newfile filename/path
     */
    public static void bufferToFile(byte[] outStream, String newfile)
    {
        try
        {
            Path outputPath = new File(newfile).toPath();
            Files.write(outputPath, outStream);
        }
        catch (IOException ioe)
        {
            // TODO : Change to Logger
            System.err.println(ioe.getMessage());
        }
    }
    
    /**
     * get CRC32 checksum from byte array
     * @param bank byte array
     * @return long CRC32 value
     */
    public static long getCRC32(byte[] bank)
    {
            CRC32 crc32 = new CRC32();
            crc32.update(bank);
            return crc32.getValue();
    }
    
    
}
