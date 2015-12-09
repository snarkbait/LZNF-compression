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
package lznp.util;

/**
 *
 * @author tim
 */
public class MoveToFront
{
        /**
         * encode byte array using MTF
         * @param bank
         * @return outBank byte array of MTF-encoded data
         */
	public static byte[] encode(byte[] bank)
	{
		byte[] outBank = new byte[bank.length];
		int[] dict = new int[256];
		int[] tempDict = new int[256];
		for (int i=0; i<256;i++)
		{
			dict[i] = i;
		}

		for (int i=0; i < bank.length;i++)
		{
			for (int j = 0; j < 256; j++)
			{
				if (dict[j]==bank[i])
				{
					outBank[i] = (byte)j;
					if (j > 0)
					{
						tempDict[0] = dict[j];
						System.arraycopy(dict, 0, tempDict, 1, j);
						System.arraycopy(dict, j + 1, tempDict, j + 1, 255 - j);
						System.arraycopy(tempDict, 0, dict, 0, 256);
					}
					break;
				}
			}
		}
		return outBank;
	}
        
        /**
         * decode MTF-encoded bank
         * @param bank
         * @return outBank byte array of decoded data
         */
	public static byte[] decode(byte[] bank)
	{
		byte[] outBank = new byte[bank.length];
		int[] dict = new int[256];
		int[] tempDict = new int[256];
		for (int i=0; i<256;i++)
		{
			dict[i] = i;
		}

		for (int i=0; i < bank.length;i++)
		{
			outBank[i] = (byte)dict[bank[i]];
			if (bank[i] > 0)
			{
				tempDict[0] = dict[bank[i]];
				System.arraycopy(dict, 0, tempDict, 1, bank[i]);
				System.arraycopy(dict, bank[i] + 1, tempDict, bank[i] + 1, 255 - bank[i]);
				System.arraycopy(tempDict, 0, dict, 0, 256);
			}
		}
		return outBank;
	}
        
        public static BitStream bitEncode(byte[] input)
        {
            BitStream bs = new BitStream();
            for (int i = 0; i < input.length; i++)
            {
                if (input[i] == 0)
                {
                    bs.pushBit(false);
                    bs.pushBit(false);
                }
                else
                {
                    if (input[i] < 9)
                    {
                        bs.pushBit(false);
                        bs.pushBit(true);
                        bs.pushBits(input[i] - 1, 3);
                    }
                    else
                    {
                        if (input[i] < 41)
                        {
                            bs.pushBit(true);
                            bs.pushBit(false);
                            bs.pushBits(input[i] - 9, 5);
                        }
                        else
                        {
                            bs.pushBit(true);
                            bs.pushBit(true);
                            bs.pushBits(input[i], 8);
                        }
                    }
                }
            }
            bs.close();
            return bs;
        }   
}
