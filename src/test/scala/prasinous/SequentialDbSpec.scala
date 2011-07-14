package prasinous

import com.mongodb.casbah.commons.Logging
import org.specs2.mutable._
import org.specs2.specification._
trait SequentialDbSpec extends Specification with Logging {

  override def is =
    Step {
//      log.info("beforeSpec: registering BSON conversion helpers")
      com.mongodb.casbah.commons.conversions.scala.RegisterConversionHelpers()
      com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()

    } ^
      super.is

}