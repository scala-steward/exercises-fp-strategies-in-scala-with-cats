package exercises.ch2

import scala.util.control.TailCalls._

import cats.Eq
import cats.Show
import cats.derived._
import cats.implicits._
/*

Ch. 2.2, 2.3

Exercise: Tree
To gain a bit of practice defining algebraic data types, code the following description in Scala
(your choice of version, or do both.).
A Tree with elements of type A is:
• a Leaf with a value of typeA; or
• a Node with a left and right child, which are both Trees with elements of type A.

Let’s get some practice with structural recursion and write some methods for Tree. Implement
• size, which returns the number of values (Leafs) stored in the Tree;
• contains, which returns true if the Tree contains a given element of type A, and false otherwise; and
• map, which creates a Tree[B] given a function A => B
Use whichever you prefer of pattern matching or dynamic dispatch to implement the methods.
 */

object Tree {
  given [A](using eq: Eq[A]): Eq[Tree[A]] with
    def eqv(tree1: Tree[A], tree2: Tree[A]): Boolean =
      def eqvRecursive(t1: Tree[A], t2: Tree[A]): TailRec[Boolean] = (t1, t2) match
        case (Leaf(a), Leaf(b)) => done(a === b)
        case (Node(left1, right1), Node(left2, right2)) =>
          for {
            leftResult  <- tailcall(eqvRecursive(left1, left2))
            rightResult <- tailcall(eqvRecursive(right1, right2))
          } yield leftResult && rightResult
        case _ => done(false)
      eqvRecursive(tree1, tree2).result

}

enum Tree[A] derives Show {
  case Leaf(a: A)
  case Node(left: Tree[A], right: Tree[A])

  def size: BigInt =
    @annotation.tailrec
    def sizeRecursive(sizeAccumulator: BigInt, remaining: List[Tree[A]]): BigInt =
      remaining match
        case Nil => sizeAccumulator
        case head :: tail =>
          head match
            case Leaf(a)           => sizeRecursive(sizeAccumulator + 1, tail)
            case Node(left, right) => sizeRecursive(sizeAccumulator, left :: right :: tail)
    sizeRecursive(0, List(this))

  def contains(x: A)(using eq: Eq[A]): Boolean =
    @annotation.tailrec
    def containsRecursive(remaining: List[Tree[A]]): Boolean =
      remaining match
        case Nil => false
        case head :: tail =>
          head match
            case Leaf(a) if a === x => true
            case Leaf(_)            => containsRecursive(tail)
            case Node(left, right)  => containsRecursive(left :: right :: tail)
    containsRecursive(List(this))

  def map[B](f: A => B): Tree[B] =
    // Note: unable to add @annotation.tailrec, but tests prove its safety
    def mapRecursive(tree: Tree[A]): TailRec[Tree[B]] = tree match
      case Leaf(a) => done(Leaf(f(a)))
      case Node(left, right) =>
        for {
          leftResult  <- tailcall(mapRecursive(left))
          rightResult <- tailcall(mapRecursive(right))
        } yield Node(leftResult, rightResult)
    mapRecursive(this).result

}
