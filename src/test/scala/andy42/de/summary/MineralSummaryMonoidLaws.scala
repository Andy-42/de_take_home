package andy42.de.summary

import andy42.de.{Chromium, Gold, MineralSummary, Titanium}
import cats.kernel.laws.discipline.MonoidTests
import org.scalacheck.Test.Parameters
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.funspec.AnyFunSpec

class MineralSummaryMonoidLaws extends AnyFunSpec {

  // Generate MineralSummary values
  // TODO: Have variants for 0..3 keys - this generator only has the 1 case
  implicit val randomMineralSummary: Arbitrary[MineralSummary] =
  Arbitrary(
    for {
      mineral <- Gen.oneOf(Gold, Chromium, Titanium)
      quantity <- Gen.chooseNum(minT = 1, maxT = 1000000, specials = 1)
    } yield MineralSummary(mineral, quantity)
  )

  import MineralSummary.mineralSummaryMonoid

  MonoidTests[MineralSummary].monoid.all.check(Parameters.default)
}
