package my_compiler.Jack_to_vm

/**
 * Tokenizing - a class that will tokenize the Jack code
 */
object Tokenizing {
  private val jackKeywordList = List(
    "class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void",
    "true", "false", "null", "this", "let", "do", "if", "else", "while", "return")
  private val jackSymbolList = List('{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=',
    '~')
  private val jackKeywordConstList = List("true", "false", "null", "this")
  private var token = ""

  /**
   * tokenizing - tokenize the Jack code
   * @param code - the Jack code in one file string
   * @return the tokenized code in xml format
   */
  def tokenizing(code: String): String = {
    var res = "<tokens>\n"
    var i = 0
    while (i < code.length) {
//      println(code.substring(i, code.indexOf('\n', i)))
      code(i) match { // check the char
        case '/' =>
          val temp = i
          i = comment_tokenize(code, i)
          if (temp == i ) // make token only for '/' not for comment
            res += s"<symbol> ${code(i)} </symbol>\n"
        case char if char.isWhitespace =>  // ignore spaces, new lines and tabs
        case '"' => // string constant
          i = string_tokenize(code, i)
          res += "<stringConstant> " + token + " </stringConstant>\n"
        case char if char.isDigit => // integer constant
          i = int_tokenize(code, i)
          res += "<integerConstant> " + token + " </integerConstant>\n"
        case '_' => case char if char.isLetter=> // keyword or identifier
          i = identifier_tokenize(code, i)
          if (isKeyword(token))
            res += "<keyword> " + token + " </keyword>\n"
          else
            res += "<identifier> " + token + " </identifier>\n"
        case char if jackSymbolList.contains(char) => // symbol
          var sym = char.toString
          if (char == '<')
            sym = "&lt;"
          else if (char == '>')
            sym = "&gt;"
          else if (char == '&')
            sym = "&amp;"
          else if (char == '"')
            sym = "&quot;"
          res += s"<symbol> $sym </symbol>\n"
        case _ =>
          }
      token = ""
      i += 1
      }
    res += "</tokens>" // close the tokens xml 
    res
      }

  private def comment_tokenize(code: String, i: Int): Int= { // comment
    var j = i + 1
    if (code(j) == '/') // single line comment
      j = endIndex(code, i, '\n')
    else if (code(j) == '*') // multi line comment
      j += 1
      while (code(j) != '*' | code(j + 1) != '/')
        j += 1
      j+= 1
      else
        j = i
    j
  }

  /**
   * const string_tokenize - tokenize the const strings
   * @param code - the code
   * @param i - start index
   * @return the index of the end of the string, the string stored in the token variable
   */
  private def string_tokenize(code: String, i: Int): Int = { // string constant
    var temp = i
    val end = endIndex(code, i, '"')
    token = code.substring(i + 1, end)
    end
    }


  /**
   * int_tokenize - tokenize the integers
   * @param code - the code
   * @param i - start index
   * @return the index of the end of the int, the int stored in the token variable
   */
  private def int_tokenize(code: String, i: Int): Int = { // integer constant
    var j = i
    while (code(j).isDigit)
      j += 1
    token = code.substring(i, j)
    j - 1
  }

  /**
   * identifier_tokenize - tokenize the identifier
   * @param code - the code
   * @param i - start index
   * @return the index of the end of the id, the id stored in the token variable
   */
  private def identifier_tokenize(code: String, i: Int): Int = { // identifier
    var j = i
    while (code(j).isLetterOrDigit || code(j) == '_')
      j += 1
    token = code.substring(i, j)
    j - 1
  }

  private def isKeyword(token: String): Boolean = {
    jackKeywordList.contains(token)
  }

  def isSymbol(token: String): Boolean = {
    jackSymbolList.contains(token)
  }

  /**
   * endIndex - loop until the end char
   * @param code - the code
   * @param i - the index
   * @param end - the end char
   * @return the index of the end char
   */
  private def endIndex(code: String, i: Int, end: Char): Int = {
    var j = i + 1
    while (code(j) != end)
      j += 1
    j
  }
}