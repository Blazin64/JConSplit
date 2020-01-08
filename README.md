# JConSplit
This interactive CLI Java utility splits concatenated data based on occurrences of a hexadecimal pattern specified by the user. JConSplit uses riversun's bigdoc library, found at https://github.com/riversun/bigdoc.

### Compiling
Maven was used primarily for handling dependencies. If you use the Eclipse IDE, you can simply import this as a Maven project. Eclipse should take care of the rest for you. Use your IDE's compile button to create a usable jar file.

### Usage
JConSplit is interactive, so run your compiled jar file with `java -jar jconsplit.jar` and the prompts that appear will guide you through the necessary steps. It will ask you for an input file name, a base name for output files, an extension to use for output files, and the magic bytes that mark the beginning of each data stream. The output file names will use your base name with a number and your chosen file extension appended. The output file names will look something like `base123.abc`.

### Future improvements
* Better error handling. (Some rudimentary error handling exists, but it could be made more friendly.)
