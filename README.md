# LZNF-compression
Compression Library

An example of a modification of the LZP algorithm by CBloom, using no bitflags for literal/matches on the LZP pass.

Uses Huffman for the entropy encoding, and passes the encoded tree data to the output file as well.

Writes a file header with the original filename, size and tree data.
