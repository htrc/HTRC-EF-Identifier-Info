package utils

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

import scala.collection.JavaConverters._

object MarkdownHelper {
  private val extensions = List(
    TablesExtension.create(),
    StrikethroughExtension.create(),
    HeadingAnchorExtension.create(),
    InsExtension.create()
  ).asJava

  val parser: Parser = Parser.builder().extensions(extensions).build()
  val renderer: HtmlRenderer = HtmlRenderer.builder().extensions(extensions).build()
}
