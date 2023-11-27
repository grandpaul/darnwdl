# WDL format

This document describes the format of WDL.

## PASS1

WDL file is compressed. We need to decompress it first.

### Header

The file contains a magic number "DDocFB"
And the following two bytes are the version of the format.

And then it follows properties.
Each property has 4 bytes indicate the property name. For example, "font",
"indx".
And each property contains 3 32-bit integer as data.

And then with 50 bytes unknown data.
And read the integer forwardLen which is a 32-bit integer.
Then skip forwardLen - 38 bytes.

### DynaPKCP structure.

 - "DynaPKCP": 8 bytes
 - unknown integer: 4 bytes
 - uncompressedSize: 4 bytes
 - compressedSize: 4 bytes
 - unknown integer: 4 bytes
 - data: compressedSize bytes

## PASS2

