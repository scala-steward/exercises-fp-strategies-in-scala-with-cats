package exercises.ch2

import cats.Eq
import cats.Show
import cats.derived._
import cats.implicits._
/*

Ch. 2.2

Exercise: Tree
To gain a bit of practice defining algebraic data types, code the following description in Scala
(your choice of version, or do both.).
A Tree with elements of type A is:
• a Leaf with a value of typeA; or
• a Node with a left and right child, which are both Trees with elements of type A.
 */

/*
Let’s get some practice with structural recursion and write some methods for Tree. Implement
• size, which returns the number of values (Leafs) stored in the Tree;
• contains, which returns true if the Tree contains a given element of type A, and false otherwise; and
• map, which creates a Tree[B] given a function A => B
Use whichever you prefer of pattern matching or dynamic dispatch to implement the methods.

TODO: make `contains` tail recursive
TODO: make `map` tail recursive
TODO: add more detailed case-based tests
TODO: add prop-based test for size (not sure, because it tests the generated function)
TODO: add case-based test for Functor laws
TODO: add property-based test for Functor laws
 */

enum Tree[A] derives Eq, Show {
  case Leaf(a: A)
  case Node(left: Tree[A], right: Tree[A])

  def size: BigInt =
    @scala.annotation.tailrec
    def sizeRecursive(sizeAccumulator: BigInt, remaining: List[Tree[A]]): BigInt =
      remaining match
        case Nil => sizeAccumulator
        case head :: tail =>
          head match
            case Leaf(a)           => sizeRecursive(sizeAccumulator + 1, tail)
            case Node(left, right) => sizeRecursive(sizeAccumulator, left :: right :: tail)
    sizeRecursive(0, List(this))

  def contains(x: A)(using eq: Eq[A]): Boolean =
    @scala.annotation.tailrec
    def containsRecursive(remaining: List[Tree[A]]): Boolean =
      remaining match
        case Nil => false
        case head :: tail =>
          head match
            case Leaf(a) if a === x => true
            case Leaf(_)            => containsRecursive(tail)
            case Node(left, right)  => containsRecursive(left :: right :: tail)
    containsRecursive(List(this))

  def map[B](f: A => B): Tree[B] = this match {
    case Leaf(x)           => Leaf(f(x))
    case Node(left, right) => Node(left.map(f), right.map(f))
  }

}
