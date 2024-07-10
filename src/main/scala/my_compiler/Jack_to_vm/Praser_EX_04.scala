//package my_compiler.Jack_to_vm
//
//import scala.io.Source
//import java.io.{File, PrintWriter}
//import java.nio.file.{Files, Paths}
//
//object Parser {
//
//  var indexOfToken = 0
//  var tokensList: List[String] = _
//
//  var indentLevel = 0
//  var xmlWriter: java.io.PrintWriter = _
//  var parsing = new Parsing
//
//  class Parsing {
//
//    val subOpenings = List("constructor", "function", "method")
//    val statStarts = List("do", "while", "let", "if", "return")
//    val opList = List("+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=")
//
//
//    def getTagContent(token: String): String = {
//      val matcher = """\<.*\>\s(.*?)\s\<.*\>""".r
//      matcher findFirstIn token match {
//        case Some(matcher(inside)) => inside
//        case _ => ""
//      }
//    }
//
//    /**
//     * Indents the contents of the XML file
//     *
//     * @param str
//     */
//    def writeFormatted(str: String): Unit = {
//      xmlWriter.write("  " * indentLevel + str + "\n")
//    }
//
//
//    /**
//     *
//     * @param fileName is the file directory path
//     */
//    def parser(fileName: String): Unit = {
//      val strFileName: String = fileName.replace(".jack", ".xml")
//      val newFileName: String = strFileName.replace(".xml", "PPP.xml")
//
//      xmlWriter = new PrintWriter(new File(Paths.get(newFileName).toString))
//
//      val tokenPath: String = strFileName.replace(".xml", "T.xml")
//      println("the new path is:\n" + tokenPath)
//
//      tokensList = Source.fromFile(tokenPath).getLines().toList
//      indexOfToken = 0
//
//      while (indexOfToken < tokensList.length) {
//
//        val tokenContent = getTagContent(tokensList(indexOfToken))
//        if (tokenContent == "class")
//          classParser()
//        indexOfToken += 1
//      }
//      xmlWriter.close()
//    }
//
//    /**
//     *
//     */
//    def classParser(): Unit = {
//      writeFormatted("<class>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> class </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<identifier> Main </identifier>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol> { </symbol>
//      indexOfToken += 1
//      classVarDeclaration()
//      while (/*tokensList(indexOfToken) != null  &&*/ subOpenings.indexOf(getTagContent(tokensList(indexOfToken))) >= 0)
//        subroutine()
//
//      writeFormatted(tokensList(indexOfToken)) //<symbol> } </symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</class>")
//      indexOfToken += 1
//
//    }
//
//    /**
//     *
//     */
//    def classVarDeclaration(): Unit = {
//      while (getTagContent(tokensList(indexOfToken)) == "static"
//        || getTagContent(tokensList(indexOfToken)) == "field") {
//        writeFormatted("<classVarDec>")
//        indentLevel += 1
//        writeFormatted(tokensList(indexOfToken)) //<keyword> field or static </keyword>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<keyword> int </keyword>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<identifier> x </identifier>
//        indexOfToken += 1
//
//        while (getTagContent(tokensList(indexOfToken)) == ",") {
//          writeFormatted(tokensList(indexOfToken)) // <symbol> , </symbol>
//          indexOfToken += 1
//          writeFormatted(tokensList(indexOfToken)) // <identifier> y </identifier>
//          indexOfToken += 1
//        }
//        writeFormatted(tokensList(indexOfToken)) // <symbol> ; </symbol>
//        indexOfToken += 1
//        indentLevel -= 1
//        writeFormatted("</classVarDec>")
//      }
//    }
//
//    /**
//     *
//     */
//    def subroutine(): Unit = {
//
//      writeFormatted("<subroutineDec>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> 'constructor', 'function', or 'method' </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword>void</keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<identifier>main</identifier>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol>(</symbol>
//      indexOfToken += 1
//      writeFormatted("<parameterList>")
//
//      if (getTagContent(tokensList(indexOfToken)) != ")")
//        subParameters()
//
//      writeFormatted("</parameterList>")
//
//      writeFormatted(tokensList(indexOfToken)) //<symbol>)</symbol>
//      indexOfToken += 1
//
//      writeFormatted("<subroutineBody>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol>{</symbol>
//      indexOfToken += 1
//
//      varDeclaration()
//      statements()
//
//      writeFormatted(tokensList(indexOfToken)) //<symbol>}</symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</subroutineBody>")
//      indentLevel -= 1
//      writeFormatted("</subroutineDec>")
//
//    }
//
//    /**
//     *
//     */
//    def subParameters(): Unit = {
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> int </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<identifier> x </identifier>
//      indexOfToken += 1
//      while (getTagContent(tokensList(indexOfToken)) == ",") {
//        subroutineParameter()
//      }
//      indentLevel -= 1
//    }
//
//    /**
//     *
//     */
//    def subroutineParameter(): Unit = {
//      writeFormatted(tokensList(indexOfToken)) //<symbol> , </symbol>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> int </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<identifier> x </identifier>
//      indexOfToken += 1
//
//    }
//
//    /**
//     *
//     */
//    def varDeclaration(): Unit = {
//      while (getTagContent(tokensList(indexOfToken)) == "var") {
//        writeFormatted("<varDec>")
//        indentLevel += 1
//        writeFormatted(tokensList(indexOfToken)) //<keyword> var </keyword>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<keyword> int </keyword>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<identifier> x </identifier>
//        indexOfToken += 1
//
//        while (getTagContent(tokensList(indexOfToken)) == ",") {
//          writeFormatted(tokensList(indexOfToken)) // <symbol> , </symbol>
//          indexOfToken += 1
//          writeFormatted(tokensList(indexOfToken)) // <identifier> y </identifier>
//          indexOfToken += 1
//        }
//        writeFormatted(tokensList(indexOfToken)) // <symbol> ; </symbol>
//        indexOfToken += 1
//        indentLevel -= 1
//        writeFormatted("</varDec>")
//      }
//    }
//
//    /**
//     *
//     */
//    def statements(): Unit = {
//      writeFormatted("<statements>")
//      indentLevel += 1
//
//      while (statStarts.indexOf(getTagContent(tokensList(indexOfToken))) >= 0)
//        statement()
//
//      indentLevel -= 1
//      writeFormatted("</statements>")
//    }
//
//    /**
//     *
//     */
//    def statement(): Unit = {
//
//      getTagContent(tokensList(indexOfToken)) match {
//        case "do" =>
//          doStatement();
//        case "while" =>
//          whileStatement();
//        case "if" =>
//          ifStatement();
//        case "return" =>
//          returnStatement();
//        case "let" =>
//          letStatement();
//      }
//    }
//
//    /**
//     *
//     */
//    def letStatement(): Unit = {
//      writeFormatted("<letStatement>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> let </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> game </keyword>
//      indexOfToken += 1
//      if (getTagContent(tokensList(indexOfToken)) == "[") {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> [ </symbol>
//        indexOfToken += 1
//        expression()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ] </symbol>
//        indexOfToken += 1
//      }
//      writeFormatted(tokensList(indexOfToken)) //<symbol> = </symbol>
//      indexOfToken += 1
//      expression()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ; </symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</letStatement>")
//
//    }
//
//    /**
//     *
//     */
//    def returnStatement(): Unit = {
//      writeFormatted("<returnStatement>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> return </keyword>
//      indexOfToken += 1
//      if (getTagContent(tokensList(indexOfToken)) != ";")
//        expression()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ; </symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</returnStatement>")
//    }
//
//    /**
//     *
//     */
//    def ifStatement(): Unit = {
//
//      writeFormatted("<ifStatement>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> if </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ( </symbol>
//      indexOfToken += 1
//      expression()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ) </symbol>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol> { </symbol>
//      indexOfToken += 1
//      statements()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> } </symbol>
//      indexOfToken += 1
//      if (getTagContent(tokensList(indexOfToken)) == "else") {
//        writeFormatted(tokensList(indexOfToken)) //<keyword> else </keyword>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<symbol> { </symbol>
//        indexOfToken += 1
//        statements()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> } </symbol>
//        indexOfToken += 1
//      }
//      indentLevel -= 1
//      writeFormatted("</ifStatement>")
//    }
//
//    /**
//     *
//     */
//    def whileStatement(): Unit = {
//      writeFormatted("<whileStatement>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> while </keyword>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ( </symbol>
//      indexOfToken += 1
//      expression()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ) </symbol>
//      indexOfToken += 1
//      writeFormatted(tokensList(indexOfToken)) //<symbol> { </symbol>
//      indexOfToken += 1
//      statements()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> } </symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</whileStatement>")
//    }
//
//    /**
//     *
//     */
//    def doStatement(): Unit = {
//      writeFormatted("<doStatement>")
//      indentLevel += 1
//      writeFormatted(tokensList(indexOfToken)) //<keyword> do </keyword>
//      indexOfToken += 1
//      subroutineCall()
//      writeFormatted(tokensList(indexOfToken)) //<symbol> ; </symbol>
//      indexOfToken += 1
//      indentLevel -= 1
//      writeFormatted("</doStatement>")
//    }
//
//    /**
//     *
//     */
//    def expression(): Unit = {
//      writeFormatted("<expression>")
//      indentLevel += 1
//      term()
//      while (opList.indexOf(getTagContent(tokensList(indexOfToken))) >= 0) {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> + </symbol>
//        indexOfToken += 1
//        term()
//      }
//      indentLevel -= 1
//      writeFormatted("</expression>")
//    }
//
//    /**
//     *
//     */
//    def subroutineCall(): Unit = {
//      writeFormatted(tokensList(indexOfToken)) //<identifier>SquareGame</identifier>
//      indexOfToken += 1
//      if (getTagContent(tokensList(indexOfToken)) == "(") {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ( </symbol>
//        indexOfToken += 1
//        expressionList()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ) </symbol>
//        indexOfToken += 1
//      }
//      else {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> . </symbol>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<identifier>SquareGame</identifier>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ( </symbol>
//        indexOfToken += 1
//        expressionList()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ) </symbol>
//        indexOfToken += 1
//      }
//    }
//
//    /**
//     * recursive
//     */
//    def term(): Unit = {
//
//      writeFormatted("<term>")
//      indentLevel += 1
//      if (getTagContent(tokensList(indexOfToken)) == "(") {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ( </symbol>
//        indexOfToken += 1
//        expression()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ) </symbol>
//        indexOfToken += 1
//      }
//      else if (getTagContent(tokensList(indexOfToken + 1)) == "[") {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> varName </symbol>
//        indexOfToken += 1
//        writeFormatted(tokensList(indexOfToken)) //<symbol> [ </symbol>
//        indexOfToken += 1
//        expression()
//        writeFormatted(tokensList(indexOfToken)) //<symbol> ] </symbol>
//        indexOfToken += 1
//      }
//      else if ((getTagContent(tokensList(indexOfToken)) == "-") || (getTagContent(tokensList(indexOfToken)) == "~")) {
//        writeFormatted(tokensList(indexOfToken)) //<symbol> unary op </symbol>
//        indexOfToken += 1
//        term()
//      }
//      else if ((getTagContent(tokensList(indexOfToken + 1)) == "(") || (getTagContent(tokensList(indexOfToken + 1)) == ".")) {
//        subroutineCall()
//      }
//
//      else {
//        writeFormatted(tokensList(indexOfToken)) //<indentifier>  </indentifier>
//        indexOfToken += 1
//      }
//      indentLevel -= 1
//      writeFormatted("</term>")
//
//    }
//
//    /**
//     *
//     */
//    def expressionList(): Unit = {
//      writeFormatted("<expressionList>")
//      indentLevel += 1
//      if (getTagContent(tokensList(indexOfToken)) != ")") {
//        expression()
//        while (getTagContent(tokensList(indexOfToken)) == ",") {
//          writeFormatted(tokensList(indexOfToken)) //<symbol> , </symbol>
//          indexOfToken += 1
//          expression()
//        }
//      }
//      indentLevel -= 1
//      writeFormatted("</expressionList>")
//    }
//  }
//}