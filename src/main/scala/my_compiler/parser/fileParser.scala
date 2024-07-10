package my_compiler.parser


import my_compiler.Jack_to_vm.Parser.parsing
import my_compiler.vm_to_hack.vm_translator.{bootstrap, vm_to_asm}
import my_compiler.Jack_to_vm.Tokenizing.tokenizing

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.io.Source

var curFile: String = ""
/**
 * fileParser - parse the files in the directory and translate them to the other fileSource and write the result to a file
 */
class fileParser {
  private var fileSrc = "jack"
  private var fileDst = "xml"
  /**
  * fileTranslate - translate the files in the directory to the other fileSource
  * @param path - the path of the directory
  */
  def fileTranslate(path: String): Unit = {
    var res = ""
    val directory = new File(path)
    if (directory.exists && directory.isDirectory) { // check if the path is a valid directory
      val dirFiles = directory.listFiles.filter(file => file.isFile && file.getName.endsWith(fileSrc)) // get all .fileSource files
      if ((fileSrc == "vm") && (dirFiles.length > 1)) { // if there are more than 1 .vm files
        res += bootstrap // add the bootstrap code
        res += "\n" + vm_to_asm("call Sys.init 0", directory.getName) // add call Sys.init
      }

        if(fileDst == "asm")
          dirFiles.foreach({
          file => res += iterateFile(file, fileSrc)
            println(res)
            writeToFile(path, res)

          })
        else if (fileDst == "xml")
          dirFiles.foreach({
            file => res = iterateFile(file, fileSrc)
              curFile = file.getName.split('.')(0)
              println(res)
              writeToFile(path, res)
      })
        val xmlFiles = directory.listFiles.filter(file => file.isFile && file.getName.endsWith(".xml")) // get all .xml files
        xmlFiles.foreach { file => parsing.parser(file.toString) }
    }
    else {
      sys.error(s"The path $path is not a valid directory.")
    }
//    if (fileSrc == "vm") {
//      Files.write(Paths.get(path + "\\" + directory.getName + ".asm"), res.getBytes(StandardCharsets.UTF_8)) // write the asm code to a file
//    }
//    else{ // if the fileSource is jack - write the vm code to a file
//      Files.write(Paths.get(path + "\\" + directory.getName + ".vm"), res.getBytes(StandardCharsets.UTF_8)) // write the asm code to a file
//    }

  }

  /**
  * iterateFile - iterate over the file and perform some action on each line
  * @param file - the file
  * @param srcFile - the fileSource of the file
  * @return the translated code
   */
  private def iterateFile(file: File, srcFile: String): String = {
    var res = ""
    var line = ""
    val lines = readLines(file)
    lines.foreach { line =>
      if (srcFile == "vm") {
        res += "// " + line + "\n" + vm_to_asm(line, file.getName) // convert the vm command to Hack asm command
      }
    }
      if (srcFile == "jack")
        res += tokenizing(lines.reduce((x,y) => x+ '\n' + y)) + "\n" // convert the jack content to vm code
//      else if (srcFile == "xml")
//        parsing.parser(file.toString)
    res
  }

  /**
   * readLines - read the lines of a file
   * @param file - the file
   * @return the content of the file split to lines
   */
  private def readLines(file: File): List[String] = {
    val source = Source.fromFile(file)
    val lines = source.getLines().toList
    source.close()
    lines.map(_.trim).map(_.strip()).filter(_.nonEmpty)
  }

  /**
   * writeToFile - write the content to a file
   * @param path - the path of the directory
   * @param content - the content to write
   */
  private def writeToFile(path: String, content: String): Unit = {
    val directory = new File(path)
    if (fileDst == "xml")
      Files.write(Paths.get(path + "\\" + curFile + s".$fileDst"), content.getBytes(StandardCharsets.UTF_8))
    else
      Files.write(Paths.get(path + "\\" + directory.getName + s".$fileDst"), content.getBytes(StandardCharsets.UTF_8))
  }

  def setFileSrc(fileSource: String): fileParser = {
    this.fileSrc = fileSource
    this
  }

  def setFileDst(fileDst: String): fileParser = {
    this.fileDst = fileDst
    this
  }
}