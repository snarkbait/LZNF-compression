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
 * BitStream class
 * for reading and writing variable lengths of bits to a byte array
 * @author /u/Philboyd_Studge
 */
public class BitStream {
    
    private final int DEFAULT_SIZE = 256;

    // byte array for actual BitStream
    private byte[] bank;
    
    // boolean array representation of a single byte
    private boolean[] bits = new boolean[8];
    
    // current position in bit array
    private int bitPosition;
    
    // current position in byte array
    private int bytePosition;
    
    // are we at the end of the bank
    private boolean endOfBank = false;
    
    // number of leftover bits short of a full byte
    private byte padBits;
    
    boolean closed = false;

    /**
     * Creates a new BitStream of DEFAULT_SIZE
     */
    public BitStream() {
        bank = new byte[DEFAULT_SIZE];
    }

    /**
     * Creates a new BitStream of given size
     * @param size size of BitStream
     */
    public BitStream(int size) {
        bank = new byte[size];
    }

    /**
     * Creates a new BitStream from a byte array
     * assumes last byte is padBits
     * @param bank byte array of encoded bits
     */
    public BitStream(byte[] bank)
    {
        this.bank = bank;
        this.padBits = bank[bank.length - 1];
        pullByte();
    }

    /**
     * Get the byte array
     * @return byte array of BitStream
     */
    public byte[] getBank()
    {
        return bank;
    }

    /**
     * grow byte array by DEFAULT_SIZE
     * @return byte array grown by DEFAULT_SIZE
     */
    private byte[] grow()
    {
        return Arrays.copyOf(bank, bank.length + DEFAULT_SIZE);
    }

    /**
     * size of array
     * @return size of array
     */
    public int length()
    {
        return bank.length;
    }

    /**
     * returns EOB() or EndOfBank flag
     * @return true if end of bank
     */
    public boolean EOB()
    {
        return endOfBank;
    }

    /**
     * add a single bit to the BitStream
     * 
     * @param bit <code>true</code> for 1, <code>false</code> for 0
     */
    public void pushBit(boolean bit)
    {
        if (closed) return;
        bits[bitPosition] = bit;
        bitPosition++;
        if (bitPosition > 7) {
            flush();
        }
    }

    /**
     * flush the full byte to the BitStream
     */
    private void flush() {
        int makeByte = 0;
        for (int i = 0; i < 8; i++) {
            // push 1's to the correct bit position
            if (bits[i]) makeByte |= 1 << 7 - i;
        }
        bank[bytePosition] = (byte) (makeByte & 0xff);
        bytePosition++;
        if (bytePosition >= bank.length) bank = grow();
        bitPosition = 0;
        clearBits();        
    }

    /**
     * set all bits in the current bit[] array to 0
     */
    private void clearBits() {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = false;
        }
    }

    /**
     * Push bits to BitStream
     * length will be based on highest one-bit in integer
     * @param inBits integer value to push 
     */
    public void pushBits(int inBits) {
        int len = 31 - Integer.numberOfLeadingZeros(inBits);
        while(len >= 0) {
            pushBit((inBits & (1 << len)) == 1 << len);
            len--;
        }
    }

    /**
     * Push bits to BitStream
     * pushes a set length of bits, padding with zeroes if necessary
     * if value length is greater than length, will push entire value
     * @param value integer value to push
     * @param length number of bits to push
     */
    public void pushBits(int value, int length)
    {
        // TODO : Is there a way to do this with bit math
        String str = Integer.toBinaryString(value);
        while (str.length() < length)
        {
            str = "0" + str;
        }
        pushBits(str);
    }
    
    /**
     * Push bits to BitStream
     * pushes a String representation of a binary number
     * @param bitString 
     */
    public void pushBits(String bitString)
    {
        for (int i = 0; i < bitString.length(); i++)
        {
            pushBit(bitString.charAt(i) == '1');
        }
    } 

    /**
     * pull a byte of the current byte position 
     * in the array
     */
    private void pullByte()
    {
        clearBits();
        byte b = bank[bytePosition];
        // counter (bits are read in backwards)
        int up = 0;
        for (int i = 7; i >= 0; i--)
        {
            int f = 1 << i;
            if ((b & f) == f) bits[up] = true;
            up++;
        }
        bytePosition++;
        if (bytePosition > bank.length - 1) endOfBank = true;
        bitPosition = 0;
    }

    /**
     * Returns a single bit at bitPosition
     * @return bit boolean true for 1, false for 0
     */
    public boolean readBit()
    {
        boolean bit = bits[bitPosition];
        bitPosition++;
        if (bytePosition == bank.length - 1 && bitPosition > 7 - padBits) endOfBank = true;
        if (bitPosition > 7)
        {
            pullByte();
        }
        return bit;
    }
    

    /**
     * read <code>length</code> number of bits from the stream
     * @param length
     * @return integer
     */
    public int readBits(int length)
    {
        int retval = 0;
        for (int i = length - 1; i >= 0; i--)
        {
            retval |= ((readBit()) ? 1 : 0) << i;
        }
        return retval;
    }
    
    /**
     * close stream for writing operations.
     * will pull the first byte for reading operations.
     */
    public void close() {
        if (closed) return;
        closed = true;
        padBits = (byte)((-(bitPosition) + 8) % 8);
        if (padBits > 0)
        {
            flush();
        } 
        bank[bytePosition] = padBits;
        bank = Arrays.copyOfRange(bank, 0, bytePosition + 1); 
        bitPosition = 0;
        bytePosition = 0;
        pullByte();
    }
 

    @Override
    public String toString()
    {
        if (bitPosition != 0 ) flush();
        String retval = "";
        for (int i = 0; i < bank.length; i++)
        {
            if (i % 24 == 0) retval += "\n";
            retval += Utils.toHexString((int) (bank[i] & 0xff), 2) + " ";
        }
        return retval;
    }  
    
}
