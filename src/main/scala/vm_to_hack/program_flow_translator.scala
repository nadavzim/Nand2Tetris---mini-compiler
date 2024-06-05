package vm_to_hack

class program_flow_translator {
  def label_translate(cmd:Array[String], dir:String): String = {
    return "(" + dir + "." + cmd(0) + ")\n"
  }
  def function_translate(cmd:Array[String], dir:String): String = {
    return "(" + cmd(1) + ")\n"
      + "@" + cmd(2) + "\n"
      + "D=A\n"
      + "@" + cmd(1) + ".End\n"
      + "D; JEQ\n" // if n = 0, jump to the end
      + "(" + cmd(1) + ".Loop)\n"
      + "@SP\n" + "A=M\n" + "M=0\n" + "@SP\n" + "M=M+1\n"
      + "@" + cmd(1) + ".Loop\n"
      + "D=D-1;JNE\n" // if n != 0, jump to the loop
      + "(" + cmd(1) + ".End)\n" // end of the function
  }
  def return_translate(cmd:Array[String], dir:String): String = {
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
