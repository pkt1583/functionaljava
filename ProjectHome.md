[Functional Java](http://functionaljava.org/) is an open source library for applying Functional Programming concepts in the Java language. It also serves as a platform for learning these concepts by introducing them using a familiar language. The library is intended for use in production applications and is thoroughly tested using the technique of automated specification-based testing with [ScalaCheck](http://code.google.com/p/scalacheck).

## Functional Java source code is now [hosted on github](https://github.com/functionaljava/functionaljava) ##

**[Functional Java website](http://functionaljava.org/)**

**[Functional Java Examples](http://code.google.com/p/functionaljava/source/browse/artifacts/3.0/demo/1.5/)**

**[Functional Java API Specifications](http://functionaljava.googlecode.com/svn/artifacts/3.0/javadoc/index.html)**

**[Functional Java Community](http://functionaljava.org/community)**

**[Download Functional Java](http://functionaljava.org/download)**

**[Functional Java Builds](http://hudson.scala-tools.org/job/functional-java/)**


# Features #

### First-Class Functions ###

Functional Java provides generic interfaces and abstract classes that serve as first-class functions or closures, entirely within Java's type system (i.e. without reflection or byte-code manipulation). The library centers around the `F<A, B>` interface, which models a function from type `A` to type `B`.

Functions are written with anonymous class syntax:

```
// Regular Style
Integer timesTwo(Integer i) {
  return i * 2;
}

// Functional Style
F<Integer, Integer> timesTwo = new F<Integer, Integer>() {
  public Integer f(Integer i) { return i * 2; }
}
```

First-class functions can be composed and passed as arguments to higher-order functions:

```
// Regular Style
Integer timesTwoPlusOne(Integer i) {
  return plusOne(timesTwo(i));
}

// Functional Style
F<Integer, Integer> timesTwoPlusOne = plusOne.o(timesTwo);
```

... mapped over collections ...

```
// Regular Style
List<Integer> oneUp = new ArrayList<Integer>();
for (Integer i: ints)
  oneUp.add(plusOne(i));

// Functional Style
List<Integer> oneUp = ints.map(plusOne);
```

Functions up to arity-8 are supported, allowing elimination of nested control constructs:

```
// Regular Style
Integer product = 1;
for (Integer x: ints)
  product = x * product;
List<Integer> products1 = new ArrayList<Integer>();
for (int x = 0; x < ints.size(); x++) {
  for (int y = 0; y <= x; y++) {
    products.add(ints.get(x) * ints.get(y);
  }
}
List<Integer> products2 = new ArrayList<Integer>();
for (Integer x: ints) {
  for (Integer y: ints) {
    products.add(x * y);
  }
}

// Functional Style
Integer product = ints.foldLeft(1, multiply);
List<Integer> products1 = ints.tails().apply(ints.map(multiply));
List<Integer> products2 = ints.bind(ints, multiply);
```


### Immutable Datastructures ###

Functional Java implements many immutable datastructures, such as
  * Singly-linked lists (fj.data.List)
  * Non-strict, potentially infinite, singly-linked list (fj.data.Stream)
  * Non-strict, potentially infinite Strings (fj.data.LazyString)
  * A wrapper for arrays (fj.data.Array)
  * A wrapper for any Iterable type (fj.data.IterableW)
  * Immutable ordered sets (fj.data.Set)
  * Multi-way trees -- a.k.a. rose trees, with support for infinite trees (fj.data.Tree)
  * Immutable Map, with single-traversal search-and-update (fj.data.TreeMap)
  * Type-safe heterogeneous lists (fj.data.hlist)
  * Pointed lists and trees (fj.data.Zipper and fj.data.TreeZipper)

These datatypes come with many powerful higher-order functions, such as `map` (for functors), `bind` (monads), `apply` and `zipWith` (applicative functors), and `cobind` (for comonads).

Efficient conversions to and from the standard Java Collections classes are provided, and `java.util.Iterable` is implemented where possible, for use with Java's `foreach` syntax.


### Optional Values (type-safe null) ###

The library provides a datatype for variables, parameters, and return values that may have no value, while remaining type-safe.

```
// Using null
String val = map.get(key);
if (val == null || val.equals(""))
  val = "Nothing";
return val;

// Using Option
return fromString(map.get(key)).orSome("Nothing");
```

Optional values are iterable, so they play nicely with `foreach` syntax, and they can be composed in a variety of ways. The `fj.Option` class has a plethora of methods for manipulating optional values, including many higher-order functions.


### Product Types ###

Joint union types (tuples) are products of other types. Products of arities 1-8 are provided (`fj.P1 - fj.P8`). These are useful for when you want to return more than one value from a function, or when you want to accept several values when implementing an interface method that accepts only one argument. They can also be used to get products over other datatypes, such as lists (`zip` function).

Example:

```
// Regular Java
public Integer albuquerqueToLA(Map<String, Map<String, Integer>> map) {
  Map m = map.get("Albuquerque");
  if (m != null)
     return m.get("Los Angeles"); // May return null.
}

// Functional Java with product and option types.
public Option<Integer> albuquerqueToLA(TreeMap<P2<String, String>, Integer>() map) {
  return m.get(p("Albuquerque", "Los Angeles"));
}
```


### Disjoint Union Types ###

By the same token, types can be added by disjoint union. Values of type `Either<A, B>` contain a value of either type `A` or type `B`. This has many uses. As an argument type, it allows a single argument to depend on the type of value that is received (effectively overloading the method even if the interface is not designed to do that). As a return type, it allows you to return a value of one of two types depending on some condition. For example, to provide error handling where you are not allowed to throw Exceptions:

```
// Meanwhile, inside an iterator implementation...
public Either<Fail, Integer> next() {
  String s = moreInput();
  try {
    return Either.right(Integer.valueOf(s));
  } catch (Exception e) {
    return Either.left(Fail.invalidInteger(s));
  }
}
```

The `Either` class includes a lot of useful methods, including higher-order functions for mapping and binding over the left and right types, as well as `Iterable` implementations for both types.

[See here for a more detailed explanation of using `Either` for handling errors.](http://apocalisp.wordpress.com/2008/06/04/throwing-away-throws)


### Higher-Order Concurrency Abstractions ###

Functional Java includes Parallel Strategies (`fj.control.parallel.Strategy`) for effectively decoupling concurrency patterns from algorithms. `Strategy` provides higher-order functions for mapping and binding over collections in parallel:

```
Strategy<Integer> s = simpleThreadStrategy();
List<Integer> ns = range(Integer.MIN_VALUE, Integer.MIN_VALUE + 10).map(negate).toList();
List<Boolean> bs = s.parMap(ns, isPrime);
```

Also included is an implementation of the actor model (`fj.control.parallel.Actor` and `QueueActor`), and `Promise`, which is a composable and non-blocking version of `java.util.concurrent.Future`.

[A series of blog posts on the concurrency features can be found here.](http://apocalisp.wordpress.com/2008/06/30/parallel-list-transformations)


### Lots More... ###

There's a lot here, so check out the [API docs](http://functionaljava.org/api), download [the binary or source distributions](http://functionaljava.org/download), and join [the Functional Java community](http://functionaljava.org/community).