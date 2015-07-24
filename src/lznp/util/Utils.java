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

/**
 * Utilities Class for LZNF
 * @author /u/Philboyd_Studge
 */
public class Utils {
    
    private static final int LOWERCASE_ASCII_MIN = 65;
    private static final int NUMBER_ASCII_MIN = 48;

    /**
     * Returns Hexadecimal String of specified number of digits
     * padding with zeroes if necessary
     * @param value integer to convert
     * @param len length of result
     * @return String 
     */
    public static String toHexString(int value, int len)
    {
        String retval = "";

        // we need to shift in groups of 4-bits this time
        for (int i = (len-1)*4; i >= 0; i-=4)
        {
            // shift rgb 4 bits at a time, mask
            int j = value >> i & 0xF;

            // if 10 - 15 we need a - f
            if (j > 9)
            {
                j += LOWERCASE_ASCII_MIN - 10; // ASCII characters a - f

            }
            else
            {
                j += NUMBER_ASCII_MIN; // ASCII characters 0 - 9
            }
            retval += (char) j; // convert ASCII int value to char
        }
        return retval;
    } 
    
    /**
     * Convert byte[] bank of length 4 or less to integer.
     * @param bank byte[] bank
     * @return integer from bytes
     */
    public static int byteToInt(byte[] bank)
    {
        if (bank.length > 4) return -1;
        int retval = 0;
        int up = 0;
        for (int i = bank.length - 1; i >= 0; i--)
        {
                retval += ((int) (bank[up] & 0xff) << (i * 8));
                up++;
        }

        return retval;
    }
    
    /**
     * Convert integer to byte[] array
     * @param value
     * @return byte[] array
     */
    public static byte[] intToByte(int value)
    {
        byte[] b = new byte[4];
        b[0] = (byte) ((value >> 24) & 0xFF);
        b[1] = (byte) ((value >> 16) & 0xFF);
        b[2] = (byte) ((value >> 8) & 0xFF);
        b[3] = (byte) (value & 0xFF);
        return b;
    }

    /**
     * byte array to hex string
     * for debugging purposes 
     * @param in byte array
     * @return String hex array 32 bytes per line
     */
    public static String bankToString(byte[] in)
    {
        String ret = "";
        for (int i = 0; i < in.length; i++)
        {
            if (i % 32 == 0) ret += "\n";
            ret += toHexString((int) (in[i] & 0xff), 2) + " ";
        }
        return ret;
    }

}
