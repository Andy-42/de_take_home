package andy42.de.decode

import andy42.de.summary.MineralSummary
import andy42.de._
import zio._

import java.util.Locale
import scala.util.Try

/**
 * Decode a CSV row to either a DecodeFailure or MineralSummary.
 */
trait RowDecoder {
  def decodeRowToInventory(row: String): IO[DecodeFailure, MineralSummary]
}

case class RowDecoderLive() extends RowDecoder {

  override def decodeRowToInventory(row: String): IO[DecodeFailure, MineralSummary] =
    for {
      qt <- splitRow(row) // TODO: Why does tuple pattern not work here?
      quantity <- quantityFromText(qt._1)
      mineral <- mineralFromText(qt._2)
    } yield MineralSummary(mineral, quantity)

  /**
   * This is trivial CSV parsing and assumes that the values in the file are not quoted.
   *
   * @param row A row from a CSV file.
   * @return A tuple of the text of the Quantity and Mineral columns.
   */
  def splitRow(row: String): IO[DecodeFailure, (String, String)] =
    row.split(",") match {
      case columns if columns.length == 6 => ZIO.succeed((columns(3), columns(4)))
      case columns => ZIO.fail(UnexpectedRowLength(row, actualLength = columns.length, expectedLength = 6))
    }

  def quantityFromText(quantityText: String): IO[DecodeFailure, Quantity] =
    for {
      n <- ZIO.fromTry(Try(quantityText.trim.toLong))
        .orElse(ZIO.fail(QuantityParseFailure(quantityText)))

      quantity <- if (n > 0) ZIO.succeed(n) else ZIO.fail(QuantityRangeFailure(n))
    } yield quantity

  def mineralFromText(mineralText: String): IO[DecodeFailure, Mineral] =
    mineralText.trim.toLowerCase(Locale.ENGLISH) match {
      case "chromium" => ZIO.succeed(Chromium)
      case "gold" => ZIO.succeed(Gold)
      case "titanium" => ZIO.succeed(Titanium)

      case "" => ZIO.fail(EmptyMineralString)
      case _ => ZIO.fail(UnmatchedMineralString(mineralText))
    }
}

object RowDecoderLive {
  val layer: ULayer[Has[RowDecoder]] = (RowDecoderLive.apply _).toLayer
}

object RowDecoder {
  def decodeRowToMineralSummary(row: String): ZIO[Has[RowDecoder], DecodeFailure, MineralSummary] =
    ZIO.serviceWith[RowDecoder](_.decodeRowToInventory(row))
}
