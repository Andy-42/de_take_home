package andy42.de.summary

import andy42.de.{Mineral, MineralSummary, Quantity}
import cats.Monoid

/**
 * Describes the total quantity by mineral type.
 * Implements the `empty` and `combine` methods that implement a monoid over `MineralSummary`
 * that can be used to calculate total quantity.
 */
object MineralSummary {

  /**
   * An instance constructor to create a summary for a single Mineral.
   *
   * Since we require that `MineralSummary` can be compared for equality, the domain of quantity must be positive.
   * This is because the implementation of `Map.equals` would not consider a missing key to be equivalent to a key
   * with the value 0.
   */
  def apply(mineral: Mineral, quantity: Quantity): MineralSummary = {
    require(quantity > 0)
    Map(mineral -> quantity)
  }

  implicit def mineralSummaryMonoid: Monoid[MineralSummary] =
    new Monoid[MineralSummary] {

      override val empty: MineralSummary = Map.empty

      override def combine(a: MineralSummary, b: MineralSummary): MineralSummary =
        a ++ b.map { case (mineral, bQuantity) =>
          mineral -> a.get(mineral).fold(bQuantity)(aQuantity => aQuantity + bQuantity)
        }
    }

  def formatAsCSV(mineralSummary: MineralSummary): String =
    "Mineral,Quantity\n" +
      mineralSummary
        .toIndexedSeq.map { case (m: Mineral, q: Quantity) => s"$m,$q\n" }
        .sorted
        .mkString
}
