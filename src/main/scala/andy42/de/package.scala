package andy42

package object de {

  /**
   * The quantity of minerals, as in a Mineral Summary.
   * See `MineralSummary` for the reasoning why this is constrained to be positive.
   */
  type Quantity = Long // Must be positive, TODO: Refine type

  /**
   * Describes a total quantity by mineral.
   */
  type MineralSummary = Map[Mineral, Quantity]
}
