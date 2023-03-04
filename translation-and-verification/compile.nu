
def compileshit [x: string] {
  flowc1 $x jar=out.jar
  java -jar out.jar
  rm out.jar
}
