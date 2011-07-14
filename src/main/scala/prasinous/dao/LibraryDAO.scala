package prasinous.dao

import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import prasinous.model._
import prasinous.{collections => db}
import com.mongodb.casbah.commons.MongoDBList

object AuthorDAO extends SalatDAO[Author, ObjectId](collection = db.author) {

  class BookAuthorCollection(collection: MongoCollection, parentIdField: String)
    extends ChildCollection[BookAuthor, ObjectId](collection, parentIdField)

  val bookAuthor = new BookAuthorCollection(collection = db.bookAuthor, parentIdField = "authorId")

  def idByAuthorName(firstName: String, lastName: String): Option[ObjectId] = {
    // author collection has a unique index on firstName, lastName
    ids(MongoDBObject("firstName" -> firstName, "lastName" -> lastName)).firstOption
  }

  def addBook(a: Author, b: Book) = {
    BookDAO.insert(b)
    bookAuthor.insert(BookAuthor(bookId = b.id, authorId = a.id))
  }

  def booksByAuthor(author: Author): List[Book] = {
    val bookIds = bookAuthor.primitiveProjectionsByParentId[ObjectId](parentId = author.id, field = "bookId")
        BookDAO.find(ref = MongoDBObject("_id" -> MongoDBObject("$in" -> MongoDBList(bookIds: _*))))
          .sort(MongoDBObject("title" -> 1))
          .toList
  }

}

object BookDAO extends SalatDAO[Book, ObjectId](collection = db.book) {

  class BookAuthorCollection(collection: MongoCollection, parentIdField: String)
    extends ChildCollection[BookAuthor, ObjectId](collection, parentIdField)

  class BorrowalCollection(collection: MongoCollection, parentIdField: String)
    extends ChildCollection[Borrowal, ObjectId](collection, parentIdField)

  val bookAuthor = new BookAuthorCollection(collection = db.bookAuthor, parentIdField = "bookId")
  val borrowal = new BorrowalCollection(collection = db.borrowal, parentIdField = "bookId")

  def idByTitle(title: String): Option[ObjectId] = {
    ids(MongoDBObject("title" -> title)).firstOption
  }

  def authorsForBook(book: Book): List[Author] = {
    val bookIds = bookAuthor.primitiveProjectionsByParentId[ObjectId](parentId = book.id, field = "authorId")
    AuthorDAO.find(ref = MongoDBObject("_id" -> MongoDBObject("$in" -> MongoDBList(bookIds: _*))))
      .sort(MongoDBObject("lastName" -> 1))
      .toList
  }

  def borrowalHistoryForBook(book: Book): List[Borrowal] = {
    borrowal.findByParentId(book.id).
      sort(MongoDBObject("scheduledToReturnOn" -> -1)).
      toList
  }
}


