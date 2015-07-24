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

import lznp.util.BitStream;

/**
 * Tree class for Huffman Encoding
 * @author /u/Philboyd_Studge
 */
public class Tree implements Comparable<Tree>
{
    Node root;

    /**
     * blank constructor
     */
    public Tree()
    {
        root = null;
    }

    /**
     * Leaf Node
     * @param index byte value
     * @param count frequency
     */
    public Tree(int index, int count)
    {
        root = new Node(index, count);
    }

    /**
     * new subtree
     * @param left left subtree
     * @param right right subtree
     */
    public Tree(Tree left, Tree right)
    {
        root = new Node();
        root.left = left.root;
        root.right = right.root;
        root.count = left.root.count + right.root.count;
    }
    
    @Override
    public int compareTo(Tree t)
    {
        return root.count - t.root.count;
    }
    
    /**
     * Creates BitStream and sends root node of tree to helper function
     * that will encode the tree into a BitStream for usage in a file header.
     * @return BitStream
     */
    public BitStream encodedTree()
    {
        BitStream bs = new BitStream();
        encodedTree(root, bs);
        bs.close();
        System.out.println("");
        return bs;
    }
    
    /**
     * Recursive helper method to load tree into BitStream
     * where 1 indicates a value literal byte to follow
     * and 0 indicates a non-leaf node
     * @param node starts with root
     * @param bs BitStream to write to
     */
    public void encodedTree(Node node, BitStream bs)
    {
        if (isLeaf(node))
        {
            System.out.print("1:" + node.index + ":");
            bs.pushBit(true);
            bs.pushBits(node.index, 8);
        }
        else
        {
            System.out.print("0:");
            bs.pushBit(false);
            encodedTree(node.left, bs);
            encodedTree(node.right, bs);
        }
    }

    /**
     * Get single code from stream
     * @param bs
     * @return integer encoded value
     */
    public int getCode(BitStream bs)
    {
        Node current = root;
        boolean bit;
        while (!isLeaf(current))
        {
           bit = bs.readBit();
           if (bit) current = current.right;
           else current = current.left;
           
        }
        return current.index;
    }

    /**
     * Pre-order traversal
     */
    public void printTree()
    {
        printTree(root);
        System.out.println();
    }
    
    private void printTree(Node node)
    {
        if (node == null) return;
        printTree(node.left);
        if (isLeaf(node)) System.out.println(node);
        printTree(node.right);
    }

    /**
     * Returns true if node has no children thus is a leaf-node
     * @param node Node to test
     * @return true if is a leaf
     */
    public boolean isLeaf(Node node)
    {
        return (node.left == null && node.right == null);
    }
}
