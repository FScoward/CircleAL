import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class TestSpec extends Specification {
	/*
	"HOME表示" in {
		val Some(result) = routeAndCall(FakeRequest(GET, "/login/FScoward"))

		status(result) must equalTo(OK)
		contentType(result) must beSome("text/html")
		charset(result) must beSome("utf-8")
		
	}
	*/


	"test" in {
		running(FakeApplication()){
			val result = controllers.Application.index()(FakeRequest())
			status(result) must equalTo(OK)
			contentType(result) must beSome("text/html")
			charset(result) must beSome("utf-8")
		}
		
	}
	
}