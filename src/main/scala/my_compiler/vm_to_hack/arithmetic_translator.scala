package my_compiler.vm_to_hack

import my_compiler.vm_to_hack.vm_translator.DEC_SP


private var counter: Int = 0

class arithmetic_translator {
  def push_translate(cmd: Array[String], dir: String): String = {
    var asm = ""
    val number: String = cmd(2)
    val command = cmd(1)
    command match {
      case "constant" | "temp" | "argument" | "this" | "that" | "local" =>
        asm += s"@$number\n"
        asm += "D= A\n" //insert X into D
        command match {
          case "local" | "this" | "that" | "argument" =>
            //insert LCL/THIS/THAT/ARG into A
            command match {
              case "local" => asm += "@LCL\n"
              case "this" => asm += "@THIS\n"
              case "that" => asm += "@THAT\n"
              case "argument" => asm += "@ARG\n"
            }
            asm += "A=M+D\n" //insert RAM[LCL/THIS/THAT/ARG] + X into A
            asm += "D=M\n" //insert RAM[RAM[LCL/THIS/THAT/ARG] + X] into D
          case "temp" =>
            asm += "@5\n" //insert 5 into A
            asm += "A=D+A\n" //insert 5 + x into A
            asm += "D=M\n" //insert RAM[5 + x] into Dב
          case _ =>
        }
      case "pointer" =>
        number match {
          case "1" => asm += "@THAT\n"
          case "0" => asm += "@THIS\n"
        }
        asm += "D=M\n"
      case "static" =>
        asm += "@" + dir + "." + number + "\n"
        asm += "D=M\n"
    }

    asm += "@SP\n" //reset A to SP
    asm += "A=M\n" //get the top of the stack
    asm += "M=D\n" //insert D into the top of the the stack
    asm += "@SP\n"
    asm += "M=M+1\n"

    asm
  }

  def pop_translate(cmd: Array[String], dir: String): String = {
    var asm = ""
    asm += "@SP\n"
    asm += "A=M-1\n"
    asm += "D=M\n"
    val command = cmd(1)
    val number: String = cmd(2).replaceAll("\\s", "")
    command match {
      case "local" | "this" | "that" | "argument" =>
        command match {
          case "this" => asm += "@THIS\n"
          case "that" => asm += "@THAT\n"
          case "argument" => asm += "@ARG\n"
          case "local" => asm += "@LCL\n"
        }
        asm += "A=M\n"
        for (i <- 0 until number.toInt)
          asm += "A=A+1\n"
      case "temp" =>
        asm += s"@${(5 + number.toInt).toString}\n"
      case "pointer" =>
        if (number == "1")
          asm += "@THAT\n"
        if (number == "0")
          asm += "@THIS\n"
      case "static" =>
        asm += "@" + dir + "." + number + "\n" //
    }
    asm += "M=D\n"
    asm += DEC_SP
    asm
  }

  def binary_op_translate(cmd: Array[String], dir: String): String = {
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
    asm + DEC_SP + "\n"
  }

  private def compare_translate(cmd: Array[String], dir: String): String = {
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
    asm
  }

  def unary_op_translate(cmd: Array[String], dir: String): String = {
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
