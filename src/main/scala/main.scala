import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets


@main
def main(): Unit = {
  var res = ""
  val my_file = raw"file.vm"
  val lines = read_vm(my_file).split("\r\n")
  for ( i <- lines ) {
    res += vm_to_asm(i)
  }
  print(res)
  Files.write(Paths.get("res.asm"), res.getBytes(StandardCharsets.UTF_8))

}

def read_vm(str: String): String = {
  val source = scala.io.Source.fromFile(str)
  val lines = try source.mkString finally source.close()
  lines
}

def vm_to_asm(vm_cmd: String): String = {
  // TODO: check - do we need to implement push and pop? do we need to implement both push const and push static?
  var asm = ""
  val words = vm_cmd.trim.split(" ")
  words(0) match{
    case "push" => // command: push x
      asm = "@" + String(words(1)) + "\n"
        + "D=A\n" // D = x
        + "@SP\n"
        + "A=M\n" // A = *SP
        + "M=D\n"
        + INC_SP + "\n" // SP = SP+1
    case "pop" =>
    // TODO: complete ynon
    case "add" |  "sub" | "eq" | "lt" | "gt" |"or" | "and" =>
      var op = ""
      words(0) match{
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
        +  "M=M" + op + "D\n"
        + INC_SP +"\n"
    // TODO: complete ynon
    case "not" =>
    // TODO: complete ynon
    case "neg" =>
    // TODO: complete ynon
  }
  asm
}

val INC_SP: String = "@SP\n" // SP++
          + "M=M+1\n\n"

val DEC_SP: String = "@SP\n" // SP--
  + "M=M-1\n\n"