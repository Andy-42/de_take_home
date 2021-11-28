package andy42.de

sealed trait Mineral


case object Chromium extends Mineral {
  override def toString: String = "Chromium"
}

case object Gold extends Mineral {
  override def toString: String = "Gold"
}

case object Titanium extends Mineral {
  override def toString: String = "Titanium"
}
