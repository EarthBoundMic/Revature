import jodd.util.ThreadUtil.sleep
import mlData._
import scala.io.StdIn.readLine

object Madlib {
  private var _userCheck:Int = 0
  private var _username:String = "anonymous"
  private var _madlibCheck:Int = 0
  private val _madlibCount:Int = madlibCount()

  def main(args:Array[String]):Unit = {
    var decision:Int = 0

    while (decision != 2) {
      println("\nWelcome To Crazy Michael's Madlibs!!\n\nSelect Option:\n1: Start\n2: Exit\n")
      val input: String = readLine()
      if (input.forall(_.isDigit)) {
        decision = input.toInt
        if (decision == 1)
          selectUser()
        else if (decision == 2)
          println("\nThanks for playing!\n\n")
        else
          println("Incorrect input, try again!\nNeeds to be either a 1 or 2.\n")
      }
      else
        println("Incorrect input, try again!\nNeeds to be either a 1 or 2.\n")
    }
  }

  def selectUser():Unit = {
    var decision:Int = -1

    println("Now, you can pick either to begin anonymous\nor to select a name that previously went nutz.\nRemember, anonymous means your shenanigans won't be saved.\n\n0: Go Back\n1: Anonymous\n2: Select User\n")
    while (decision != 0) {
      val input: String = readLine()
      if (input.forall(_.isDigit)) {
        decision = input.toInt
        val check = userMatch(decision)
        if (check > 0) {
          mdlbSelect(decision)
          decision = 0
        } else if (check < 0)
          println("C'mon, you know what inputs are needed.\nEither a 0, 1, or 2.\n")
      }
      else
        println("C'mon, you know what inputs are needed.\nEither a 0, 1, or 2.\n")
    }
  }

  def userMatch(x:Int):Int = x match {
    case 0 => 0
    case 1 => 1   //anonymous
    case 2 => 1   //invoke userSelection
    case _ => -1  //bad input
  }

  def mdlbSelect(outerDecision: Int):Unit = {
    var decision:Int = 0
    _userCheck = 0

    // if user is selected than will invoke a function to retrieve user_id from database based on name from users table
    if (outerDecision == 2)
      println("Sorry, but this is still getting some kinks buffed out.\nInstead you will be anonymous.\n")
    else
      _username = "anonymous"
    // username = findUser()
    // _userCheck = (user_id in findUser())...or perhaps findUser will return a tuple...
    println(s"\nGood to see you ${_username}!  Which Lib are you Mad about today?\n")

    // ------------replace when link to database is hashed out-------------
    println(s"Choose a number between 1 and ${_madlibCount}.\nHopefully you remember which ones you did before, if you played before.\nOtherwise to go back, hit 0.\n")
    do {
      val input:String = readLine()
      if (input.forall(_.isDigit)) {
        decision = input.toInt
        if (decision > 0 && decision <= _madlibCount) {
          _madlibCheck = decision
          decision = 0
          beginMadlib()
        }
        else
          println("Aren't you anxious for more.  Too bad all you see is all you get.\n")
      }
      else
        println("Use your numbers, customer.\n")
    }
    while (decision != 0)
    //---------------------------------------------------------------------

  }

  def madlibCount():Int = {
    mlData.getMLCount
  }
  def findMadlib():(String, String) = {
    mlData.getMadlib(_madlibCheck)
  }

  def beginMadlib():Unit = {
    val madlibTup = findMadlib()
    val madlibMain = madlibTup._1.split("::")
    val madlibSug = madlibTup._2.split("::")
    val wordCount = madlibMain.length - 1

    println(s"We're finally at the fun part!  Time to put in some words!\nEach word has a hint for the kind of word you need.\nPick $wordCount words in sequence.  Yeehaw!!\n")
    for (x <- 0 until wordCount) {
      print(s"\nWord ${x + 1} (${madlibSug(x)}): ")
      madlibMain(x) = madlibMain(x).concat(readLine())
    }
    println(s"Here it is!\n\n${madlibMain.mkString}\n")
    sleep(10000)
  }
}
