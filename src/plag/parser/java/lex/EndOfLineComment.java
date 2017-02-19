package plag.parser.java.lex;

class EndOfLineComment extends Comment {
  EndOfLineComment(String comment) { appendLine(comment); }
}
