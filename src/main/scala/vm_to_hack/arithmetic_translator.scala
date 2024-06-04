package vm_to_hack

private var counter: Int = 0 
class arithmetic_translator {
  def push_translate(cmd:Array[String], dir:String): String = {
    var op = ""
    cmd(1) match {
      case "constant" =>
        op = "@" + cmd(2) + "\n"
          + "D=A\n" // D = x

      case "local" =>
        op = "@LCL\n"
          + "A=M\n"
          + vm_translator().add_index(cmd(2))
          + "D=M\n" // D = LCL + index

      case "argument" =>
        op = "@ARG\n"
          + "A=M\n"
          + vm_translator().add_index(cmd(2))
          + "D=M\n" // D = ARG + index

      case "this" =>
        op = "@THIS\n"
          + "A=M\n"
          + vm_translator().add_index(cmd(2))
          + "D=M\n" // D = THIS + index

      case "that" =>
        op = "@THAT\n"
          + "A=M\n"
          + vm_translator().add_index(cmd(2))
          + "D=M\n" // D = THAT + index

      case "temp" =>
        op = "@R5\n"
          + vm_translator().add_index(cmd(2))
          + "D=M\n" // D = R5 + index
      case "static" =>
        op = "@STATIC." + cmd(2) + "\n"
          + "D=M\n" // D = STATIC.i

      case "pointer" => {
        if cmd(2) == "0" then
          op = "@THIS\n"
        else {
          op = "@THAT\n"
          op += "D=M\n" // D = THIS/THAT
        }
      }
    }
    return op
      + "@SP\n"
      + "A=M\n"
      + "M=D\n" // *SP = D
      + vm_translator().INC_SP + "\n" // SP++

  }
  def pop_translate(cmd:Array[String], dir:String): String = {
    var asm = ""
    asm = "@SP\n"
      + "A=M-1\n"
      + "D=M\n"
    cmd(1) match {
      case "local" | "this" | "that" | "argument" =>
        cmd(1) match {
          case "local" =>
            asm += "@LCL\n"
          case "argument" =>
            asm += "@ARG\n"
          case "this" =>
            asm += "@THIS\n"
          case "that" =>
            asm += "@THAT\n"
        }
        asm += "A=M\n"
          + vm_translator().add_index(cmd(2))

      case "temp" =>
        asm += "@R5\n"
          + vm_translator().add_index(cmd(2))

      case "static" =>
        asm += "@STATIC." + cmd(2) + "\n"
          + "D=M\n"

      case "pointer" => {
        if cmd(2) == "0" then
          asm += "@THIS\n"
        else {
          asm += "@THAT\n"
        }
      }
    }
    asm += "M=D\n" // *addr = D
      + vm_translator().DEC_SP
    asm
  }
  def binary_op_translate(cmd:Array[String], dir:String): String = {
    var asm = ""
    asm += "@SP\n"
      + "A=M -1\n"
      + "D=M\n"
      + "A=A-1\n"

    cmd(0) match {
      case "add" =>
        asm += "M=M+D\n\n"
      case "sub" =>
        asm += "M=M-D\n\n"

      case "eq" | "gt" | "lt" => //==, >, <
        asm = asm + compare_translate(cmd, dir)

        asm += "D=0\n" //D = 0
          + "@SP\n"
          + "A=M-1\n"
          + "A=A-1\n"
          + "M=D\n"
          + "@IF_FALSE" + counter.toString + "\n"
          + "0;JMP\n"
          + "(IF_TRUE" + counter.toString + ")\n"
          + "D=-1\n" //D = -1
          + "@SP\n"
          + "A=M-1\n"
          + "A=A-1\n"
          + "M=D\n"
          + "(IF_FALSE" + counter.toString + ")\n"

      case "and" =>
        asm += "M=D&M\n"
      case "or" =>
        asm += "M=D|M\n"
    }
    asm + vm_translator().DEC_SP + "\n"
  }

  private def compare_translate(cmd:Array[String], dir:String): String = {
    var asm = ""
    counter = counter + 1
    asm += asm + "D=D-M\n"
      + "@IF_TRUE" + counter.toString + "\n"

    cmd(0) match {
      case "eq" =>
        asm += "D;JEQ\n"

      case "gt" =>
        asm += "D;JLT\n"
      case "lt" =>
        asm += "D;JGT\n"

    }

    asm + "D=0\n" //D = 0
      + "@SP\n"
      + "A=M-1\n"
      + "A=A-1\n"
      + "M=D\n"
      + "@IF_FALSE" + counter.toString + "\n"
      + "0;JMP\n"
      + "(IF_TRUE" + counter.toString + ")\n"
      + "D=-1\n" //D = -1
      + "@SP\n"
      + "A=M-1\n"
      + "A=A-1\n"
      + "M=D\n"
      + "(IF_FALSE" + counter.toString + ")\n"
  }
  def unary_op_translate(cmd:Array[String], dir:String): String = {
    var asm = ""
    asm = "@SP\n"
      + "A=M-1\n"
    if cmd(0) == "not" then
      asm += "M=!M\n\n"
    else
      asm += "M=-M\n\n"
    asm
  }
}
