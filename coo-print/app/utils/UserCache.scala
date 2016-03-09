package utils

import play.api.mvc.Request 
import play.api.cache.CacheApi
import javax.inject.Inject
import javax.inject.Singleton
import scala.collection.mutable.Map

object UserCache {
      
  val SKEY_OF_CLIENT_ID = "c_id"
}

@Singleton
class UserCache (val cache: CacheApi) {
  
  def get[B](key : String)(implicit request : Request[_]) : Option[B] = {
     val cidOpt = request.session.get(UserCache.SKEY_OF_CLIENT_ID)
     if (!cidOpt.isEmpty) {
       val cid = cidOpt.get
       val userMap = cache.getOrElse(cid) {
         val map = Map.empty[String, B]
         cache.set(cid, map)
         map
       }
       
       userMap.get(key)
     } else {
       None
     }
  }
  
  def set[B](key : String, value : B)(implicit request : Request[_]) : Option[B] = {
     val cidOpt = request.session.get(UserCache.SKEY_OF_CLIENT_ID)
     if (!cidOpt.isEmpty) {
       val cid = cidOpt.get
       val userMap = cache.getOrElse(cid) {
         val map = Map.empty[String, B]
         cache.set(cid, map)
         map
       }
       
       userMap += (key -> value)
       
       Some(value)
     } else {
       None
     }
  }
  
  def remove()(implicit request : Request[_]) : Unit = {
    val cidOpt = request.session.get(UserCache.SKEY_OF_CLIENT_ID)
     if (!cidOpt.isEmpty) {
       cache.remove(cidOpt.get)
     }
  }
  
  
}