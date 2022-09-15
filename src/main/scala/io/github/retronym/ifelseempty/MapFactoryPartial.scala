package io.github.retronym.ifelseempty

import scala.collection.MapFactory

trait MapFactoryPartial {
  implicit class MapFactoryHasPartiallyApplied[M[_, _]](private val f: MapFactory[M]) {
    def keyType[K] = new MapFactoryPartiallyAppliedK[M, K](f)
  }
  final class MapFactoryPartiallyAppliedK[M[_, _], K](private val f: MapFactory[M]) {
    def apply[V](): M[K, V] = f.empty[K, V]
    def empty[V]: M[K, V] = f.empty[K, V]
  }
}

object MapFactoryPartial extends MapFactoryPartial