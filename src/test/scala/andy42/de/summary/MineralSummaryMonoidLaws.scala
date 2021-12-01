package andy42.de.summary

import andy42.de.{Chromium, Gold, Mineral, MineralSummary, Quantity, Titanium}
import cats.kernel.laws.discipline.MonoidTests
import org.scalacheck.Test.Parameters
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funspec.AnyFunSpec

class MineralSummaryMonoidLaws extends AnyFunSpec {

  val genMineral: Gen[Mineral] = Gen.oneOf(Gold, Chromium, Titanium)
  val genQuantity: Gen[Int] = Gen.chooseNum(1, 1000000)

  val genMineralQuantity: Gen[(Mineral, Quantity)] =
    for {
    mineral <- genMineral
    quantity <- genQuantity
  } yield mineral -> quantity.asInstanceOf[Quantity]

   val genMineralSummary: Gen[MineralSummary] =
    for {
      n <- Gen.chooseNum(0, 3)
      pairs <- Gen.listOfN(n, genMineralQuantity)
  } yield MineralSummary(pairs)

  implicit val randomMineralSummary: Arbitrary[MineralSummary] = Arbitrary(genMineralSummary)

  import MineralSummary.mineralSummaryMonoid

  MonoidTests[MineralSummary].monoid.all.check(Parameters.default)
}
