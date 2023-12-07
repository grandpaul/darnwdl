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
 - CRC32 of uncompressed Data: 4 bytes
 - data: compressedSize bytes

## PASS2

After decompress all the data in WPass1 and written to a file.
Now we are at Pass2. Pass2 contains the following structures.

### R2

This structure the page separator

 - tag: "R2", 2 bytes
 - unknown short: 2 bytes

### TC

This structure defines the foreground color

 - tag: "TC", 2 bytes
 - r: 0~255, 1 byte
 - g: 0~255, 1 byte
 - b: 0~255, 1 byte
 - unknown byte: 1 byte

### BC

This structure defines the background color

 - tag: "BC", 2 bytes
 - r: 0~255, 1 byte
 - g: 0~255, 1 byte
 - b: 0~255, 1 byte
 - unknown byte: 1 byte

### BH

This structure defines the foreground color. For filling an area or polygon.

 - tag: "BH", 2 bytes
 - file pointer: points to a Special02 structure.

### FT

This structure defines the Font for ET/EU.

 - tag: "FT", 2 bytes
 - file pointer: points to a Special01 structure.

### UF

This structure defines the Font for UT

 - tag: "UF", 2 bytes
 - unknown short: 2 bytes
 - file pointer: points to a Special01 structure.

### PN

This structure defines the Stroke.

 - tag: "PN", 2 bytes
 - file pointer: points to a Special03 structure.

### ET/EU

This structure describes text encoded by local encoding.

 - tag: "ET" or "EU", 2 bytes
 - seekLen: the length of following data. The following structure will be multiple.
   - x: 2 bytes
   - y: 2 bytes
   - string_length: 2 bytes
   - flag1: flag. 1 byte
   - string: The string. string_length bytes. It is encoded by either Big5 or GB2312.
     - x1: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - y1: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - x2: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - y2: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - width_array: string_length shorts (2 bytes). Only appeared if flag1 & 0x02 != 0
   
### UT

This structure describes text encoded by UTF-16LE.

 - tag: "UT", 2 bytes
 - seekLen: the length of following data. The following structure will be multiple.
   - x: 2 bytes
   - y: 2 bytes
   - string_length: 2 bytes
   - flag1: flag. 1 byte
   - string: The string. string_length shorts (2 bytes).
     - x1: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - y1: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - x2: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - y2: 2 bytes. Only appeared if flag1 & 0x01 != 0
     - width_array: string_length shorts (2 bytes). Only appeared if flag1 & 0x02 != 0

### CR

This structure describes the clip region

 - tag: "CR", 2 bytes
 - x1: 2 bytes
 - y1: 2 bytes
 - x2: 2 bytes
 - y2: 2 bytes

### FR

This structure describe a filled rectangle

 - tag: "FR", 2 bytes
 - seekLen: the length of following data. The following structure will be multiple.
   - x1: 2 bytes
   - y1: 2 bytes
   - x2: 2 bytes
   - y2: 2 bytes

### PL

This structure describe a Path to draw

 - tag: "PL", 2 bytes
 - seekLen: the length of following data. The following structure will be multiple.
   - N: the number of points, 2 bytes. The following data will repeat N times.
     - x: 2 bytes
     - y: 2 bytes

### AP

This structure describe a Polygon to draw

 - tag: "AP", 2 bytes
 - seekLen: the length of following data. The following structure will be multiple.
   - N: the number of points, 2 bytes. The following data will repeat N times.
     - x: 2 bytes
     - y: 2 bytes

### SD

This structure describe a uncompressed picture.

 - tag: "SD", 2 bytes
 - seekLen: the length of following data.
 - target_x: 2 bytes
 - target_y: 2 bytes
 - target_width: 2 bytes
 - target_height: 2 bytes
 - unknown_bytes_1: 4 bytes
 - width_short: the source image's width, 2 bytes
 - height_short: the source image's height, 2 bytes
 - unknown_bytes_2: 8 bytes
 - width: the source image's width, 4 bytes
 - height: the source image's height, 4 bytes
 - unknown_short_1: 2 bytes
 - color_depth: 2 bytes
 - unknown_short_2: 2 bytes
 - compressionMethod: 2 bytes
 - graphDataLen: the length of graphData. 4 bytes
 - unknown_int_1: 4 bytes
 - unknown_int_2: 4 bytes
 - unknown_int_3: 4 bytes
 - unknown_int_4: 4 bytes
 - palette_data: 1024 bytes. Only appeared if unknwon_int_3 != 0.
 - graphDataLen2: the length of graphData. 4 bytes
 - graphData: graphDataLen2 bytes

### SP

This structure describe a compressed picture.

 - tag: "SP", 2 bytes
 - unknown_short_0: 2 bytes
 - seekLen: the length of following data.
 - target_x: 2 bytes
 - target_y: 2 bytes
 - target_width: 2 bytes
 - target_height: 2 bytes
 - unknown_bytes_1: 4 bytes
 - width_short: the source image's width, 2 bytes
 - height_short: the source image's height, 2 bytes
 - unknown_bytes_2: 10 bytes
 - width: the source image's width, 4 bytes
 - height: the source image's height, 4 bytes
 - unknown_short_1: 2 bytes
 - color_depth: 2 bytes
 - unknown_short_2: 2 bytes
 - compressionMethod: 2 bytes
 - graphDataLen: the length of graphData. 4 bytes
 - unknown_int_1: 4 bytes
 - unknown_int_2: 4 bytes
 - unknown_int_3: 4 bytes
 - unknown_int_4: 4 bytes
 - paletteData: only appeared if compressMethod == 2, 6, or 7.
                if color_depth == 1, it has 4*2 bytes.
		if color_depth == 8, it has 4*256 bytes.
 - graphDataLen2: the length of graphData. 4 bytes.
 - graphData: graphDataLen2 bytes.

### BM

Unknown function.

 - tag: "BM", 2 bytes
 - unknown_short: 2 bytes

### CT

Unknown function.

 - tag: "CT", 2 bytes
 - unknown_short: 2 bytes

### AQ

Unknown function.

 - tag: "AQ", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes

### RT

Unknown function.

 - tag: "RT", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes

### WP

Unknown function.

 - tag: "WP", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes

### XD

Unknown function.

 - tag: "XD", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes

### SX

Unknown function.

 - tag: "SX", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes

### EP

Unknown function.

 - tag: "EP", 2 bytes
 - seekLen: 2 bytes
 - unknownData: seekLen bytes
