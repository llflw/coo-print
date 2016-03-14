package utils

import slick.driver.PostgresDriver.api._
import javax.inject.Inject

object DBUtils {
  
  def getNexClientId = {
    sql"""select nextval('seq_client_id') """.as[String]
  }
}