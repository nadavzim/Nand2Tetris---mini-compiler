package vm_to_hack
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

@main
def main(): Unit = {
  var (res, input_file, output_file) = ("", "", "")
  Console.println("\nPlease insert path for the input vm file and the output asm file seperated by Enter\ndefault values: 'file.vm', 'res.asm' for default values press 1: ")
  input_file = scala.io.StdIn.readLine()
  output_file = scala.io.StdIn.readLine()
  if input_file == "1" then
    input_file = "file.vm" // default value
    if output_file == "1" then
    output_file = "res.asm" // default value


  val lines = read_vm(input_file).split("\r\n")
  for (i <- lines) {
    res += vm_to_hack.vm_to_asm(i)
  }
  print(res)
  Files.write(Paths.get("\"C:\\Users\\nadav\\Desktop\\שנה ג סמסטר ב\\עקרונות שפות תוכנה\\תרגילי בית\\nand2tetris\\nand2tetris\\projects\\7\\StackArithmetic\\StackTest\\StackTest.vm\"res.asm"), res.getBytes(StandardCharsets.UTF_8)) // write the result to a file
}

/**
 * read_vm - read the vm file
 *
 * @param str - the file name
 * @return the file content
 */
def read_vm(str: String): String = {
  val source = scala.io.Source.fromFile(str)
  val lines = try source.mkString finally source.close()
  lines
}
