import java.sql.DriverManager
import java.sql.Connection
//import java.sql.PreparedStatement
//import java.sql.SQLException

//  this object will strictly be for data querying for database.
/*
Current needs in order of importance:
select madlib table data correctly according to madlib_id and return madlib and |word_sug|...done
create and select usernames correctly
insert save data
*/

object mlData {
  private val _url = "jdbc:mysql://localhost:3306/test"
  private val _username = "root"
  private val _password = ""

  def getMadlib(madlib_id:Int): (String,String) = {
    val connection:Connection = DriverManager.getConnection(_url, _username, _password)
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(s"SELECT madlib,word_sug FROM Madlibs WHERE madlib_id = $madlib_id;")
    resultSet.next()
    val data:(String,String) = (resultSet.getString(1),resultSet.getString(2))
    connection.close()
    data
  }
  def getMLCount:Int = {
    val connection:Connection = DriverManager.getConnection(_url, _username, _password)
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT count(*) From Madlibs")
    resultSet.next()
    val data:Int = resultSet.getInt(1)
    connection.close()
    data
  }
}
