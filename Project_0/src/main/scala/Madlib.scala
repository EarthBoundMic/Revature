import jodd.util.ThreadUtil.sleep

import scala.io.StdIn.readLine

// main will have the primary loop for user input...will need a lot of readlines
// will need checks for user input for proper menu commands...will be best to use numbers
// each command will have its own function, and parsing functions may be its own class
object Madlib {
  var userCheck:Int = 0

  def main(args:Array[String]):Unit = {
    var decision:Int = 0

    println("\nWelcome To Crazy Michael's Madlibs!!\n\nSelect Option:\n1: Start\n2: Exit\n")
    while (decision != 2) {
      val input: String = readLine()
      if (input.forall(_.isDigit)) {
        decision = input.toInt
        if (decision == 1)
          selectUser()
        else if (decision == 2)
          println("\nThanks for playing!\n\n")
        else
          println("\u001b[2JIncorrect input, try again!\nNeeds to be either a 1 or 2.\n")
      }
      else
        println("\u001b[2JIncorrect input, try again!\nNeeds to be either a 1 or 2.\n")
    }
  }

  def selectUser():Unit = {
    var decision:Int = 0

    println("Now, you can pick either to begin anonymous\nor to select a name that previously went nutz.\nRemember, anonymous means your shenanigans won't be saved.\n\n0: Go Back\n1: Anonymous\n2: Select User\n")
    while (decision != 0) {
      val input: String = readLine()
      if (input.forall(_.isDigit)) {
        decision = input.toInt
        val check = userMatch(decision)
        if (check > 0)
          mdlbSelect()
        else if (check < 0)
          println("C'mon, you know what inputs are needed.\nEither a 0, 1, or 2.\n")
      }
    }
  }
  def userMatch(x:Int):Int = x match {
    case 0 => 0
    case 1 => 1   //anonymous
    case 2 => 1   //invoke userSelection
    case _ => -1  //bad input
  }
}
