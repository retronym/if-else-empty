# if-else-empty

## Exploring ways to avoid undesirable LUBs

Migrating from the Scala 2.12 to the 2.13 collections API reveals a
source incompatible change that can be tricky to detect.

```
Welcome to Scala 2.12.15 (OpenJDK 64-Bit Server VM, Java 11.0.16-beta).
Type in expressions for evaluation. Or try :help.

scala> def foo(c: Boolean) = if (c) Map(1 -> "one") else Map(); Map(2 -> "two") ++ foo(true)
foo: (c: Boolean)scala.collection.immutable.Map[_ <: Int, String]
res0: scala.collection.immutable.Map[Int,String] = Map(2 -> two, 1 -> one)

scala> def foo(c: Boolean) = if (c) Map(1 -> "one") else Map(); foo(true) ++ Map(2 -> "two")
foo: (c: Boolean)scala.collection.immutable.Map[_ <: Int, String]
res1: scala.collection.immutable.Map[Int,String] = Map(1 -> one, 2 -> two)
```

```
Welcome to Scala 2.13.8 (OpenJDK 64-Bit Server VM, Java 11.0.16-beta).
Type in expressions for evaluation. Or try :help.

scala> def foo(c: Boolean) = if (c) Map(1 -> "one") else Map(); Map(2 -> "two") ++ foo(true)
def foo(c: Boolean): scala.collection.immutable.Map[_ <: Int, String]
val res0: scala.collection.immutable.Map[Int,String] = Map(2 -> two, 1 -> one)

scala> def foo(c: Boolean) = if (c) Map(1 -> "one") else Map(); foo(true) ++ Map(2 -> "two")
def foo(c: Boolean): scala.collection.immutable.Map[_ <: Int, String]
val res1: scala.collection.immutable.Iterable[(Int, String)] = List((1,one), (2,two))
```

`Map.++` used to compute the Least Upper Bound (LUB) of the key types of the receiver and argument.
Now, if the argument's key type does not conform the receiver's, a `List` is created.

## Lint for problematic `Map.++` calls

We can statically analyze the typed ASTs (under the 2.12 compiler) to find calls the `Map.++`
which require the type-slack in 2.12 and which will be widened to `Iterable` in 2.13.

This can then pinpoint places where manual edits are required to align the types.

## Finding so-close-yet-so-far LUBs

What is the inferred type of these expressions?

```scala
if (cond) Set[String]("") else Set()
if (cond) Map[String, String]("" -> "") else Map.empty()
```

Intuition suggest `Set[String]` or `Map[String, String]`, but in this doesn't follow from the
process of type checking.

In the absence of an expected type (pt) for the expression, the type checker will compute the type of
each branch, and LUB these to compute the type of the `If` expression. The `else` branches abovce
are typed `Set[Nothing]`, `Map[Nothing, Nothing]` respectively, so the resulting LUBs are existentials
`Set[_ <: String]`, `Map[_ <: String, String]` resp. This is related to the invariant type parameters
of `Set` element types and of `Map` key types.

Further static analysis can also find such cases, where a branched control flow expression has an
existential type computed by a LUB. This often leads to the root cause of the undesirable types.

## LUB-less ifs (Act I)

This repo includes an demonstration of using a DSL to replace the build in `if` expression that
a) restricts the type of the result to the type of the first branch and b) has a convenience
method to create the else branch that returns an empty collection of that type.

```scala
val s1 = If(false).Then(HashSet(1)).Else(HashSet(2))

val m1 = If(false).Then(Map(1 -> "one")).ElseEmpty
```

## LUB-less ifs (Act II)

Could we do the same without needing to discard native control flow?

One idea would be to create a dummy method:

```scala
@compileTimeOnly("must be eliminated by a compiler plugin")
def emptyCollection: Nothing = ???
```

The user could write code:
```scala
if (cond) Set("") else emptyCollection
```

This would typecheck as `LUB(Set[String], Nothing) = Set[String]`

A subsequent compiler plugin would then replace the `emptyCollection` call with a call the empty factory method.

```scala
(if (cond) Set("") else Set.empty[String])
```