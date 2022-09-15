package io.github.retronym.ifelseempty

import io.github.retronym.ifelseempty.MapFactoryPartial._
import org.junit.Test

import scala.collection.immutable.ListMap

class MapFactoryPartialTest {
  @Test def testManual(): Unit = {
    var cond = true
    val x = if (cond) {
      ListMap(1 -> "one")
    } else {
      ListMap[Int, String]()
    }
    checkType[ListMap[Int, String]](x)
  }

  @Test def testPartialApply(): Unit = {
    var cond = true
    val x = if (cond) {
      ListMap(1 -> "one")
    } else {
      ListMap.keyType[Int]()
    }
    checkType[ListMap[Int, String]](x)
  }

  @Test def testPartialEmpty(): Unit = {
    var cond = true
    val x = if (cond) {
      ListMap(1 -> "one")
    } else {
      ListMap.keyType[Int].empty
    }
    checkType[ListMap[Int, String]](x)
  }

  private def checkType[T](t: T): Unit = ()

}
