name := "editions-atom-renderer"

organization := "com.gu"

description := "renders atoms for editions"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-dead-code"
)

libraryDependencies ++= Seq(
  "com.gu" % "atom-renderer" %% "1.0.3",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
  "com.amazonaws" % "aws-lambda-java-log4j2" % "1.1.0",
  "org.slf4j" % "slf4j-api" % "2.11.2",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "1.8.0-beta4"
)

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")
