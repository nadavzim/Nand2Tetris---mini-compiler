package object vm_to_hack {
  private var count: Int = 0

  /**
   * add_index - add index times "A=A+1" to the asm code
   *
   * @param str - the index
   * @return the asm code
   */
  private def add_index(str: String): Unit = {
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
  private val INC_SP: String = "@SP\n" // SP++
    + "M=M+1\n\n"
  private val DEC_SP: String = "@SP\n" // SP--
    + "M=M-1\n\n"

  /**
   * vm_to_asm - convert vm command to Hack asm command
   *
   * @param vm_cmd - the vm command
   * @return the Hack asm command
   */
  def vm_to_asm(vm_cmd: String): String = {
    var asm = ""
    var op = ""
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

          case "pointer" => {
            if words(2) == "0" then
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
          + INC_SP + "\n" // SP++
      case "pop" =>
      // TODO: complete ynon
      case "add" | "sub" | "eq" | "lt" | "gt" | "or" | "and" =>
        asm += "@SP\n"
          + "A = M -1\n"
          + "D = M\n"
          + "A = A-1\n"

        words(0) match {
          case "add" =>
            asm += "M = D + M\n"
          case "sub" =>
            asm += "M = D - M\n"

          case "eq" | "gt" | "lt" => //==, >, <
            count = count + 1
            asm += asm + "D = D - M\n"
              + "@IF_TRUE" + count.toString + "\n"

            words(0) match {
              case "eq" =>
                asm += "D;JEQ\n"

              case "gt" =>
                asm += "D;JLT\n"
              case "lt" =>
                asm += "D;JGT\n"

            }

            asm += "D = 0\n" //D = 0
              + "@SP\n"
              + "A = M - 1\n"
              + "A = A - 1\n"
              + "M = D\n"
              + "@IF_FALSE" + count.toString + "\n"
              + "0;JMP\n"
              + "(IF_TRUE" + count.toString + ")\n"
              + "D = -1\n" //D = -1
              + "@SP\n"
              + "A = M - 1\n"
              + "A = A - 1\n"
              + "M = D\n"
              + "(IF_FALSE" + count.toString + ")\n"

          case "and" =>
            asm += "M = D & M\n"
          case "or" =>
            asm += "M = D | M\n"
        }
        asm += DEC_SP

      // TODO: complete ynon
      case "not" =>
      // TODO: complete ynon
      case "neg" =>
      // TODO: complete ynon
      case default =>
    }
    asm
  }
}
