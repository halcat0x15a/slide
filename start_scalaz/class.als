abstract sig Class {
  super : set Class
}

one sig Show, Semigroup, Equal, Plus, Functor, Foldable, ArrId, Compose extends Class {} {
  super = none
}

one sig Monoid extends Class {} {
  super = Semigroup
}

one sig Group extends Class {} {
  super = Monoid
}

one sig Order extends Class {} {
  super = Equal
}

one sig Enum extends Class {} {
  super = Order
}

one sig PlusEmpty extends Class {} {
  super = Plus
}

one sig Pointed, Apply extends Class {} {
  super = Functor
}

one sig Bind extends Class {} {
  super = Apply
}

one sig Applicative extends Class {} {
  super = Apply + Pointed
}

one sig Monad extends Class {} {
  super = Applicative + Bind
}

one sig ApplicativePlus extends Class {} {
  super = Applicative + PlusEmpty
}

one sig MonadPlus extends Class {} {
  super = Monad + ApplicativePlus
}

one sig Traverse extends Class {} {
  super = Functor + Foldable
}

one sig Category extends Class {} {
  super = ArrId + Compose
}

one sig Arrow, Choice, Split extends Class {} {
  super = Category
}

run {} for 1
