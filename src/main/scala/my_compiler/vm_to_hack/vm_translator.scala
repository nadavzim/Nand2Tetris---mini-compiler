package my_compiler.vm_to_hack

private var funcCount: Int = 0

object vm_translator {
  val bootstrap: String = "//bootstrap\n"
    + "@256\n" // SP = 256
    + "D=A\n"
    + "@SP\n"
    + "M=D\n"

  val sys_init: String = "@Sys.init\n" + "0;JMP\n"
  /**
   * DEC_SP - decrease the SP by 1
   * INC_SP - increase the SP by 1
   */
  val INC_SP: String = "@SP\n" // SP++
    + "M=M+1\n"
  val DEC_SP: String = "@SP\n" // SP--
    + "M=M-1\n"

  /**
   * vm_to_asm - convert vm command to Hack asm command
   * @param vm_cmd - the vm command
   * @return the Hack asm command
   */
  def vm_to_asm(vm_cmd: String, dir: String = ""): String = {
    var asm = ""
      val words = vm_cmd.trim.split(" ")
      words(0) match {
        case "label" =>
          asm = program_flow_translator().label_translate(words, dir)
        case "goto" =>
          asm = program_flow_translator().goto_translate(words, dir)
        case "if-goto" =>
          asm = program_flow_translator().if_goto_translate(words, dir) //
        case "call" =>
          funcCount += 1
          asm = program_flow_translator().call_translate(words, dir, funcCount)
        case "function" =>
          funcCount += 1
          asm = program_flow_translator().function_translate(words, dir, funcCount)
        case "return" =>
          asm = program_flow_translator().return_translate(words, dir)
        case "push" => // command: push x
          asm += arithmetic_translator().push_translate(words, dir)
        case "pop" =>
          asm += arithmetic_translator().pop_translate(words, dir)
        case "add" | "sub" | "eq" | "lt" | "gt" | "or" | "and" =>
          asm += arithmetic_translator().binary_op_translate(words, dir)
        case "not" | "neg" =>
          asm += arithmetic_translator().unary_op_translate(words, dir)
        case _ =>
      }
    asm
  }
  
}
