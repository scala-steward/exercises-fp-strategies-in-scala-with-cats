package exercises.ch2

import cats.implicits._
import weaver._

import Tree._

def buildTreeLeft[A](size: BigInt, fillFromIndex: BigInt => A): Tree[A] =
  @scala.annotation.tailrec
  def buildTreeLeftRecursive(remainingSize: BigInt, accumulator: Tree[A]): Tree[A] = {
    if (remainingSize === 0) accumulator
    else {
      val newTree = Node(accumulator, Leaf(fillFromIndex(remainingSize - 1)))
      buildTreeLeftRecursive(remainingSize - 1, newTree)
    }
  }
  buildTreeLeftRecursive(size - 1, Leaf(fillFromIndex(size - 1)))

object BuildTreeSuite extends SimpleIOSuite {

  loggedTest("buildTreeLeft example") { log =>
    val size                       = BigInt(3)
    val tree: Tree[BigInt]         = buildTreeLeft(size, identity)
    val expectedTree: Tree[BigInt] = Node(Node(Leaf(2), Leaf(1)), Leaf(0))
    for {
      _ <- log.info(tree.toString)
    } yield expect.eql(tree, expectedTree)

  }

}

object TreeSuite extends FunSuite {

  val largeSize = BigInt(1_000_000)
  val largeTree = buildTreeLeft(largeSize, identity)

  test("size (simple)") {
    val tree = Node(Leaf(1), Node(Leaf(2), Leaf(4)))

    expect.eql(tree.size, 3: BigInt)
  }

  test("tree built of size 3") {
    val tree                       = buildTreeLeft(3, identity)
    val expectedTree: Tree[BigInt] = Node(Node(Leaf(2), Leaf(1)), Leaf(0))

    expect.eql(tree.size, 3: BigInt) and expect.eql(tree, expectedTree)
  }

  test("size is stack-safe (supports large trees)") {
    expect.eql(largeTree.size, largeSize)
  }

  test("contains (simple)") {
    val tree = Node(Leaf(1), Node(Leaf(2), Leaf(4)))

    expect(tree.contains(1)) and expect(tree.contains(2)) and expect(!tree.contains(3))
  }

  test("contains is stack-safe (supports large trees) (expected slow)") {
    expect(largeTree.contains(0))
  }

  test("contains is stack-safe (supports large trees) (expected quick)") {
    expect(largeTree.contains(largeSize - 1))
  }

  test("map simple case 1") {
    val intTree    = Node(Leaf(1), Node(Leaf(2), Leaf(4)))
    val stringTree = Node(Leaf("1"), Node(Leaf("2"), Leaf("4")))

    expect.eql(intTree.map(_.toString), stringTree)
  }

  test("map simple case 2") {
    val intTree    = Node(Node(Leaf(1), Leaf(2)), Leaf(4))
    val stringTree = Node(Node(Leaf("1"), Leaf("2")), Leaf("4"))

    expect.eql(intTree.map(_.toString), stringTree)
  }

  test("map: identity, small tree") {
    val size = BigInt(10)
    val tree = buildTreeLeft(size, identity)

    expect.eql(tree.map(identity), tree)
  }

  test("map: identity, large tree") {
    expect.eql(largeTree.map(identity), largeTree)
  }

}
