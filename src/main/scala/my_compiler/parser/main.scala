// This is the main file that will be executed when the program is run.
// It will take the path of the directory as an argument and convert the Jack files to vm files.
package my_compiler.parser

/**
 * main - the main file that will be executed when the program is run.
 * It will take the path of the directory as an argument and convert the Jack files to vm files:
 * first it will translate jack to xml, then it will build it hierarchy, then translate it to vm and then to asm
 */
object main {
  def main(args: Array[String]): Unit = {
    val path = args(0)
    val fileParser = new fileParser

    fileParser.setFileSrc("jack").setFileDst("xml") // stage 4.1 + 4.2 jack to xml
    fileParser.fileTranslate(path) // parse and translate jack to xml
//    fileParser.setFileSrc("xml").setFileDst("vm")  // stage 5 - xml to vm
//    fileParser.fileTranslate(path) // parse and translate xml to vm
    fileParser.setFileSrc("vm").setFileDst("asm")  // stage 1,2 - vm to hack
    fileParser.fileTranslate(path); // parse and translate vm to Hack
  }
}