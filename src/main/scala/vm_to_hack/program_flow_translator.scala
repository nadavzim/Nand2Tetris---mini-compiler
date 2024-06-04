package vm_to_hack

class program_flow_translator {
  def label_translate(cmd:Array[String], dir:String): String = {
    return "(" + dir + "." + cmd + ")\n"
  }
}
