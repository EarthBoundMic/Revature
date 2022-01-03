import scala.io.StdIn.readLine
import org.apache.spark.sql.SparkSession

//import scala.util.control.Breaks.break

object MovieDatabase {
  private var _adminCreated = false
  private var _isAdmin = false
  private var _isLogin = false
  private var _user = ""

  def main(args:Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "C:\\hadoop")
    val spark = SparkSession
      .builder
      .appName("MovieDatabase")
      .config("spark.master", "local")
      .enableHiveSupport()
      .getOrCreate()
    println("created spark session")
    spark.sparkContext.setLogLevel("ERROR")
    loadData(spark)
    mainMenu(spark)
    //spark.sql("select title,network from p1.movie_list_main").show
    //spark.sql("SELECT * FROM p1.movie_list_main where network LIKE 'A%'").show
    //spark.sql("describe p1.movie_list_main").show
    spark.sql("DROP TABLE IF EXISTS movie_list_copy")
    println("\nGoodbye")
    spark.close()
  }

  def loadData(spark:SparkSession): Unit = {
    // this will be an init to check if main table exists and has data
    // also extends to user list table
    // afterward create a copy of the table for ease of use
    if (!spark.catalog.databaseExists("p1")) {
      println("first setup: creating database")
      spark.sql("CREATE DATABASE p1")
    }
    spark.sql("USE p1")
    //spark.sql("drop table if exists user_list")
    //spark.sql("show databases").show
    //spark.sql("USE DATABASE p1")
    if (!spark.catalog.tableExists("user_list")) {
      println("creating user list")
      spark.sql("CREATE TABLE IF NOT EXISTS user_list (username String, userlevel String, password String)")
    }
    //spark.sql("describe user_list").show
    //spark.sql("drop table if exists movie_list_main")
    if (!spark.catalog.tableExists("movie_list_main")) {
      println("creating primary movie list")
      spark.sql("CREATE TABLE IF NOT EXISTS movie_list_main (title String,genre String,director String,cast String,year Int,criticScore Double,criticRatings Int,audienceScore Double,audienceCount Int,network String) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','")
      spark.sql("LOAD DATA LOCAL INPATH 'pj1datasample.txt' INTO TABLE movie_list_main")
    }
    //spark.sql("drop table if exists movie_list_copy")
    if (!spark.catalog.tableExists("movie_list_copy")) {
      spark.sql("CREATE TABLE IF NOT EXISTS movie_list_copy (title String,genre String,director String,cast String,year Int,criticScore Double,criticRatings Int,audienceScore Double,audienceCount Int,network String)")
      spark.sql("INSERT INTO movie_list_copy (SELECT * FROM movie_list_main)")
    }
    //spark.sql("select * from movie_list_main").show
    //spark.sql("select * from movie_list_copy").show
    val adminCheck = spark.sql("select username from user_list where userlevel = 'Admin'")
    if (adminCheck.collect().isEmpty) {
      println("no admin user located")
    } else {
      println("Admin name is: " + adminCheck.collect().take(1)(0).getString(0))
      _adminCreated = true
    }
  }

  def mainMenu(spark: SparkSession): Unit = {
    var selection: Int = 0
    do {
      if (_isLogin) {
        println(s"\n\nWelcome ${_user}")
        if (_isAdmin)
          print("\nAdmin Main Menu\nSelect option number:\n1. Sample Data\n2. Basic Data\n3. User Data\n0. Exit\n> ")
        else
          print("\nBasic Main Menu\nSelect option number:\n1. Sample Data\n2. Basic Data\n0. Exit\n> ")
      } else
        print("\n\n\n\nMain Menu\nSelect option number:\n1. Sample Data\n2. User Login\n0. Exit\n> ")
      selection = readLine().toInt
      if (selection == 1)
        sampleQueries(spark)
      else if (selection == 2) {
        if (!_isLogin)
          userLoginMenu(spark)
        else
          basicQueries(spark)
      }
      else if (_isAdmin && selection == 3)
          adminUserManip(spark)
      else if (selection != 0)
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def sampleQueries(spark:SparkSession): Unit = {
    // sample example queries go here
    // can use data that can't be shown but not allowed to see it
    var selection: Int = 0
    do {
      print("\nSample Queries\n\nSelect option number:\n1. List first 20 movies\n0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1)
        spark.sql("SELECT network, title, genre, year, criticScore FROM movie_list_copy ORDER BY year LIMIT 20").show
      else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def basicQueries(spark:SparkSession): Unit = {
    // basic example queries go here
    var selection: Int = 0
    do {
      print("\nBasic Queries\n\n" +
        "Select option number:\n" +
        "1. List first 20 movies\n" +
        "2. List movies that were released in 2019\n" +
        "3. List directors that got a critic score above 75%\n" +
        "4. List popular genres (by amount of audience ratings)\n" +
        "5. List movies with both good critic and audience scores (above 70%)\n" +
        "6. List movies with cast members where critics didn't like the movies but audience did\n" +
        "0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1)
        spark.sql("SELECT network, title, genre, year, director, criticScore, audienceScore FROM movie_list_copy ORDER BY year LIMIT 20").show
      else if (selection == 2)
        spark.sql("SELECT title, genre, year FROM movie_list_copy where year = 2019").show
      else if (selection == 3)
        spark.sql("SELECT director FROM movie_list_copy WHERE criticScore >= .75").show
      else if (selection == 4)
        spark.sql("SELECT genre FROM movie_list_copy WHERE audienceCount > 1000 GROUP BY genre").show
      else if (selection == 5)
        spark.sql("SELECT title FROM movie_list_copy WHERE criticScore >= .7 AND audienceScore >= .7").show
      else if (selection == 6)
        spark.sql("SELECT title, cast, criticScore, audienceScore FROM movie_list_copy WHERE criticScore < .4 AND audienceScore > .6").show
      else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def adminUserManip(spark:SparkSession): Unit = {
    // change and remove users here including self
    var selection: Int = 0
    do {
      print("\nUser Data Access\n\nSelect option number:\n1. Change Data\n2. Delete Data\n3. List Users\n0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1)
        alterUserData(spark)
      else if (selection == 2)
        deleteUserData(spark)
      else if (selection == 3)
        spark.sql("SELECT username, userlevel FROM user_list ORDER BY userlevel LIMIT 20").show
      else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def userLoginMenu(spark: SparkSession): Unit = {
    // this is login menu, will consist of login, create user, and back
    // however, if no admin, create user will go to creating admin account
    var selection: Int = 0
    do {
      print("\nUser Select\nSelect option number:\n1. Create User\n2. Login\n0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1) {
        createUser(spark)
        selection = 0
      } else if (selection == 2) {
        userLogin(spark)
        selection = 0
      } else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def userLogin(spark:SparkSession): Unit = {
    println("\nUser Login\nIf you change your mind, leave username blank")
    var selection = 0
    do {
      selection = 0
      print("\nEnter username: ")
      val input = readLine()
      val query = spark.sql(s"SELECT userlevel, password FROM user_list WHERE username = '$input'").collect()
      if (input != "") {
        if (query.isEmpty) {
          println("\nname doesn't exist\n")
          selection = 1
        }
        else {
          print("\nEnter password: ")
          val password = readLine()
          if (query.take(1)(0).getString(1) != password) {
            println("\ninvalid password\n")
            selection = 1
          }
          else {
            _user = input
            _isLogin = true
          }
          if (query.take(1)(0).getString(0) == "Admin") {
            _isAdmin = true
          }
        }
      }
    }
    while (selection != 0)
  }

  def createUser(spark:SparkSession): Unit = {
    if (!_adminCreated)
      println("\nThere must be an Admin, and there is none.\nNew user will be Admin\n")
    print("create user\nenter username: ")
    val name = readLine()
    print("\ncreate password: ")
    val password = readLine()
    if (!_adminCreated) {
      spark.sql(s"INSERT INTO TABLE user_list VALUES ('$name','Admin','$password')")
      _adminCreated = true
      _isAdmin = true
    } else
      spark.sql(s"INSERT INTO TABLE user_list VALUES ('$name', 'Basic', '$password')")
    _isLogin = true
    _user = name
  }

  def alterUserData(spark: SparkSession): Unit = {
    // change username of a user
    var selection = 1
    do {
      print("\nSelect username:\n> ")
      val name = readLine()
      val query = spark.sql(s"select * from user_list where username = $name").collect()
      if (!query.isEmpty) {
        print("\nNew username:\n> ")
        val newName = readLine()
        spark.sql("ALTER ")
      }
    }
    while (selection != 0)
  }

  def deleteUserData(spark: SparkSession): Unit = {

  }

  /*def userMenu(spark:SparkSession): Unit = {
    // basic user menu
  }

  def adminMenu(spark:SparkSession): Unit = {
    // admin user menu
  }*/
}

//note to self: strings inserted into queries must have quotes around them...

/*
    quick notes on what is expected...

    must have user/admin only interfacing
    must have login (encryption optional)
    users can check data but admin adds, changes, and removes data
    must use spark to extract data and set it in tables/dataframes
    must implement partitioning and bucketing, even if the dataset is small
    finally, must create 6 querying questions and answer them
    must have at least one of each CRUD operation
    can't directly update and delete in hive from spark
    approach is get table from hive, manipulated it in spark, then overwrite back to hive
    confirmed that insert overwrite does work
 */

/*
    now the big part...how to organize it...

    i am thinking that at base, as in no login required, 1 to 2 questions will be answered
    as like a demo
    4 to 5 more need to be logged in to access tiered data
    the 6th only being answerable in admin since it would require update/delete
    can update table for user list for that

    as for the queries themselves, will have a base select statement that basic users
    will have access to in order to find the data they want...
    it will be all inside the where statement where users can specify a row
    to use as well as which columns to display

    admin level will only have additional power over user manipulation to remove
    users

    outside of MVP admins can restrict which rows a user can see
 */