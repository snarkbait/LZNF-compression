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
package lznp.huffman;

import lznp.util.BinaryHeap;
import lznp.util.BitStream;

/**
 * Huffman Tree class 
 * Holds a 256-byte single byte tree
 * @author /u/Philboyd_Studge
 */
public class HuffmanTree
{
    Tree htree;

    /**
     * Creates a Huffman Tree with the given array
     * 
     * @param frequencies Array of integers size 256
     */
    public HuffmanTree(int[] frequencies)
    {
        this.htree = getHuffmanTree(frequencies);
    }
    
    /**
     * Creates a Huffman Tree from encoded BitStream
     * @param bs 
     */
    public HuffmanTree(BitStream bs)
    {
       this.htree = getHuffmanTree(bs);
    }

    /**
     * Generate Huffman Tree from array of frequencies
     * @param frequencies
     * @return Tree
     */
    private Tree getHuffmanTree(int[] frequencies)
    {
        BinaryHeap<Tree> heap = new BinaryHeap<>();
        for (int i = 0; i < frequencies.length; i++)
        {
            if (frequencies[i] > 0)
            {
                heap.add(new Tree(i, frequencies[i]));
            }
        }
        
        while (heap.length() > 1)
        {
            Tree t1 = heap.remove();
            Tree t2 = heap.remove();
            heap.add(new Tree(t1, t2));
        }
        
        return heap.remove();
    }

    /**
     * Generate Huffman tree from BitStream
     * Re-creates tree, ignores frequency count
     * @param bs BitStream of tree structure
     * @return Tree
     */
    private Tree getHuffmanTree(BitStream bs)
    {  

        Tree tree;
        if (bs.readBit())
        {
            int t = bs.readBits(8);
            tree = new Tree(t, 0);
            return tree;
        }
        else
        {
            Tree left = getHuffmanTree(bs);
            Tree right = getHuffmanTree(bs);
            tree = new Tree(left, right);
            return tree;
        }
    }
    
    /**
     * get BitStream of current Tree
     * @return BitStream tree structure
     */
    public BitStream getBitTree()
    {
        return htree.encodedTree();
    }    

    /**
     * Returns a String array of the bit codes for the 
     * given frequency array
     * @return Array of bit codes corresponding to the array index
     */
    public String[] getCodes()
    {
        if (htree.root == null) return null;
        String[] codes = new String[256];
        assignCodes(htree.root, codes);
        return codes;
    }
    
    private void assignCodes(Node root, String[] codes)
    {
        // TODO : use integers and bit math for speed
        if (root.left != null)
        {
            root.left.code = root.code + "0";
            assignCodes(root.left, codes);
            
            root.right.code = root.code + "1";
            assignCodes(root.right, codes);
        }
        else
        {
            codes[root.index] = root.code;
        }
    }

    /**
     * decodes individual value from BitStream of Huffman Codes
     * Wrapper for method in Tree
     * @param bs BitStream of Huffman codes
     * @return integer encoded value
     */
    public int getCode(BitStream bs)
    {
        return htree.getCode(bs);
    }
    
}
