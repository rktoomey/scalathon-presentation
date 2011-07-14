package prasinous.model

import com.mongodb.casbah.Imports._
import com.novus.salat.annotations._
import org.joda.time.DateTime

//  CREATE TABLE author (
//    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    last_name VARCHAR(50) NOT NULL,
//    first_name VARCHAR(50) NOT NULL,
//    middle_name VARCHAR(50) NULL,
//    nationality VARCHAR(100),
//    year_of_birth VARCHAR(4),
//  );
case class Author(@Key("_id") id: ObjectId = new ObjectId,
                  lastName: String,
                  firstName: String,
                  middleName: Option[String] = None,
                  nationality: Option[String] = None,
                  yearOfBirth: Option[Int] = None)  {

  // contrived example of using @Persist to allow a value outside the case class constructor to be
  /// serialized when an instance of Author is changed into a DBObject
  @Persist lazy val displayName = "%s %s".format(firstName, lastName)

}


//  CREATE TABLE bookauthor (
//    book_id BIGINT NOT NULL,
//    author_id BIGINT NOT NULL,
//
//    PRIMARY KEY (book_id, author_id),
//    FOREIGN KEY (author_id) REFERENCES author(id),
//    FOREIGN KEY (book_id) REFERENCES book(id)
//  );


case class BookAuthor(@Key("_id") id: ObjectId = new ObjectId,
                      bookId: ObjectId,
                      authorId: ObjectId)

//  CREATE TABLE book (
//    id BIGINT AUTO_INCREMENT PRIMARY KEY,
//    title VARCHAR(100) NOT NULL
//  );

case class Book(@Key("_id") id: ObjectId = new ObjectId,
                title: String)

//  CREATE TABLE borrower (
//    id INTEGER PRIMARY KEY,
//    phone_num VARCHAR(20) NOT NULL,
//    address TEXT NOT NULL
//  );

case class Borrower(@Key("_id") id: ObjectId = new ObjectId,
                     phoneNum: String,
                     address: String)

//  CREATE TABLE borrowal (
//    id INTEGER PRIMARY KEY,
//    book_id INTEGER NOT NULL,
//    borrower_id INTEGER NOT NULL,
//    scheduled_to_return_on DATE NOT NULL,
//    returned_on TIMESTAMP,
//    num_nonreturn_phonecalls INT,
//
//    FOREIGN KEY (book_id) REFERENCES book(id),
//    FOREIGN KEY (borrower_id) REFERENCES borrower(id)
//  );

case class Borrowal(@Key("_id") id: ObjectId = new ObjectId,
                    bookId: ObjectId,
                    borrowerId: ObjectId,
                    scheduledToReturnOn: DateTime,
                    returnedOn: Option[DateTime] = None,
                    numNonReturnPhoneCalls: Int = 0)  {

  def phoneCallNotReturned = copy(numNonReturnPhoneCalls = numNonReturnPhoneCalls + 1)

}