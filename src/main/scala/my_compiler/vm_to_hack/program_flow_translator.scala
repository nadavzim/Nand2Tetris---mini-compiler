package my_compiler.vm_to_hack

class program_flow_translator {
  def label_translate(cmd: Array[String], dir: String): String = {
    return "(" + dir + "." + cmd(1) + ")\n"
  }

  def goto_translate(cmd: Array[String], dir: String): String = {
    return "@" + dir + "." + cmd(1) + "\n" +
      "0;JMP\n"
  }

  def if_goto_translate(cmd: Array[String], dir: String): String = {
    return "@SP\n" +
      "M=M-1\n" +
      "A=M\n" +
      "D=M\n" +
      "@" + dir + "." + cmd(1) + "\n" +
      "D;JNE\n"
  }

  def function_translate(cmd: Array[String], dir: String, funcCount: Int): String = {
    return "(" + cmd(1) + ")\n"
      + "@" + cmd(2) + "\n"
      + "D=A\n"
      + "@" + cmd(1) + ".End_" + funcCount + "\n"
      + "D; JEQ\n" // if n = 0, jump to the end
      + "(" + cmd(1) + s".Loop_" + funcCount + ")\n"
      + "@SP\n" + "A=M\n" + "M=0\n" + "@SP\n" + "M=M+1\n"
      + "@" + cmd(1) + s".Loop_" + funcCount + "\n"
      + "D=D-1;JNE\n" // if n != 0, jump to the loop
      + "(" + cmd(1) + s".End_" + funcCount + ")\n" // end of the function
  }

  def call_translate(cmd: Array[String], dir: String, funcCount: Int): String = {
    val funcName = cmd(1)
    val args = cmd(2).toInt

    return "@" + funcName + ".returnAddress_" + funcCount + "\n" + // load return address
      "D=A\n" + "@SP\n" + "M=M+1\n" + "A=M-1\n" + "M=D\n" + "@LCL\n" + // save local pointer
      "D=M\n" + "@SP\n" + "M=M+1\n" + "A=M-1\n" + "M=D\n" + "@ARG\n" + // save argument pointer
      "D=M\n" + "@SP\n" + "M=M+1\n" + "A=M-1\n" + "M=D\n" + "@THIS\n" + // save this pointer
      "D=M\n" + "@SP\n" + "M=M+1\n" + "A=M-1\n" + "M=D\n" + "@THAT\n" + // save that pointer
      "D=M\n" + "@SP\n" + "M=M+1\n" + "A=M-1\n" + "M=D\n" + "@" + args + "\n" + // load function's argument pointer
      "D=A\n" + "@5\n" + "D=A+D\n" + "@SP\n" + "D=M-D\n" + "@ARG\n" + "M=D\n" + "@SP\n" + // load function's local pointer
      "D=M\n" + "@LCL\n" + "M=D\n" + "@" + funcName + "\n" + // jump to function
      "0;JMP\n" + "(" + funcName + ".returnAddress_" + funcCount + ")\n" // return address label
  }

  def return_translate(cmd: Array[String], dir: String): String = {
    return "@LCL\n" + "D=M\n" // frame = LCL
      + "@5\n" + "A=D-A\n" + "D=M\n" + "@R13\n" + "M=D\n" // R13 = *(LCL-5)
      + "@SP\n" + "M=M-1\n" + "A=M\n" + "D=M\n" + "@ARG\n" + "A=M\n" + "M=D\n" // *ARG = pop()
      + "@ARG\n" + "D=M\n" + "@SP\n" + "M=D+1\n" // SP = ARG + 1
      + "@LCL\n" + "M=M-1\n" + "A=M\n" + "D=M\n" + "@THAT\n" + "M=D\n" // THAT = *(LCL-1)
      + "@LCL\n" + "M=M-1\n" + "A=M\n" + "D=M\n" + "@THIS\n" + "M=D\n" // THIS = *(LCL-2)
      + "@LCL\n" + "M=M-1\n" + "A=M\n" + "D=M\n" + "@ARG\n" + "M=D\n" // ARG = *(LCL-3)
      + "@LCL\n" + "M=M-1\n" + "A=M\n" + "D=M\n" + "@LCL\n" + "M=D\n" // LCL = *(LCL-4)
      + "@13\n" + "A=M\n" + "0;JMP\n" // goto RET
  }
}
