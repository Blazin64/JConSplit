# JConSplit

After a bit of a haitus due to school and just generally being busy, I'm back! ~Argonitious

General description:

This Java project splits files based on ocurrences of a pattern specified by the user.

Originally, the method of splitting was to find all ocurrences of a pattern and output split files at the same time. After some experimenting, however, it turned out that it was better and **faster** to handle this process with two passes. The first pass simply finds ocurrences of a given pattern, while the second pass splits the input into individual files.

To do:
1. Add detailed documentation.
2. Clean up the code!
~~3. Add support for different lengths of hex patterns. (Only 16 byte patterns currently supported due to a bug that I am investigating.)~~ Done!
~~4. Add support for splitting based on lengths instead of patterns as well. (Just trying to be thorough here!)~~
~~5. Add support for concatenation.~~ Probably not happening.
