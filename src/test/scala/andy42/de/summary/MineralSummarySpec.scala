package andy42.de.summary

import andy42.de.{Chromium, Gold, Titanium}
import org.scalatest.flatspec._
import org.scalatest.matchers.should

class MineralSummarySpec extends AnyFlatSpec with should.Matchers {

  "MineralSummary.apply" should "require a positive quantity" in {
    intercept[IllegalArgumentException] {
      MineralSummary.apply(Chromium, 0)
    }
  }

  "MineralSummary.formatAsCSV" should "produce the expected CSV text" in {

    // formatAsCSV sorts on Mineral, so this test works reliably.

    MineralSummary.formatAsCSV(
      Map(
        Titanium -> 9L,
        Gold -> 3L,
        Chromium -> 12L)
    ) shouldBe "Mineral,Quantity\nChromium,12\nGold,3\nTitanium,9\n"
  }

  it should "handle the empty case" in {
    MineralSummary.formatAsCSV(Map.empty) shouldBe "Mineral,Quantity\n"
  }
}
