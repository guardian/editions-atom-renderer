package com.gu.editionsatomrenderer

import com.gu.contentapi.client.{ContentApiClient, GuardianContentClient}

import scala.concurrent.ExecutionContext.Implicits.global
import com.amazonaws.services.lambda.runtime.Context
import com.gu.contentatom.renderer.ArticleConfiguration.CommonsdivisionConfiguration
import com.gu.contentatom.renderer.{DefaultAtomRenderer}
import com.gu.contentatom.thrift.AtomType
import scala.concurrent.duration._

import scala.concurrent.Await

import io.circe.syntax._
import io.circe.generic.auto._

/**
  * This is compatible with aws' lambda JSON to POJO conversion.
  * You can test your lambda by sending it the following payload:
  * {"name": "Bob"}
  */
class LambdaInput() {
  var atomType: String = _
  var id: String = _

  def getAtomType(): String = atomType
  def setAtomType(thetype: String): Unit = atomType = thetype

  def getId(): String = atomType
  def setId(theid: String): Unit = id = theid
}

case class AtomPath(atomType: String, atomId: String)

case class RenderedAtom(html: String, css: Seq[String], js: Seq[String])
case class Env(app: String, stack: String, stage: String, capi: String) {
  override def toString: String = s"App: $app, Stack: $stack, Stage: $stage\n"
}

object Env {
  def apply(): Env = Env(
    Option(System.getenv("App")).getOrElse("DEV"),
    Option(System.getenv("Stack")).getOrElse("DEV"),
    Option(System.getenv("Stage")).getOrElse("DEV"),
    Option(System.getenv("CAPIKEY")).get
  )
}

object Lambda {

  def handler(lambdaInput: LambdaInput, context: Context): String = {

    val path = AtomPath(lambdaInput.atomType, lambdaInput.id)
    val env = Env()
    val resp = process(path, env)
    resp.asJson.toString

  }
  def process(path: AtomPath, env: Env): Option[RenderedAtom] = {
    val client = new GuardianContentClient(env.capi)

    val atomType =
      AtomType.list.find(_.name.toUpperCase == path.atomType.toUpperCase)

    val search = ContentApiClient
      .item(s"atom/${path.atomType}/${path.atomId}")
    // .showAtoms("all")

    println(search.toString())
    println(search.pathSegment)

    val response = client.getResponse(search)

    val resp = Await.result(response, 20 seconds)
    println("Response received")
    println(resp.status)

    val all = List(
      resp.audio,
      resp.chart,
      resp.commonsdivision,
      resp.cta,
      resp.explainer,
      resp.guide,
      resp.interactive,
      resp.media,
      resp.profile,
      resp.qanda,
      resp.quiz,
      resp.recipe,
      resp.review,
      resp.storyquestions,
      resp.timeline
    )

    val maybeAtom = all.flatten.headOption
    println(maybeAtom)

    maybeAtom.map { atom =>
      RenderedAtom(
        html = DefaultAtomRenderer.getHTML(atom),
        css = DefaultAtomRenderer.getCSS(Seq(atomType.get)),
        js = DefaultAtomRenderer.getJS(Seq(atomType.get))
      )
    }
  }
}

object TestIt {
  def main(args: Array[String]): Unit = {
    val atomType = args(0)
    val atomId = args(1)
    val path = AtomPath(atomType, atomId)

    println(Lambda.process(path, Env()))

  }

}
