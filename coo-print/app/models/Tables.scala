package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(MMaterial.schema, MMaxAvailable.schema, MProperties.schema, PAddress.schema, POrder.schema, POrderDetail.schema, PUser.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table MMaterial
   *  @param materialId Database column material_id SqlType(int4), PrimaryKey
   *  @param materialName Database column material_name SqlType(varchar), Length(32,true)
   *  @param tech Database column tech SqlType(varchar), Length(32,true)
   *  @param price Database column price SqlType(numeric)
   *  @param vColor Database column v_color SqlType(varchar), Length(32,true)
   *  @param vFinish Database column v_finish SqlType(varchar), Length(32,true)
   *  @param vLayer Database column v_layer SqlType(varchar), Length(32,true)
   *  @param vFill Database column v_fill SqlType(varchar), Length(32,true)
   *  @param vZoom Database column v_zoom SqlType(varchar), Length(32,true) */
  case class MMaterialRow(materialId: Int, materialName: String, tech: String, price: scala.math.BigDecimal, vColor: String, vFinish: String, vLayer: String, vFill: String, vZoom: String)
  /** GetResult implicit for fetching MMaterialRow objects using plain SQL queries */
  implicit def GetResultMMaterialRow(implicit e0: GR[Int], e1: GR[String], e2: GR[scala.math.BigDecimal]): GR[MMaterialRow] = GR{
    prs => import prs._
    MMaterialRow.tupled((<<[Int], <<[String], <<[String], <<[scala.math.BigDecimal], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table m_material. Objects of this class serve as prototypes for rows in queries. */
  class MMaterial(_tableTag: Tag) extends Table[MMaterialRow](_tableTag, "m_material") {
    def * = (materialId, materialName, tech, price, vColor, vFinish, vLayer, vFill, vZoom) <> (MMaterialRow.tupled, MMaterialRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(materialId), Rep.Some(materialName), Rep.Some(tech), Rep.Some(price), Rep.Some(vColor), Rep.Some(vFinish), Rep.Some(vLayer), Rep.Some(vFill), Rep.Some(vZoom)).shaped.<>({r=>import r._; _1.map(_=> MMaterialRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column material_id SqlType(int4), PrimaryKey */
    val materialId: Rep[Int] = column[Int]("material_id", O.PrimaryKey)
    /** Database column material_name SqlType(varchar), Length(32,true) */
    val materialName: Rep[String] = column[String]("material_name", O.Length(32,varying=true))
    /** Database column tech SqlType(varchar), Length(32,true) */
    val tech: Rep[String] = column[String]("tech", O.Length(32,varying=true))
    /** Database column price SqlType(numeric) */
    val price: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("price")
    /** Database column v_color SqlType(varchar), Length(32,true) */
    val vColor: Rep[String] = column[String]("v_color", O.Length(32,varying=true))
    /** Database column v_finish SqlType(varchar), Length(32,true) */
    val vFinish: Rep[String] = column[String]("v_finish", O.Length(32,varying=true))
    /** Database column v_layer SqlType(varchar), Length(32,true) */
    val vLayer: Rep[String] = column[String]("v_layer", O.Length(32,varying=true))
    /** Database column v_fill SqlType(varchar), Length(32,true) */
    val vFill: Rep[String] = column[String]("v_fill", O.Length(32,varying=true))
    /** Database column v_zoom SqlType(varchar), Length(32,true) */
    val vZoom: Rep[String] = column[String]("v_zoom", O.Length(32,varying=true))
  }
  /** Collection-like TableQuery object for table MMaterial */
  lazy val MMaterial = new TableQuery(tag => new MMaterial(tag))

  /** Entity class storing rows of table MMaxAvailable
   *  @param maxId Database column max_id SqlType(int4), PrimaryKey
   *  @param materialName Database column material_name SqlType(varchar), Length(32,true)
   *  @param maxX Database column max_x SqlType(int4)
   *  @param maxY Database column max_y SqlType(int4)
   *  @param maxZ Database column max_z SqlType(int4) */
  case class MMaxAvailableRow(maxId: Int, materialName: String, maxX: Int, maxY: Int, maxZ: Int)
  /** GetResult implicit for fetching MMaxAvailableRow objects using plain SQL queries */
  implicit def GetResultMMaxAvailableRow(implicit e0: GR[Int], e1: GR[String]): GR[MMaxAvailableRow] = GR{
    prs => import prs._
    MMaxAvailableRow.tupled((<<[Int], <<[String], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table m_max_available. Objects of this class serve as prototypes for rows in queries. */
  class MMaxAvailable(_tableTag: Tag) extends Table[MMaxAvailableRow](_tableTag, "m_max_available") {
    def * = (maxId, materialName, maxX, maxY, maxZ) <> (MMaxAvailableRow.tupled, MMaxAvailableRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(maxId), Rep.Some(materialName), Rep.Some(maxX), Rep.Some(maxY), Rep.Some(maxZ)).shaped.<>({r=>import r._; _1.map(_=> MMaxAvailableRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column max_id SqlType(int4), PrimaryKey */
    val maxId: Rep[Int] = column[Int]("max_id", O.PrimaryKey)
    /** Database column material_name SqlType(varchar), Length(32,true) */
    val materialName: Rep[String] = column[String]("material_name", O.Length(32,varying=true))
    /** Database column max_x SqlType(int4) */
    val maxX: Rep[Int] = column[Int]("max_x")
    /** Database column max_y SqlType(int4) */
    val maxY: Rep[Int] = column[Int]("max_y")
    /** Database column max_z SqlType(int4) */
    val maxZ: Rep[Int] = column[Int]("max_z")
  }
  /** Collection-like TableQuery object for table MMaxAvailable */
  lazy val MMaxAvailable = new TableQuery(tag => new MMaxAvailable(tag))

  /** Entity class storing rows of table MProperties
   *  @param propType Database column prop_type SqlType(varchar), Length(10,true)
   *  @param propName Database column prop_name SqlType(varchar), Length(32,true)
   *  @param propPrice Database column prop_price SqlType(numeric) */
  case class MPropertiesRow(propType: String, propName: String, propPrice: scala.math.BigDecimal)
  /** GetResult implicit for fetching MPropertiesRow objects using plain SQL queries */
  implicit def GetResultMPropertiesRow(implicit e0: GR[String], e1: GR[scala.math.BigDecimal]): GR[MPropertiesRow] = GR{
    prs => import prs._
    MPropertiesRow.tupled((<<[String], <<[String], <<[scala.math.BigDecimal]))
  }
  /** Table description of table m_properties. Objects of this class serve as prototypes for rows in queries. */
  class MProperties(_tableTag: Tag) extends Table[MPropertiesRow](_tableTag, "m_properties") {
    def * = (propType, propName, propPrice) <> (MPropertiesRow.tupled, MPropertiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(propType), Rep.Some(propName), Rep.Some(propPrice)).shaped.<>({r=>import r._; _1.map(_=> MPropertiesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column prop_type SqlType(varchar), Length(10,true) */
    val propType: Rep[String] = column[String]("prop_type", O.Length(10,varying=true))
    /** Database column prop_name SqlType(varchar), Length(32,true) */
    val propName: Rep[String] = column[String]("prop_name", O.Length(32,varying=true))
    /** Database column prop_price SqlType(numeric) */
    val propPrice: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("prop_price")

    /** Primary key of MProperties (database name m_properties_pk) */
    val pk = primaryKey("m_properties_pk", (propType, propName))
  }
  /** Collection-like TableQuery object for table MProperties */
  lazy val MProperties = new TableQuery(tag => new MProperties(tag))

  /** Entity class storing rows of table PAddress
   *  @param userId Database column user_id SqlType(varchar), Length(10,true)
   *  @param addressId Database column address_id SqlType(int4)
   *  @param addressee Database column addressee SqlType(varchar), Length(32,true)
   *  @param company Database column company SqlType(varchar), Length(64,true), Default(None)
   *  @param province Database column province SqlType(varchar), Length(10,true)
   *  @param city Database column city SqlType(varchar), Length(10,true)
   *  @param district Database column district SqlType(varchar), Length(10,true)
   *  @param detailAddress Database column detail_address SqlType(varchar), Length(100,true)
   *  @param tel Database column tel SqlType(varchar), Length(11,true)
   *  @param email Database column email SqlType(varchar), Length(64,true) */
  case class PAddressRow(userId: String, addressId: Int, addressee: String, company: Option[String] = None, province: String, city: String, district: String, detailAddress: String, tel: String, email: String)
  /** GetResult implicit for fetching PAddressRow objects using plain SQL queries */
  implicit def GetResultPAddressRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[String]]): GR[PAddressRow] = GR{
    prs => import prs._
    PAddressRow.tupled((<<[String], <<[Int], <<[String], <<?[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table p_address. Objects of this class serve as prototypes for rows in queries. */
  class PAddress(_tableTag: Tag) extends Table[PAddressRow](_tableTag, "p_address") {
    def * = (userId, addressId, addressee, company, province, city, district, detailAddress, tel, email) <> (PAddressRow.tupled, PAddressRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(addressId), Rep.Some(addressee), company, Rep.Some(province), Rep.Some(city), Rep.Some(district), Rep.Some(detailAddress), Rep.Some(tel), Rep.Some(email)).shaped.<>({r=>import r._; _1.map(_=> PAddressRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(varchar), Length(10,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(10,varying=true))
    /** Database column address_id SqlType(int4) */
    val addressId: Rep[Int] = column[Int]("address_id")
    /** Database column addressee SqlType(varchar), Length(32,true) */
    val addressee: Rep[String] = column[String]("addressee", O.Length(32,varying=true))
    /** Database column company SqlType(varchar), Length(64,true), Default(None) */
    val company: Rep[Option[String]] = column[Option[String]]("company", O.Length(64,varying=true), O.Default(None))
    /** Database column province SqlType(varchar), Length(10,true) */
    val province: Rep[String] = column[String]("province", O.Length(10,varying=true))
    /** Database column city SqlType(varchar), Length(10,true) */
    val city: Rep[String] = column[String]("city", O.Length(10,varying=true))
    /** Database column district SqlType(varchar), Length(10,true) */
    val district: Rep[String] = column[String]("district", O.Length(10,varying=true))
    /** Database column detail_address SqlType(varchar), Length(100,true) */
    val detailAddress: Rep[String] = column[String]("detail_address", O.Length(100,varying=true))
    /** Database column tel SqlType(varchar), Length(11,true) */
    val tel: Rep[String] = column[String]("tel", O.Length(11,varying=true))
    /** Database column email SqlType(varchar), Length(64,true) */
    val email: Rep[String] = column[String]("email", O.Length(64,varying=true))

    /** Primary key of PAddress (database name p_address_pk) */
    val pk = primaryKey("p_address_pk", (userId, addressId))
  }
  /** Collection-like TableQuery object for table PAddress */
  lazy val PAddress = new TableQuery(tag => new PAddress(tag))

  /** Entity class storing rows of table POrder
   *  @param orderId Database column order_id SqlType(varchar), PrimaryKey, Length(10,true)
   *  @param orderDt Database column order_dt SqlType(timestamp)
   *  @param userId Database column user_id SqlType(varchar), Length(10,true)
   *  @param addressId Database column address_id SqlType(int4)
   *  @param orderPrice Database column order_price SqlType(numeric)
   *  @param orderStatus Database column order_status SqlType(int4), Default(0)
   *  @param userMemo Database column user_memo SqlType(text), Default(None)
   *  @param operMemo Database column oper_memo SqlType(text), Default(None) */
  case class POrderRow(orderId: String, orderDt: java.sql.Timestamp, userId: String, addressId: Int, orderPrice: scala.math.BigDecimal, orderStatus: Int = 0, userMemo: Option[String] = None, operMemo: Option[String] = None)
  /** GetResult implicit for fetching POrderRow objects using plain SQL queries */
  implicit def GetResultPOrderRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Int], e3: GR[scala.math.BigDecimal], e4: GR[Option[String]]): GR[POrderRow] = GR{
    prs => import prs._
    POrderRow.tupled((<<[String], <<[java.sql.Timestamp], <<[String], <<[Int], <<[scala.math.BigDecimal], <<[Int], <<?[String], <<?[String]))
  }
  /** Table description of table p_order. Objects of this class serve as prototypes for rows in queries. */
  class POrder(_tableTag: Tag) extends Table[POrderRow](_tableTag, "p_order") {
    def * = (orderId, orderDt, userId, addressId, orderPrice, orderStatus, userMemo, operMemo) <> (POrderRow.tupled, POrderRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(orderId), Rep.Some(orderDt), Rep.Some(userId), Rep.Some(addressId), Rep.Some(orderPrice), Rep.Some(orderStatus), userMemo, operMemo).shaped.<>({r=>import r._; _1.map(_=> POrderRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column order_id SqlType(varchar), PrimaryKey, Length(10,true) */
    val orderId: Rep[String] = column[String]("order_id", O.PrimaryKey, O.Length(10,varying=true))
    /** Database column order_dt SqlType(timestamp) */
    val orderDt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("order_dt")
    /** Database column user_id SqlType(varchar), Length(10,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(10,varying=true))
    /** Database column address_id SqlType(int4) */
    val addressId: Rep[Int] = column[Int]("address_id")
    /** Database column order_price SqlType(numeric) */
    val orderPrice: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("order_price")
    /** Database column order_status SqlType(int4), Default(0) */
    val orderStatus: Rep[Int] = column[Int]("order_status", O.Default(0))
    /** Database column user_memo SqlType(text), Default(None) */
    val userMemo: Rep[Option[String]] = column[Option[String]]("user_memo", O.Default(None))
    /** Database column oper_memo SqlType(text), Default(None) */
    val operMemo: Rep[Option[String]] = column[Option[String]]("oper_memo", O.Default(None))
  }
  /** Collection-like TableQuery object for table POrder */
  lazy val POrder = new TableQuery(tag => new POrder(tag))

  /** Entity class storing rows of table POrderDetail
   *  @param orderId Database column order_id SqlType(varchar), Length(10,true)
   *  @param fileName Database column file_name SqlType(varchar), Length(64,true)
   *  @param printCnt Database column print_cnt SqlType(int4), Default(1)
   *  @param metarilId Database column metaril_id SqlType(int4)
   *  @param color Database column color SqlType(varchar), Length(10,true), Default(None)
   *  @param finish Database column finish SqlType(varchar), Length(10,true), Default(None)
   *  @param layer Database column layer SqlType(varchar), Length(10,true), Default(None)
   *  @param fill Database column fill SqlType(varchar), Length(10,true), Default(None)
   *  @param zoom Database column zoom SqlType(varchar), Length(10,true), Default(None)
   *  @param price Database column price SqlType(numeric) */
  case class POrderDetailRow(orderId: String, fileName: String, printCnt: Int = 1, metarilId: Int, color: Option[String] = None, finish: Option[String] = None, layer: Option[String] = None, fill: Option[String] = None, zoom: Option[String] = None, price: scala.math.BigDecimal)
  /** GetResult implicit for fetching POrderDetailRow objects using plain SQL queries */
  implicit def GetResultPOrderDetailRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[String]], e3: GR[scala.math.BigDecimal]): GR[POrderDetailRow] = GR{
    prs => import prs._
    POrderDetailRow.tupled((<<[String], <<[String], <<[Int], <<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[scala.math.BigDecimal]))
  }
  /** Table description of table p_order_detail. Objects of this class serve as prototypes for rows in queries. */
  class POrderDetail(_tableTag: Tag) extends Table[POrderDetailRow](_tableTag, "p_order_detail") {
    def * = (orderId, fileName, printCnt, metarilId, color, finish, layer, fill, zoom, price) <> (POrderDetailRow.tupled, POrderDetailRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(orderId), Rep.Some(fileName), Rep.Some(printCnt), Rep.Some(metarilId), color, finish, layer, fill, zoom, Rep.Some(price)).shaped.<>({r=>import r._; _1.map(_=> POrderDetailRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6, _7, _8, _9, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column order_id SqlType(varchar), Length(10,true) */
    val orderId: Rep[String] = column[String]("order_id", O.Length(10,varying=true))
    /** Database column file_name SqlType(varchar), Length(64,true) */
    val fileName: Rep[String] = column[String]("file_name", O.Length(64,varying=true))
    /** Database column print_cnt SqlType(int4), Default(1) */
    val printCnt: Rep[Int] = column[Int]("print_cnt", O.Default(1))
    /** Database column metaril_id SqlType(int4) */
    val metarilId: Rep[Int] = column[Int]("metaril_id")
    /** Database column color SqlType(varchar), Length(10,true), Default(None) */
    val color: Rep[Option[String]] = column[Option[String]]("color", O.Length(10,varying=true), O.Default(None))
    /** Database column finish SqlType(varchar), Length(10,true), Default(None) */
    val finish: Rep[Option[String]] = column[Option[String]]("finish", O.Length(10,varying=true), O.Default(None))
    /** Database column layer SqlType(varchar), Length(10,true), Default(None) */
    val layer: Rep[Option[String]] = column[Option[String]]("layer", O.Length(10,varying=true), O.Default(None))
    /** Database column fill SqlType(varchar), Length(10,true), Default(None) */
    val fill: Rep[Option[String]] = column[Option[String]]("fill", O.Length(10,varying=true), O.Default(None))
    /** Database column zoom SqlType(varchar), Length(10,true), Default(None) */
    val zoom: Rep[Option[String]] = column[Option[String]]("zoom", O.Length(10,varying=true), O.Default(None))
    /** Database column price SqlType(numeric) */
    val price: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("price")

    /** Primary key of POrderDetail (database name order_detail_pk) */
    val pk = primaryKey("order_detail_pk", (orderId, fileName))
  }
  /** Collection-like TableQuery object for table POrderDetail */
  lazy val POrderDetail = new TableQuery(tag => new POrderDetail(tag))

  /** Entity class storing rows of table PUser
   *  @param clientId Database column client_id SqlType(varchar), PrimaryKey, Length(8,true)
   *  @param userId Database column user_id SqlType(varchar), Length(32,true), Default(None)
   *  @param userName Database column user_name SqlType(varchar), Length(32,true), Default(None)
   *  @param passwd Database column passwd SqlType(varchar), Length(32,true), Default(None)
   *  @param email Database column email SqlType(varchar), Length(64,true), Default(None)
   *  @param tel Database column tel SqlType(varchar), Length(11,true), Default(None)
   *  @param company Database column company SqlType(varchar), Length(64,true), Default(None)
   *  @param other Database column other SqlType(varchar), Length(64,true), Default(None)
   *  @param registerDt Database column register_dt SqlType(timestamp), Default(None)
   *  @param firstOrderDt Database column first_order_dt SqlType(timestamp), Default(None) */
  case class PUserRow(clientId: String, userId: Option[String] = None, userName: Option[String] = None, passwd: Option[String] = None, email: Option[String] = None, tel: Option[String] = None, company: Option[String] = None, other: Option[String] = None, registerDt: Option[java.sql.Timestamp] = None, firstOrderDt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching PUserRow objects using plain SQL queries */
  implicit def GetResultPUserRow(implicit e0: GR[String], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]]): GR[PUserRow] = GR{
    prs => import prs._
    PUserRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table p_user. Objects of this class serve as prototypes for rows in queries. */
  class PUser(_tableTag: Tag) extends Table[PUserRow](_tableTag, "p_user") {
    def * = (clientId, userId, userName, passwd, email, tel, company, other, registerDt, firstOrderDt) <> (PUserRow.tupled, PUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(clientId), userId, userName, passwd, email, tel, company, other, registerDt, firstOrderDt).shaped.<>({r=>import r._; _1.map(_=> PUserRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column client_id SqlType(varchar), PrimaryKey, Length(8,true) */
    val clientId: Rep[String] = column[String]("client_id", O.PrimaryKey, O.Length(8,varying=true))
    /** Database column user_id SqlType(varchar), Length(32,true), Default(None) */
    val userId: Rep[Option[String]] = column[Option[String]]("user_id", O.Length(32,varying=true), O.Default(None))
    /** Database column user_name SqlType(varchar), Length(32,true), Default(None) */
    val userName: Rep[Option[String]] = column[Option[String]]("user_name", O.Length(32,varying=true), O.Default(None))
    /** Database column passwd SqlType(varchar), Length(32,true), Default(None) */
    val passwd: Rep[Option[String]] = column[Option[String]]("passwd", O.Length(32,varying=true), O.Default(None))
    /** Database column email SqlType(varchar), Length(64,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(64,varying=true), O.Default(None))
    /** Database column tel SqlType(varchar), Length(11,true), Default(None) */
    val tel: Rep[Option[String]] = column[Option[String]]("tel", O.Length(11,varying=true), O.Default(None))
    /** Database column company SqlType(varchar), Length(64,true), Default(None) */
    val company: Rep[Option[String]] = column[Option[String]]("company", O.Length(64,varying=true), O.Default(None))
    /** Database column other SqlType(varchar), Length(64,true), Default(None) */
    val other: Rep[Option[String]] = column[Option[String]]("other", O.Length(64,varying=true), O.Default(None))
    /** Database column register_dt SqlType(timestamp), Default(None) */
    val registerDt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("register_dt", O.Default(None))
    /** Database column first_order_dt SqlType(timestamp), Default(None) */
    val firstOrderDt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("first_order_dt", O.Default(None))
  }
  /** Collection-like TableQuery object for table PUser */
  lazy val PUser = new TableQuery(tag => new PUser(tag))
}
