/*************************************************************************************************************
*  nadav zimmerman and ynon hayun translator from vm to asm                                                  *
*  exe1 (stage 07 of nand2tetris demo compiler project)                                                      *
*************************************************************************************************************/
package vm_to_hack
import vm_to_hack.vm_translator.{bootstrap, vm_to_asm}

import java.io.File
import java.nio.charset.StandardCharsets
import scala.io.Source
import java.nio.file.{Files, Paths}

 /**
  * main - the main function that reads the vm files and converts them to single asm file
  *
  * @param path - the path of the directory
  */
@main
def main(path: String): Unit = {
  println("path: " + path + "\n")
  var res = "// nadav and ynon translator from vm to asm\n\n" // the asm code.
  val directory = new File(path)

  if (directory.exists && directory.isDirectory) { // check if the path is a valid directory
    var vmFiles = directory.listFiles.filter(file => file.isFile && file.getName.endsWith(".vm")) // get all .vm files
    val (sysFiles, otherFiles) = vmFiles.partition(_.getName == "Sys.vm")
    vmFiles = sysFiles ++ otherFiles // put Sys.vm first in the list

    if (vmFiles.length > 1) {
      res += bootstrap // add the bootstrap code
      res += "\n" + vm_to_asm("call Sys.init 0", directory.getName) // call Sys.init

    }
    if(vmFiles.length == 1){
      val lines = readLines(vmFiles(0))
      for (i <- lines) {
        res += "// " + i + "\n" + vm_to_asm(i, directory.getName) // convert the vm command to Hack asm command
      }
    }
    else{
      for (file <- vmFiles) {
        // Perform some action on each .vm file
        val lines = readLines(file)
        for (i <- lines) {
          res += "// " + i + "\n" + vm_to_asm(i, directory.getName) // convert the vm command to Hack asm command
    }
      }
    }
    print(res)
    Files.write(Paths.get(path + "\\" + directory.getName + ".asm"), res.getBytes(StandardCharsets.UTF_8)) // write the asm code to a file
  }
  else {
    println(s"The path $path is not a valid directory.")
  }
}

/**
 * readLines - read the lines of a file
 * @param file
 * @return the content of the file split to lines
 */
 def readLines(file: File): List[String] = {
   val source = Source.fromFile(file)
   val lines = source.getLines().toList
   source.close()
   lines
 }

