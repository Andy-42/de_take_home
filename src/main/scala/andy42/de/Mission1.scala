package andy42.de

import andy42.de.decode.RowDecoder.decodeRowToMineralSummary
import andy42.de.decode._
import andy42.de.summary.MineralSummary.{formatAsCSV, mineralSummaryMonoid}
import cats.syntax.semigroup._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.logging._
import zio.stream.ZStream
import zio.stream.ZTransducer.{splitLines, utf8Decode}
import zio.{ExitCode, Has, URIO, ZEnv, ZIO, ZLayer}

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.StandardOpenOption.{CREATE, TRUNCATE_EXISTING}
import java.nio.file.{Files, Path}

object Mission1 extends zio.App {

  val inputFile: Path = Path.of("data/expeditions.csv")
  val outputFile: Path = Path.of("data/minerals.csv")

  val logging: ZLayer[Console with Clock, Nothing, Logging] = Logging.console(
    logLevel = LogLevel.Info,
    format = LogFormat.ColoredLogFormat()
  ) >>> Logging.withRootLoggerName("mission-1")

  type ProgramDependencies = Has[RowDecoder] with Logging with Blocking
  val applicationLayer = RowDecoderLive.layer ++ logging ++ Blocking.live

  val program: ZIO[ProgramDependencies, Throwable, Unit] =
    ZStream
      // Load the data as a stream of strings (one string is a CSV row)
      .fromFile(inputFile)
      .transduce(utf8Decode >>> splitLines)

      .drop(1) // header

      // Decode each CSV row to a MineralSummary
      // Log and filter out any decode failures
      .mapM {
        decodeRowToMineralSummary(_)
          .foldM(
            decodeFailure => Logging.warn(decodeFailure.asJsonString) *> ZIO.succeed(ZStream.empty),
            mineralSummary => ZIO.succeed(ZStream(mineralSummary))
          )
      }.flatten

      // Aggregate all the MineralSummary instances to a single MineralSummary
      .fold(mineralSummaryMonoid.empty)(_ |+| _)

      // Write the final aggregate MineralSummary to file
      .map { mineralSummary =>
        Files.writeString(outputFile, formatAsCSV(mineralSummary), UTF_8, CREATE, TRUNCATE_EXISTING)
      }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.provideLayer(applicationLayer).exitCode
}
