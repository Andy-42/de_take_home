package andy42.de.decode

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

/**
 * The `DecodeFailure` trait describes all the possible failures in decoding a row (from expeditions.csv).
 * Unifying all the failure to a single type allows the failures to be handled in a uniform way, which
 * in this case is that a JSON representation can be generated for structured logging.
 */
sealed trait DecodeFailure extends Throwable {
  self =>

  def location: String

  def message: String

  def asJsonString: String = self match {

    case f@EmptyMineralString => f.asJson.noSpaces
    case f@UnmatchedMineralString(_) => f.asJson.noSpaces
    case f@UnexpectedRowLength(_, _, _) => f.asJson.noSpaces
    case f@QuantityParseFailure(_) => f.asJson.noSpaces
    case f@QuantityRangeFailure(_) => f.asJson.noSpaces
  }
}

case object EmptyMineralString extends DecodeFailure {
  override val location = "Mineral field"
  override val message = "Must not be empty"
}

case class UnmatchedMineralString(s: String) extends DecodeFailure {
  override val location = "Mineral field"
  override val message = "Must match a known mineral"
}


case class UnexpectedRowLength(row: String, actualLength: Int, expectedLength: Int) extends DecodeFailure {
  override val location: String = "csv row"
  override val message: String = "Wrong row length"
}

case class QuantityParseFailure(text: String) extends DecodeFailure {
  override val location: String = "Quantity field"
  override val message: String = "Quantity text is not a valid long integer"
}

case class QuantityRangeFailure(value: Long) extends DecodeFailure {
  override val location: String = "Quantity field"
  override val message: String = "Quantity value was not positive"
}
