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
    for (file <- vmFiles) {
      if (vmFiles.length > 1) {
        res += bootstrap
      }
      // Perform some action on each .vm file
      val lines = Source.fromFile(file).mkString.split("\r\n") // read the file
      Source.fromFile(file).close()
      for (i <- lines) { 
        res += "// " + i + "\n" + vm_to_asm(i, directory.getName) // convert the vm command to Hack asm command
      }
    }
    print(res)
    Files.write(Paths.get(path + "\\" + directory.getName + ".asm"), res.getBytes(StandardCharsets.UTF_8)) // write the asm code to a file
  }
  else {
    println(s"The path $path is not a valid directory.")
  }
}

