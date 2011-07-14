package prasinous

import org.specs2.mutable.Specification
import com.mongodb.casbah.commons.Logging
import com.novus.salat._
import com.novus.salat.global._
import prasinous.model.Author
import org.specs2.specification.Scope
import prasinous.dao.{BookDAO, AuthorDAO}
import prasinous.model._

class AuthorDAOSpec extends SequentialDbSpec {
  "Author DAO" should {
    "find books by author" in new testDataScope {
      success
    }
  }
}

trait testDataScope extends Scope with Logging {

  com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers()
  com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()

  AuthorDAO.bookAuthor.collection.drop()
  AuthorDAO.collection.drop()
  BookDAO.collection.drop()

  val author1 = Author(lastName = "Sagan", firstName = "Carl", middleName = Some("Edward"),
    yearOfBirth = Some(1934))
  val author2 = Author(lastName ="Odersky", firstName = "Martin", nationality = Some("DE"), yearOfBirth = Some(1958))
  val author3 = Author(lastName = "Spoon", firstName = "Lex")
  val author4 = Author(lastName = "Venners", firstName = "Bill")
  AuthorDAO.insert(author1, author2, author3, author4)


  val book1 = Book(title = "The Demon-Haunted World: Science as a Candle in the Dark")
  val book2 = Book(title = "Cosmos")
  val book3 = Book(title = "Programming in Scala")
  BookDAO.insert(book1, book2, book3)

  val bookAuthor1 = BookAuthor(bookId = book1.id, authorId = author1.id)
  val bookAuthor2 = BookAuthor(bookId = book2.id, authorId = author1.id)
  val bookAuthor3 = BookAuthor(bookId = book3.id, authorId = author2.id)
  val bookAuthor4 = BookAuthor(bookId = book3.id, authorId = author3.id)
  val bookAuthor5 = BookAuthor(bookId = book3.id, authorId = author4.id)
  AuthorDAO.bookAuthor.insert(bookAuthor1, bookAuthor2, bookAuthor3, bookAuthor4, bookAuthor5)
}