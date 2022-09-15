package io.github.retronym.ifelseempty

import io.github.retronym.ifelseempty.IfElseEmptyDsl._
import org.junit.Assert.assertEquals
import org.junit.Test

import scala.collection.immutable.{HashSet, ListMap}

class IfElseEmptyDslTest {

  private def checkType[T](t: T): Unit = ()

  @Test def testThen1Else(): Unit = {
    val s1 = If(true).Then(Set(1)).Else(Set(2))
    checkType[Set[Int]](s1)
    assertEquals(Set(1), s1)

    val s2 = If(false).Then(HashSet(1)).Else(HashSet(2))
    checkType[HashSet[Int]](s2)
    assertEqualsAndIdenticalClass(HashSet(2), s2)
  }

  @Test def testThen2Else(): Unit = {
    val s1 = If(true).Then(Map(1 -> "one")).Else(Map(2 -> "two"))
    checkType[Map[Int, String]](s1)
    assertEquals(Map(1 -> "one"), s1)

    val s2 = If(false).Then(Map(1 -> "one")).Else(Map(2 -> "two"))
    checkType[Map[Int, String]](s2)
    assertEqualsAndIdenticalClass(Map(2 -> "two"), s2)
  }
  @Test def testThen1ElseEmpty(): Unit = {
    val s1 = If(true).Then(Set(1)).ElseEmpty
    checkType[Set[Int]](s1)
    assertEquals(Set(1), s1)

    val s2 = If(false).Then(HashSet(1)).ElseEmpty
    checkType[HashSet[Int]](s2)
    assertEqualsAndIdenticalClass(HashSet(), s2)
  }

  @Test def testThen2ElseEmpty(): Unit = {
    val s1 = If(true).Then(Map(1 -> "one")).ElseEmpty
    checkType[Map[Int, String]](s1)
    assertEquals(Map(1 -> "one"), s1)

    val s2 = If(false).Then(ListMap(1 -> "one")).ElseEmpty
    checkType[ListMap[Int, String]](s2)
    assertEqualsAndIdenticalClass(ListMap(), s2)
  }

  private def assertEqualsAndIdenticalClass(expected: AnyRef, actual: AnyRef): Unit = {
    assertEquals(expected, actual)
    assertEquals(expected.getClass, actual.getClass)
  }
}
