name := "FirstAkka"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++=   Seq( "com.typesafe.akka" %% "akka-actor" % "2.3.2",
                              "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.2",
                              "com.typesafe.akka"          %%  "akka-testkit"             % "2.3.2"          % "test",
                              "com.typesafe.akka"          %% "akka-testkit"              % "2.3.2",
                              "org.scalatest"              % "scalatest_2.10"             % "2.0"             % "test",
                              "org.specs2"                 %% "specs2"                    % "2.2.3"           % "test"
)