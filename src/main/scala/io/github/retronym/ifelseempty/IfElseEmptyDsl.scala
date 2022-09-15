package io.github.retronym.ifelseempty

import scala.collection.Factory

// TODO remove by-name params and instead implement with an inlining macros to avoid overhead
trait IfElseEmptyDsl {
  object If {
    def apply(cond: Boolean): If = new If(cond)

    final class Then[CC[_], A](cond: Boolean, thenp: => CC[A])(implicit factory: Factory[A, CC[A]]) {
      def ElseEmpty: CC[A] = if (cond) thenp else factory.newBuilder.result()

      def Else(elsep: => CC[A]): CC[A] = if (cond) thenp else elsep
    }

    final class Then2[CC[_, _], A, B](cond: Boolean, thenp: => CC[A, B])(implicit factory: Factory[(A, B), CC[A, B]]) {
      def ElseEmpty: CC[A, B] = if (cond) thenp else factory.newBuilder.result()

      def Else(elsep: => CC[A, B]): CC[A, B] = if (cond) thenp else elsep
    }
  }
  final class If(cond: Boolean) {
    def Then[CC[E] <: collection.IterableOnce[E], A](thenp: CC[A])(implicit factory: Factory[A, CC[A]]): If.Then[CC, A] = new If.Then[CC, A](cond, thenp)
    def Then[CC[K, V] <: collection.Map[K, V], A, B](thenp: CC[A, B])(implicit factory: Factory[(A, B), CC[A, B]]): If.Then2[CC, A, B] = new If.Then2[CC, A, B](cond, thenp)
  }
}

object IfElseEmptyDsl extends IfElseEmptyDsl
