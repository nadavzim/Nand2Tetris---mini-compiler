import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}


@main
def main(): Unit = {
  var res = ""
  val my_file = raw"file.vm"
  val lines = read_vm(my_file).split("\r\n")
  for (i <- lines) {
    res += vm_to_asm(i)
  }
  print(res)
  Files.write(Paths.get("res.asm"), res.getBytes(StandardCharsets.UTF_8)) // write the result to a file

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

/**
 * vm_to_asm - convert vm command to Hack asm command
 *
 * @param vm_cmd - the vm command
 * @return the Hack asm command
 */
def vm_to_asm(vm_cmd: String): String = {
  var asm = ""; var op = ""
  val words = vm_cmd.trim.split(" ")
  words(0) match {
    case "push" => // command: push x
      words(1) match {
        case "constant" =>
          op = "@" + words(2) + "\n"
            + "D=A\n" // D = x

        case "local" =>
          op = "@LCL\n"
            + "A=M\n"
            + add_index(words(2))
            + "D=M\n" // D = LCL + index

        case "argument" =>
          op = "@ARG\n"
            + "A=M\n"
            + add_index(words(2))
            + "D=M\n" // D = ARG + index

        case "this" =>
          op = "@THIS\n"
            + "A=M\n"
            + add_index(words(2))
            + "D=M\n" // D = THIS + index

        case "that" =>
          op = "@THAT\n"
            + "A=M\n"
            + add_index(words(2))
            + "D=M\n" // D = THAT + index

        case "temp" =>
          op = "@R5\n"
            + add_index(words(2))
            + "D=M\n" // D = R5 + index
        case "static" =>
          op = "@STATIC." + words(2) + "\n"
            + "D=M\n" // D = STATIC.i

        case "pointer" =>
          if words(2) == "0" then
            op = "@THIS\n"
          else
            op = "@THAT\n"
          op += "D=M\n" // D = THIS/THAT

      }
      return  op
        + "@SP\n"
        + "A=M\n"
        + "M=D\n" // *SP = D
        + INC_SP + "\n" // SP++

    case "pop" =>
    // TODO: complete ynon
    case "add" | "sub" | "eq" | "lt" | "gt" | "or" | "and" =>
      words(0) match {
        case "add" =>
          op = "+"
        case "sub" =>
          op = "-"
        case "eq" =>
          op = " eq "
        case "lt" =>
          op = " lt "
        case "gt" =>
          op = " gt "
        case "and" =>
          op = "&"
        case "or" =>
          op = "|"
      }
      asm = DEC_SP
        + "@SP\n"
        + "A=M\n"
        + "D=M\n"
        + DEC_SP
        + "@SP\n"
        + "A=M\n"
        + "M=M" + op + "D\n"
        + INC_SP + "\n"
    // TODO: complete ynon
    case "not" =>
    // TODO: complete ynon
    case "neg" =>
    // TODO: complete ynon
  }
  asm
}
/**
 * add_index - add index times "A=A+1" to the asm code
 *
 * @param str - the index
 * @return the asm code
 */
def add_index(str: String): Unit = {
  var i = str.toInt
  var res = ""
  while i > 0 do {
    res += "A=A+1\n"
    i -= 1
    }
}
/**
 * DEC_SP - decrease the SP by 1
 * INC_SP - increase the SP by 1
 */
val INC_SP: String = "@SP\n" // SP++
  + "M=M+1\n\n"

val DEC_SP: String = "@SP\n" // SP--
  + "M=M-1\n\n"