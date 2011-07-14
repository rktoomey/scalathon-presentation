import sbt._

class SampleProject(info: ProjectInfo) extends DefaultProject(info) {
    
  override def compileOptions = super.compileOptions ++ Seq(Unchecked, Deprecation)

  val salat = "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
  val configgy = "net.lag" % "configgy" % "2.0.2-nologgy"     
  val specs2 = "org.specs2" %% "specs2" % "1.4" % "test" 
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.0" % "test->default"

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  val snapshots = "snapshots" at "http://scala-tools.org/repo-snapshots"
  val releases  = "releases" at "http://scala-tools.org/repo-releases"
  val novusRels = "repo.novus rels" at "http://repo.novus.com/releases/"
  val novusSnaps = "repo.novus snaps" at "http://repo.novus.com/snapshots/"
}

