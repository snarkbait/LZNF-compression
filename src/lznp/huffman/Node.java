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

/**
 * Node class for Huffman Encoding
 * @author /u/Philboyd_Studge
 */
public class Node {
    int index;
    int count;
    String code = "";
    int hcode;
    int hlen;
    Node left;
    Node right;
    
    public Node()
    {
        // blank constructor
    }

    /**
     * creates new node
     * @param index byte index
     * @param count frequency
     */
    public Node(int index, int count)
    {
        this.index = index;
        this.count = count;
    }

    
    @Override
    public String toString()
    {
        return index + " : " + count;
    }
}
