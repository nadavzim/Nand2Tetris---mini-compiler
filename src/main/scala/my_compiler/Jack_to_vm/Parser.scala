package my_compiler.Jack_to_vm


import scala.io.Source
import java.io.{File, PrintWriter}
import java.nio.file.Paths
import scala.collection.mutable.ListBuffer

object Parser {

  var indexOfToken = 0
  var tokensList: List[String] = _
  var indexOfXml = 0
  var xmlList: List[String] = _

  var indentLevel = 0
  var xmlWriter: java.io.PrintWriter = _
  var parsing = new Parsing
  val help = new HelpFunctions

  var classTable = new SymbolTable
  var methodTable = new SymbolTable

  var className = ""
  var subType = ""
  var subName = ""

  // ***************** Ex_5 ******************** //

  class SymbolEntry /*(aName: String, symType: String, symSegment: String, num: Int)*/ {
    var name = ""
    var symbolType = ""
    var symbolSegment = ""
    var offset = 0

    def construct (aName: String, symType: String, segment: String, num: Int): Unit ={
      name = aName
      symbolType = symType
      symbolSegment = segment
      offset = num
    }

    def getName: String = {
      return name
    }

    def getType: String = {
      return symbolType
    }

    def getSegment: String = {
      return symbolSegment
    }

    def getOffset: Int = {
      return offset
    }

  }

  class SymbolTable {
    var table = new ListBuffer[SymbolEntry]

    def addRow(name: String, symType: String, segment: String): Unit = {
      val symbol = new SymbolEntry
      var aSegment = segment
      if (segment == "field")
        aSegment = "this"
      if(table.exists(entry => entry.getSegment == aSegment)){
        val index = table.lastIndexWhere(entry => entry.getSegment == aSegment)
        val offset = table.apply(index).getOffset + 1
        symbol.construct(name, symType, aSegment, offset)
        table.insert(index+1, symbol)
      } else {
        symbol.construct(name, symType, aSegment, 0)
        table.insert(0, symbol)
      }
    }

    def clearTable(): Unit = {
      table.clear()
    }

    def contains(name: String): Boolean = {
      return table.exists(entry => entry.getName == name)
    }

    def typeOf(name: String): String = {
      val index = table.indexWhere(entry => entry.getName == name)
      return table.apply(index).getType
    }

    def segmentOf(name: String): String = {
      val index = table.indexWhere(entry => entry.getName == name)
      return table.apply(index).getSegment
    }

    def indexOf(name: String): Int = {
      val index = table.indexWhere(entry => entry.getName == name)
      return table.apply(index).getOffset
    }

    def varCount(segment: String): Int = {
      if(table.exists(entry => entry.getSegment == segment)){
        val index = table.lastIndexWhere(entry => entry.getSegment == segment)
        val offset = table.apply(index).getOffset + 1
        return offset
      } else {
        return 0
      }
    }

    def printTable(): Unit ={
      val num = table.length
      var i = 0
      while(i < num) {
        println(table.apply(i).getName)
        println(table.apply(i).getSegment)
        println(table.apply(i).getType)
        println(table.apply(i).getOffset)
        i+=1
      }
    }
  }


  // ***************** Ex_4 ******************** //

  class HelpFunctions {


    def isIntegerConstant(x: String): Boolean = x forall Character.isDigit


    def getTagContent(token: String): String = {
      val matcher = """\<.*\>\s(.*?)\s<.*>""".r
      matcher findFirstIn token match {
        case Some(matcher(inside)) => inside
        case _ =>  ""
      }
    }

    // ***************** Help Functions - Parsing ******************** //

    def writeSubOpening(): Unit = {
      xmlWriter.write(s"function $className.$subName ${methodTable.varCount("local")}\n")
      subType match {
        case "constructor" =>
          xmlWriter.write(s"push constant ${classTable.varCount("this")}\n" +
            "call Memory.alloc 1\n" +
            "pop pointer 0\n")
        case "method" =>
          xmlWriter.write("push argument 0\n" +
            "pop pointer 0\n")
        case _ =>
      }
    }

    def writeTable(t: SymbolTable): Unit = {
      t.printTable()
    }

  }

  class Parsing {
    val subOpenings: Seq[String] = List("constructor", "function", "method")
    val statStarts: Seq[String] = List("do", "while", "let", "if", "return")
    val opList: Seq[String] = List("+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=")

    var someName  = ""
    var someType = ""
    var someSegment = ""

    var numLabel = 0
    var codeToWrite =""
    /**
     *
     * @param fileName is the file directory path
     */
    def parser(fileName: String): Unit = {

      val strFileName: String = fileName.replace(".jack", ".xml")
      val newFileName: String = strFileName.replace(".xml", ".vm")

      xmlWriter = new PrintWriter(new File(Paths.get(newFileName).toString))

      println("the new path is:\n" + strFileName)

      tokensList = Source.fromFile(strFileName).getLines().toList
      indexOfToken = 0

      while (indexOfToken < tokensList.length) {

        val tokenContent = help.getTagContent(tokensList(indexOfToken))
        if (tokenContent == "class")
          classParser()
        indexOfToken += 1
      }
      xmlWriter.close()
    }

    /**
     *
     */
    def classParser(): Unit = {
      classTable.clearTable()

      //<keyword> class </keyword>
      indexOfToken += 1
      //<identifier> Main </identifier>
      className = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1
      //<symbol> { </symbol>
      indexOfToken += 1


      classVarDeclaration()
      while (subOpenings.indexOf(help.getTagContent(tokensList(indexOfToken))) >= 0) {
        numLabel = 0
        subroutine()
      }

      //<symbol> } </symbol>
      indexOfToken += 1
      indexOfToken += 1

      help.writeTable(classTable)
    }

    /**
     *
     */
    def classVarDeclaration(): Unit = {
      while (help.getTagContent(tokensList(indexOfToken)) == "static"
        || help.getTagContent(tokensList(indexOfToken)) == "field") {

        //<keyword> field or static </keyword>
        someSegment = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1
        //<keyword> int </keyword>
        someType = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1
        //<identifier> x </identifier>
        someName = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1

        classTable.addRow(someName, someType, someSegment)

        while (help.getTagContent(tokensList(indexOfToken)) == ",") {
          // <symbol> , </symbol>
          indexOfToken += 1
          // <identifier> y </identifier>
          someName = help.getTagContent(tokensList(indexOfToken))
          indexOfToken += 1
          classTable.addRow(someName, someType, someSegment)
        }
        //<symbol> ; </symbol>
        indexOfToken += 1
      }

    }

    /**
     *
     */
    def subroutine(): Unit = {
      methodTable.clearTable()

      //<keyword> 'constructor', 'function', or 'method' </keyword>
      subType = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1
      //<keyword> void </keyword>
      indexOfToken += 1
      //<identifier> main </identifier>
      subName = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1
      //<symbol> ( </symbol>
      indexOfToken += 1

      // if the subroutine is a method - send a copy of the object to the method table
      if(subType == "method")
        methodTable.addRow("this", className, "argument")

      if (help.getTagContent(tokensList(indexOfToken)) != ")")
        subParameters()

      //<symbol>)</symbol>
      indexOfToken += 1

      //<symbol> { </symbol>
      indexOfToken += 1

      varDeclaration()

      // writing the beginning of the subroutine
      help.writeSubOpening()

      help.writeTable(methodTable)

      statements()

      //<symbol> } </symbol>
      indexOfToken += 1
    }

    /**
     *
     */
    def subParameters(): Unit = {
      someSegment = "argument"
      //<keyword> int </keyword>
      someType = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1
      //<identifier> x </identifier>
      someName = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1

      methodTable.addRow(someName, someType, someSegment)

      while (help.getTagContent(tokensList(indexOfToken)) == ",") {
        subroutineParameter()
      }
    }

    /**
     *
     */
    def subroutineParameter(): Unit = {
      //<symbol> , </symbol>
      indexOfToken += 1
      //<keyword> int </keyword>
      someType = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1
      //<identifier> x </identifier>
      someName = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1

      methodTable.addRow(someName, someType, someSegment)
    }

    /**
     *
     */
    def varDeclaration(): Unit = {
      while (help.getTagContent(tokensList(indexOfToken)) == "var") {
        //<keyword> var </keyword>
        someSegment = "local"
        indexOfToken += 1
        //<keyword> int </keyword>
        someType = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1
        //<identifier> x </identifier>
        someName = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1

        methodTable.addRow(someName, someType, someSegment)

        while (help.getTagContent(tokensList(indexOfToken)) == ",") {
          // <symbol> , </symbol>
          indexOfToken += 1
          // <identifier> y </identifier>
          someName = help.getTagContent(tokensList(indexOfToken))
          indexOfToken += 1

          methodTable.addRow(someName, someType, someSegment)
        }
        // <symbol> ; </symbol>
        indexOfToken += 1
      }
    }

    /**
     *
     */
    def statements(): Unit = {
      while (statStarts.indexOf(help.getTagContent(tokensList(indexOfToken))) >= 0) {
        codeToWrite = ""
        statement(numLabel)
      }
    }

    /**
     *
     */
    def statement(numLabel: Int): Unit = {

      help.getTagContent(tokensList(indexOfToken)) match {
        case "do" =>
          doStatement();
        case "while" =>
          whileStatement(numLabel);
        case "if" =>
          ifStatement(numLabel);
        case "return" =>
          returnStatement();
        case "let" =>
          letStatement();
      }
    }

    /**
     *
     */
    def letStatement(): Unit = {
      var varName = ""
      var varSegment = ""

      //<keyword> let </keyword>
      indexOfToken += 1

      var isClass :Boolean = false
      varName = help.getTagContent(tokensList(indexOfToken))
      if(classTable.contains(varName)) {
        isClass = true
        varSegment = classTable.segmentOf(varName)
      } else {
        varSegment = methodTable.segmentOf(varName)
      }

      //<keyword> game </keyword>
      indexOfToken += 1


      if (help.getTagContent(tokensList(indexOfToken)) == "[") {
        //<symbol> [ </symbol>
        indexOfToken += 1

        expression()
        if (isClass) {
          codeToWrite += s"push $varSegment ${classTable.indexOf(varName)}\n"
        }
        else {
          codeToWrite += s"push $varSegment ${methodTable.indexOf(varName)}\n"
        }
        codeToWrite += "add\n"

        //<symbol> ] </symbol>
        indexOfToken += 1

        //<symbol> = </symbol>
        indexOfToken += 1

        expression()
        codeToWrite += "pop temp 0\n" +
          "pop pointer 1\n" +
          "push temp 0\n" +
          "pop that 0\n"

        //<symbol> ; </symbol>
        indexOfToken += 1

      }
      else {
        //<symbol> = </symbol>
        indexOfToken += 1

        expression()

        //<symbol> ; </symbol>
        indexOfToken += 1

        if (isClass) {
          codeToWrite += s"pop $varSegment ${classTable.indexOf(varName)}\n"
        } else {
          codeToWrite += s"pop $varSegment ${methodTable.indexOf(varName)}\n"
        }
      }

      xmlWriter.write(codeToWrite)
      codeToWrite = ""
    }

    /**
     *
     */
    def returnStatement(): Unit = {
      //<keyword> return </keyword>
      indexOfToken += 1

      if (help.getTagContent(tokensList(indexOfToken)) != ";") {
        expression()
      } else {
        codeToWrite += "push constant 0\n"
      }

      codeToWrite += "return\n"
      //<symbol> ; </symbol>
      indexOfToken += 1

      xmlWriter.write(codeToWrite)
      codeToWrite = ""
    }

    /**
     *
     */
    def ifStatement(numIfLabel: Int): Unit = {
      //<keyword> if </keyword>
      indexOfToken += 1
      //<symbol> ( </symbol>
      indexOfToken += 1
      expression()

      codeToWrite += s"if-goto IF_TRUE$numIfLabel\n" +
        s"goto IF_FALSE$numIfLabel\n" +
        s"label IF_TRUE$numIfLabel\n"
      xmlWriter.write(codeToWrite)
      codeToWrite = ""

      //<symbol> ) </symbol>
      indexOfToken += 1
      //<symbol> { </symbol>
      indexOfToken += 1
      numLabel += 1
      statements()
      //<symbol> } </symbol>
      indexOfToken += 1

      if (help.getTagContent(tokensList(indexOfToken)) == "else") {
        codeToWrite += s"goto IF_END$numIfLabel\n" +
          s"label IF_FALSE$numIfLabel\n"

        xmlWriter.write(codeToWrite)
        codeToWrite = ""

        //<keyword> else </keyword>
        indexOfToken += 1
        //<symbol> { </symbol>
        indexOfToken += 1
        statements()
        //<symbol> } </symbol>
        indexOfToken += 1

        codeToWrite += s"label IF_END$numIfLabel\n"
      } else {
        codeToWrite += s"label IF_FALSE$numIfLabel\n"
      }

      xmlWriter.write(codeToWrite)
      codeToWrite = ""
    }

    /**
     *
     */
    def whileStatement(numWhileLabel: Int): Unit = {
      codeToWrite +=  s"label WHILE_EXP$numWhileLabel\n"

      //<keyword> while </keyword
      indexOfToken += 1
      //<symbol> ( </symbol>
      indexOfToken += 1
      expression()
      codeToWrite +=  "not\n"+
        s"if-goto WHILE_END$numWhileLabel\n"
      //<symbol> ) </symbol>
      indexOfToken += 1
      //<symbol> { </symbol>
      indexOfToken += 1

      xmlWriter.write(codeToWrite)
      codeToWrite = ""

      numLabel +=1
      statements()
      //<symbol> } </symbol>
      indexOfToken += 1
      codeToWrite += s"goto WHILE_EXP$numWhileLabel\n"+
        s"label WHILE_END$numWhileLabel\n"

      xmlWriter.write(codeToWrite)
      codeToWrite = ""
    }

    /**
     *
     */
    def doStatement(): Unit = {
      //<keyword> do </keyword>
      indexOfToken += 1

      subroutineCall()
      codeToWrite += "pop temp 0\n"

      //<symbol> ; </symbol>
      indexOfToken += 1

      xmlWriter.write(codeToWrite)
      codeToWrite = ""
    }

    /**
     *
     */
    def expression(): Unit = {
      term()
      while (opList.indexOf(help.getTagContent(tokensList(indexOfToken))) >= 0) {
        //<symbol> + </symbol>
        val op = help.getTagContent(tokensList(indexOfToken))
        indexOfToken += 1
        term()

        op match {
          case "+" =>
            codeToWrite += "add\n"
          case "-" =>
            codeToWrite += "sub\n"
          case "*" =>
            codeToWrite += "call Math.multiply 2\n"
          case "/" =>
            codeToWrite += "call Math.divide 2\n"
          case "&amp;" =>
            codeToWrite += "and\n"
          case "|" =>
            codeToWrite += "or\n"
          case "&lt;" =>
            codeToWrite += "lt\n"
          case "&gt;" =>
            codeToWrite += "gt\n"
          case "=" =>
            codeToWrite += "eq\n"
        }
      }
    }

    /**
     *
     */
    def subroutineCall(): Unit = {
      var numOfExp = 0
      var subCall = help.getTagContent(tokensList(indexOfToken))
      indexOfToken += 1

      if (help.getTagContent(tokensList(indexOfToken)) == "(") {
        codeToWrite += "push pointer 0\n"

        //<symbol> ( </symbol>
        indexOfToken += 1

        numOfExp = expressionList() + 1

        //<symbol> ) </symbol>
        indexOfToken += 1
        codeToWrite += s"call $className.$subCall $numOfExp\n"
      } else {
        //<symbol> . </symbol>
        indexOfToken += 1

        var subCallType = subCall
        subCall = help.getTagContent(tokensList(indexOfToken))

        //<identifier> SquareGame </identifier>
        indexOfToken += 1

        if(methodTable.contains(subCallType)){
          // push local 0
          codeToWrite += s"push ${methodTable.segmentOf(subCallType)} ${methodTable.indexOf(subCallType)}\n"
          numOfExp = 1
        } else if(classTable.contains(subCallType)/* || (methodTable.contains(subCallType))*/) {
          // push local 0
          codeToWrite += s"push ${classTable.segmentOf(subCallType)} ${classTable.indexOf(subCallType)}\n"
          numOfExp = 1
        }

        //<symbol> ( </symbol>
        indexOfToken += 1
        numOfExp += expressionList()

        if(classTable.contains(subCallType)) {
          subCallType = classTable.typeOf(subCallType)
        } else if (methodTable.contains(subCallType)) {
          subCallType = methodTable.typeOf(subCallType)
        }
        //<symbol> ) </symbol>
        indexOfToken += 1
        codeToWrite += s"call $subCallType.$subCall $numOfExp\n"
      }
    }

    /**
     * recursive
     */
    def term(): Unit = {
      var varName = ""
      var varSegment = ""
      var varOffset = 0

      val keywordConstantList = List("true", "false", "null", "this")

      // ( expression )
      if (help.getTagContent(tokensList(indexOfToken)) == "(") {
        //<symbol> ( </symbol>
        indexOfToken += 1
        expression()

        //<symbol> ) </symbol>
        indexOfToken += 1
      }

      // varName [ expression ]
      else if (help.getTagContent(tokensList(indexOfToken + 1)) == "[") {
        varName = help.getTagContent(tokensList(indexOfToken))
        if(classTable.contains(varName)) {
          varSegment = classTable.segmentOf(varName)
          varOffset = classTable.indexOf(varName)
        } else {
          varSegment = methodTable.segmentOf(varName)
          varOffset = methodTable.indexOf(varName)
        }

        //<symbol> varName </symbol>
        indexOfToken += 1
        //<symbol> [ </symbol>
        indexOfToken += 1
        expression()
        codeToWrite += s"push $varSegment $varOffset\n" +
          "add\n" +
          "pop pointer 1\n" +
          "push that 0\n"

        //<symbol> ] </symbol>
        indexOfToken += 1
      }

      // unaryOp term
      else if ((help.getTagContent(tokensList(indexOfToken)) == "-") || (help.getTagContent(tokensList(indexOfToken)) == "~")) {
        val op = help.getTagContent(tokensList(indexOfToken))
        //<symbol> unary op </symbol>
        indexOfToken += 1
        term()
        op match {
          case "-" =>
            codeToWrite += "neg\n"
          case "~" =>
            codeToWrite += "not\n"
        }
      }

      // subroutineCall
      else if ((help.getTagContent(tokensList(indexOfToken + 1)) == "(") || (help.getTagContent(tokensList(indexOfToken + 1)) == ".")) {
        subroutineCall()
      }

      // integer constant
      else if (help.isIntegerConstant(help.getTagContent(tokensList(indexOfToken)))) {
        varName = help.getTagContent(tokensList(indexOfToken))

        //<IntegerConstant> integerConstant </IntegerConstant>
        indexOfToken += 1

        codeToWrite += s"push constant $varName\n"
      }

      // string constant
      else if (tokensList(indexOfToken).startsWith("<stringConstant>")) {
        val varString = help.getTagContent(tokensList(indexOfToken)).map(_.toByte)
        val num = varString.length

        codeToWrite += s"push constant $num\n" +
          "call String.new 1\n"
        varString.foreach { x =>
          codeToWrite += s"push constant $x\n" +
            "call String.appendChar 2\n"
        }

        //<StringConstant> string </StringConstant>
        indexOfToken += 1
      }

      // keyword constant
      else if (keywordConstantList.indexOf(help.getTagContent(tokensList(indexOfToken))) >= 0) {
        varName = help.getTagContent(tokensList(indexOfToken))
        varName match {
          case "true" =>
            codeToWrite += "push constant 0 \n" +
              "not \n"
          case "false" =>
            codeToWrite += "push constant 0 \n"
          case "null" =>
            codeToWrite += "push constant 0 \n"
          case "this" =>
            codeToWrite += "push pointer 0 \n"
        }
        indexOfToken += 1
      }

      // identifier
      else {
        //<identifier>  </identifier>
        varName = help.getTagContent(tokensList(indexOfToken))
        if(classTable.contains(varName)) {
          varSegment = classTable.segmentOf(varName)
          varOffset = classTable.indexOf(varName)
        } else {
          varSegment = methodTable.segmentOf(varName)
          varOffset = methodTable.indexOf(varName)
        }

        indexOfToken += 1

        codeToWrite += s"push $varSegment $varOffset\n"
      }
    }

    /**
     *
     */
    def expressionList(): Int = {
      var numOfExp = 0
      if (help.getTagContent(tokensList(indexOfToken)) != ")") {
        numOfExp += 1
        expression()
        while (help.getTagContent(tokensList(indexOfToken)) == ",") {
          //<symbol> , </symbol>
          indexOfToken += 1
          numOfExp += 1
          expression()
        }
      }
      return numOfExp
    }
  }
}