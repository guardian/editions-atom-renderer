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
  "com.gu" %% "atom-renderer" % "1.0.3",
  "com.gu" %% "content-api-client-default" % "14.1",
  "com.amazonaws" % "aws-lambda-java-core" % "1.2.0",
  "io.circe" %% "circe-core" % "0.11.1",
  "io.circe" %% "circe-generic" % "0.11.1",
  "io.circe" %% "circe-parser" % "0.11.1"
)

enablePlugins(RiffRaffArtifact)

assemblyJarName := s"${name.value}.jar"
riffRaffPackageType := assembly.value
riffRaffUploadArtifactBucket := Option("riffraff-artifact")
riffRaffUploadManifestBucket := Option("riffraff-builds")
riffRaffArtifactResources += (file("cfn.yaml"), s"${name.value}-cfn/cfn.yaml")

assemblyMergeStrategy in assembly := {
  case f if f.endsWith(".thrift") => MergeStrategy.discard
  case f =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(f)
}