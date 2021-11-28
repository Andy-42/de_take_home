package andy42.de.decode

import andy42.de.Gold
import andy42.de.summary.MineralSummary
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

object RowDecoderSpec extends DefaultRunnableSpec {

  def spec: Spec[TestEnvironment, TestFailure[DecodeFailure], TestSuccess] =
    suite("RowDecoder")(

      testM("succeeds decoding a valid row")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,2,Gold,3")
          expected = MineralSummary(Gold, 2L)
        } yield assert(r)(equalTo(expected))
      ),

      testM("succeeds decoding a valid row with extra whitespace and casing variation")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B    ,1,2 , goLD ,3")
          expected = MineralSummary(Gold, 2L)
        } yield assert(r)(equalTo(expected))
      ),

      testM("fails if there are too many elements")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,2,Gold,3,xxx").run
          expected = UnexpectedRowLength(row = "2,B,1,2,Gold,3,xxx", actualLength = 7, expectedLength = 6)
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if there are too few elements")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,2,Gold").run
          expected = UnexpectedRowLength(row = "2,B,1,2,Gold", actualLength = 5, expectedLength = 6)
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if quantity cannot be parsed as a Long")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,xxxnotalong,Gold,3").run
          expected = QuantityParseFailure(text = "xxxnotalong")
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if quantity is negative")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,-1,Gold,3").run
          expected = QuantityRangeFailure(value = -1)
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if quantity is 0")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,0,Gold,3").run
          expected = QuantityRangeFailure(value = 0)
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if quantity is 0")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,0,Gold,3").run
          expected = QuantityRangeFailure(value = 0)
        } yield assert(r)(fails(equalTo(expected)))
      ),

      testM("fails if mineral text is empty")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,2,,3").run
        } yield assert(r)(fails(equalTo(EmptyMineralString)))
      ),

      testM("fails if mineral text is not matched")(
        for {
          r <- RowDecoder.decodeRowToMineralSummary("2,B,1,2,NotAMineral,3").run
        } yield assert(r)(fails(equalTo(UnmatchedMineralString("NotAMineral"))))
      ),

    ).provideLayer(RowDecoderLive.layer)
}
