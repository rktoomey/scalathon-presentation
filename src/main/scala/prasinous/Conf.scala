package prasinous

import com.mongodb.casbah.commons.Logging
import net.lag.configgy.Config
import com.mongodb.casbah.Imports._
import com.mongodb.{MongoOptions, ServerAddress, Mongo}
import com.mongodb.casbah.{MongoDB, MongoConnection}

trait Conf extends Logging {

  val confFile: String = "phase.conf"

  lazy private val cl = getClass.getClassLoader

  lazy val Configuration = {
    try {
      val config = Config.fromResource(confFile, cl)
      log.info("loaded config from %s", cl.getResource(confFile))
      config
    }
    catch {
      case t => {
        val urls = cl.asInstanceOf[java.net.URLClassLoader].getURLs.filter(!_.toString.endsWith(".jar"))
        throw new RuntimeException("failed to load %s from classpath:\n%s".format(confFile, urls.mkString("\n")), t)
      }
    }
  }
}

class MissingPropertyError(property: String) extends Error("Missing property: %s".format(property))

object DB extends Conf {
  private def connect(name: String): MongoDB = {
    val conn = new MongoConnection(
      new Mongo(
      new ServerAddress(
        Configuration.getString("mongodb.%s.hostname".format(name)).getOrElse(
          throw new IllegalArgumentException("'mongodb.%s' needs a 'hostname' setting".format(name))
        ),
        Configuration.getInt("mongodb.%s.port".format(name)).getOrElse(
          throw new IllegalArgumentException("'mongodb.%s' needs a 'port' setting".format(name))
        )
      ), {
        val opts = new MongoOptions
        opts.connectionsPerHost = 50
        opts.autoConnectRetry = true
        opts
      }
      )
    )
    conn(Configuration.getString("mongodb.%s.db".format(name)).
      getOrElse(throw new IllegalArgumentException("'mongodb.%s' needs a 'db' setting".format(name))))
  }

  val LibraryDB = connect("library")
}

object collections extends Logging {

  import prasinous.DB._

  implicit def usefulImplicitsForDefiningCollections[T <: MongoCollection](coll: T) = new {
    def index(dbo: DBObject): T = {
      val opts = MongoDBObject(
        "name" -> "idx_%d".format(System.nanoTime),
        "unique" -> false,
        "background" -> true)
      log.info("index: %s by: %s", coll.getName, dbo)
      coll.underlying.ensureIndex(dbo, opts)
      coll
    }

    def uniqueIndex(dbo: DBObject): T = {
      log.info("unique index: %s by: %s", coll.getName, dbo)
      val opts = MongoDBObject(
        "name" -> "unq_idx_%d".format(System.nanoTime),
        "unique" -> true,
        "background" -> true)
      coll.underlying.ensureIndex(dbo, opts)
      coll
    }
  }

  val author = LibraryDB("author").uniqueIndex(MongoDBObject("firstName" -> 1, "lastName" -> 1))
  val book = LibraryDB("books").uniqueIndex(MongoDBObject("title" -> 1))
  val bookAuthor = LibraryDB("book_author").uniqueIndex(MongoDBObject("bookId" -> 1, "authorId" -> 1))
  val borrower = LibraryDB("borrower")
  val borrowal = LibraryDB("borrowal").uniqueIndex(MongoDBObject("bookId" -> 1, "borrowerId" -> 1, "scheduledToReturnOn" -> 1))

}


