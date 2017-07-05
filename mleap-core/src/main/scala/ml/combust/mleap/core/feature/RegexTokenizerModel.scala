package ml.combust.mleap.core.feature

import ml.combust.mleap.core.Model

import scala.util.matching.Regex

case class RegexTokenizerModel(regex: Regex, matchGaps: Boolean = true, tokenMinLength: Int = 1, lowercaseText: Boolean = true) extends Model {

  def apply(raw: String): Seq[String] = {
    val text = if (lowercaseText) raw.toLowerCase else raw
    val tokens = if (matchGaps) regex.split(text).toSeq else regex.findAllIn(text).toSeq

    tokens.filter(_.length >= tokenMinLength)
  }
}