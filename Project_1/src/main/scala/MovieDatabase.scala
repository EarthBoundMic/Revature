import scala.io.StdIn.readLine
import org.apache.spark.sql.SparkSession

import scala.util.control.Breaks.break

object MovieDatabase {
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
    //spark.sql("show databases").show
    //spark.sql("USE DATABASE p1")
    if (!spark.catalog.tableExists("p1.user_list")) {
      println("creating user list")
      spark.sql("CREATE TABLE IF NOT EXISTS p1.user_list(id Int,username String,userlevel String,password String)")
    }
    val adminCheck = spark.sql("select username from p1.user_list where userlevel = \"Admin\"")
    if (adminCheck.collect().isEmpty)
      println("no admin user located")
    else
      println(adminCheck.collect().take(1)(1))
  }
  def createUser(spark:SparkSession, isAdmin: Boolean): Unit = {
    print("create user\nenter username: ")
    val name = readLine()
    print("\ncreate password: ")
    val password = readLine()
    if (isAdmin)
      spark.sql(s"INSERT INTO TABLE user_list (id,username,userlevel,password) VALUES(1, $name, Admin, $password)")
    else {
      val id = spark.sql("SELECT max(id) AS cur FROM p1.user_list").collect().take(1)(1)
      spark.sql(s"INSERT INTO TABLE user_list (id,username,userlevel,password) VALUES(${id + 1}, $name, Basic, $password)")
    }
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