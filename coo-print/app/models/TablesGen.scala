package models

object TablesGen extends App {
  slick.codegen.SourceCodeGenerator.main(
  Array("slick.driver.PostgresDriver", 
      "slick.jdbc.DatabaseUrlDataSource", 
      "jdbc:postgresql://localhost:5432/postgres", 
      "C:/Users/wli18/git/coo-print/coo-print/app", "models", "postgres", "postgres")
)
}