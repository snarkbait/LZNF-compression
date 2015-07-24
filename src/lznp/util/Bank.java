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

/** Class<b>Bank</b><br>
 * Creates memory bank byte[ ] arrays and automatically gets the <b>CRC32</b> checksum.<br>
 * 
 * @author /u/Philboyd_Studge
 */
public class Bank {
    
    private final byte[] bank;
    private long crc32;

    /**
     * creates a byte Bank of given size
     * @param size integer size of bank
     */
    public Bank(int size)
    {
        bank = new byte[size];
        crc32 = 0;
    }

    /**
     * Creates a byte bank from byte array
     * @param b byte array
     */
    public Bank(byte[] b)
    {
        bank = b;
        setCRC32();
    }

    /**
     * get byte array from bank
     * @return byte array
     */
    public byte[] getBank()
    {
        return bank;
    }

    /**
     * size of bank
     * @return integer size of bank
     */
    public int size()
    {
        return bank.length;
    }

    /**
     * set CRC32 from FileIO
     */
    private void setCRC32()
    {
        crc32 = FileIO.getCRC32(bank);
    }

    /**
     * get CRC32 checksum
     * @return long CRC32 checksum
     */
    public long getCRC32()
    {
        return crc32;
    }

    /**
     * get CRC32 checksum as hexadecimal string
     * @return String Hex string
     */
    public String getCRC32Hex()
    {
        return "0x" + Utils.toHexString((int)crc32,8);
    }
    
    
}
