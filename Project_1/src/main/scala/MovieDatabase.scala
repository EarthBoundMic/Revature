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
    /*if (!spark.catalog.tableExists("movie_list_main")) {
      println("creating primary movie list")
      spark.sql("CREATE TABLE IF NOT EXISTS movie_list_main (title String,director String,cast String,year Int,criticScore Decimal,criticRatings Int,audienceScore Decimal,audienceCount Int,network String,genre String) ROW FORMAT DELIMITED FIELDS TERMINATED BY ','")
      spark.sql("LOAD DATA LOCAL INPATH 'pj1datasample.txt' INTO TABLE movie_list_main")
      spark.sql("CREATE TABLE IF NOT EXISTS movie_list_main (title String,director String,cast String,year Int,criticScore Decimal,criticRatings Int,audienceScore Decimal,audienceCount Int,network String,genre String)")
      spark.sql("INSERT INTO movie_list_copy (SELECT * FROM movie_list_main)")
    }
    spark.sql("select * from movie_list_main").show*/
    val adminCheck = spark.sql("select username from user_list where userlevel = \"Admin\"")
    if (adminCheck.collect().isEmpty)
      println("no admin user located")
    else {
      println("Admin name is: " + adminCheck.collect().take(1)(0).getString(0))
      _adminCreated = true
    }
  }

  def mainMenu(spark: SparkSession): Unit = {
    var selection: Int = 0
    do {
      if (_isLogin) {
        if (_isAdmin)
          print("Select option number:\n1. Sample Data\n2. Basic Data\n3. User Data\n0. Exit\n> ")
        else
          print("Select option number:\n1. Sample Data\n2. Basic Data\n0. Exit\n> ")
      } else
        print("Select option number:\n1. Sample Data\n2. User Login\n0. Exit\n> ")
      selection = readLine().toInt
      if (selection == 1)
        sampleQueries(spark)
      else if (selection == 2) {
        if (!_isLogin)
          userLoginMenu(spark)
        else
          basicQueries(spark)
      } else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def sampleQueries(spark:SparkSession): Unit = {
    // sample example queries go here
    // can use data that can't be shown but not allowed to see it
    var selection: Int = 0
    do {
      print("Sample Queries\n\nSelect option number:\n1. List first 20 movies\n0. Go back\n> ")
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
      print("Basic Queries\n\nSelect option number:\n1. List first 20 movies\n0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1)
        spark.sql("SELECT * FROM movie_list_copy ORDER BY year LIMIT 20").show
      else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def adminUserManip(spark:SparkSession): Unit = {
    // change and remove users here including self
  }

  def userLoginMenu(spark: SparkSession): Unit = {
    // this is login menu, will consist of login, create user, and back
    // however, if no admin, create user will go to creating admin account
    var selection: Int = 0
    do {
      print("User Select\nSelect option number:\n1. Create User\n2. Login\n0. Go back\n> ")
      selection = readLine().toInt
      if (selection == 1)
        createUser(spark)
      else if (selection == 2)
        userLogin(spark)
      else
        println("invalid input\n")
    }
    while (selection != 0)
  }

  def userLogin(spark:SparkSession): Unit = {
    println("User Login\nIf you change your mind, leave username blank")
    var selection = 0
    do {
      print("\nEnter username:\n> ")
      val input = readLine()
      val query = spark.sql(s"SELECT userlevel, password FROM user_list WHERE username = $input").collect()
      if (query.isEmpty) {
        println("\nname doesn't exist\n")
        selection = 1
      }
      else {
        print("\nEnter password:\n> ")
        val password = readLine()
        if (query.take(1)(1).getString(1) != password) {
          println("\ninvalid password\n")
          selection = 1
        }
        else {
          _user = input
          _isLogin = true
          selection = 0
        }
        if (query.take(1)(1).getString(0) == "Admin") {
          _isAdmin = true
        }
      }
    }
    while (selection != 0)
  }

  def createUser(spark:SparkSession): Unit = {
    if (!_adminCreated)
      println("There must be an Admin, and there is none.\nNew user will be Admin\n")
    print("create user\nenter username: ")
    val name = readLine()
    print("\ncreate password: ")
    val password = readLine()
    if (!_adminCreated) {
      spark.sql(s"INSERT INTO user_list (username,userlevel,password) VALUES ($name, ${"Admin"}, $password)")
      _adminCreated = true
      _isAdmin = true
    } else {
      //val id = spark.sql("SELECT max(id) AS cur FROM user_list").collect().take(1)(1)
      //println("ugh: " + id)
      spark.sql(s"INSERT INTO user_list (username,userlevel,password) VALUES ($name, Basic, $password)")
    }
  }

  def userMenu(spark:SparkSession): Unit = {
    // basic user menu
  }

  def adminMenu(spark:SparkSession): Unit = {
    // admin user menu
  }
}

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