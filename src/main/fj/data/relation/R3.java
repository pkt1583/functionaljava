package fj.data.relation;

import fj.*;
import static fj.Function.compose2;
import fj.data.Set;
import fj.data.TreeMap;
import static fj.data.relation.R1.r1$;
import static fj.data.relation.R2.r2;
import fj.pre.Monoid;
import fj.pre.Ord;
import fj.pre.Semigroup;

import java.util.Iterator;

/**
 * A position-indexed ternary relation, or a tri-directional map.
 * Represents the extension of a ternary predicate.
 */
public final class R3<A, B, C> implements Iterable<P3<A, B, C>> {

  private final Set<P3<A, B, C>> body;
  private final TreeMap<A, R2<B, C>> map1;
  private final TreeMap<B, R2<A, C>> map2;
  private final TreeMap<C, R2<A, B>> map3;
  private final Ord<A> orda;
  private final Ord<B> ordb;
  private final Ord<C> ordc;

  private R3(final Set<P3<A, B, C>> s, final Ord<A> oa, final Ord<B> ob, final Ord<C> oc) {
    body = s;
    orda = oa;
    ordb = ob;
    ordc = oc;
    TreeMap<A, R2<B, C>> map1 = TreeMap.empty(orda);
    TreeMap<B, R2<A, C>> map2 = TreeMap.empty(ordb);
    TreeMap<C, R2<A, B>> map3 = TreeMap.empty(ordc);

    for (final P3<A, B, C> e : body) {
      map1 = map1.set(e._1(), map1.get(e._1()).orSome(r2(Set.single(ordbc(), P.<B, C>p(e._2(), e._3())), ordb, ordc)));
      map2 = map2.set(e._2(), map2.get(e._2()).orSome(r2(Set.single(ordac(), P.<A, C>p(e._1(), e._3())), orda, ordc)));
      map3 = map3.set(e._3(), map3.get(e._3()).orSome(r2(Set.single(ordab(), P.<A, B>p(e._1(), e._2())), orda, ordb)));
    }

    this.map1 = map1;
    this.map2 = map2;
    this.map3 = map3;
  }

  private R3<A, B, C> r3(final Set<P3<A, B, C>> s) {
    return r3(s, orda, ordb, ordc);
  }

  private Ord<P2<A, B>> ordab() {
    return Ord.p2Ord(orda, ordb);
  }

  private Ord<P2<A, C>> ordac() {
    return Ord.p2Ord(orda, ordc);
  }

  private Ord<P2<B, C>> ordbc() {
    return Ord.p2Ord(ordb, ordc);
  }

  /**
   * Constructs a new relation of arity-3, given its body.
   *
   * @param s  A set of tuples that compose the body of the relation.
   * @param oa An order for the relation's first attribute.
   * @param ob An order for the relation's second attribute.
   * @param oc An order for the relation's third attribute.
   * @return A new relation of arity-3, with the given body.
   */
  public static <A, B, C> R3<A, B, C> r3(final Set<P3<A, B, C>> s, final Ord<A> oa, final Ord<B> ob, final Ord<C> oc) {
    return new R3<A, B, C>(s, oa, ob, oc);
  }

  /**
   * Projects this relation over its first attribute.
   *
   * @return A new relation of arity-1, over the first attribute of this relation.
   */
  public R1<A> project1() {
    return r1$(body.map(orda, P3.<A, B, C>__1()));
  }

  /**
   * Projects this relation over its second attribute.
   *
   * @return A new relation of arity-1, over the second attribute of this relation.
   */
  public R1<B> project2() {
    return r1$(body.map(ordb, P3.<A, B, C>__2()));
  }

  /**
   * Projects this relation over its third attribute.
   *
   * @return A new relation of arity-1, over the third attribute of this relation.
   */
  public R1<C> project3() {
    return r1$(body.map(ordc, P3.<A, B, C>__3()));
  }

  /**
   * Projects this relation over its second and third attributes.
   *
   * @return A new relation of arity-2, over all but the first attribute of this relation.
   */
  public R2<B, C> projectBut1() {
    return r2(Set.iterableSet(ordbc(), R2.r2Monoid(ordb, ordc).sumLeft(map1.values())), ordb, ordc);
  }

  /**
   * Projects this relation over its first and third attributes.
   *
   * @return A new relation of arity-2, over all but the second attribute of this relation.
   */
  public R2<A, C> projectBut2() {
    return r2(Set.iterableSet(ordac(), R2.r2Monoid(orda, ordc).sumLeft(map2.values())), orda, ordc);
  }

  /**
   * Projects this relation over its first and second attributes.
   *
   * @return A new relation of arity-2, over all but the third attribute of this relation.
   */
  public R2<A, B> projectBut3() {
    return r2(Set.iterableSet(ordab(), R2.r2Monoid(orda, ordb).sumLeft(map3.values())), orda, ordb);
  }

  /**
   * Selects the tuples whose first attribute is equal to the given value.
   *
   * @param a The value by which to select.
   * @return A new relation consisting only of the tuples whose first attribute is equal to the given value.
   */
  public R3<A, B, C> selectBy1(final A a) {
    return r3(map1.get(a).orSome(R2.empty(ordb, ordc)).toSet().map(
        body.ord(), new F<P2<B, C>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<B, C> p) {
            return P.p(a, p._1(), p._2());
          }
        }));
  }

  /**
   * Selects the tuples whose second attribute is equal to the given value.
   *
   * @param b The value by which to select.
   * @return A new relation consisting only of the tuples whose second attribute is equal to the given value.
   */
  public R3<A, B, C> selectBy2(final B b) {
    return r3(map2.get(b).orSome(R2.empty(orda, ordc)).toSet().map(
        body.ord(), new F<P2<A, C>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<A, C> p) {
            return P.p(p._1(), b, p._2());
          }
        }));
  }

  /**
   * Selects the tuples whose third attribute is equal to the given value.
   *
   * @param c The value by which to select.
   * @return A new relation consisting only of the tuples whose third attribute is equal to the given value.
   */
  public R3<A, B, C> selectBy3(final C c) {
    return r3(map3.get(c).orSome(R2.empty(orda, ordb)).toSet().map(
        body.ord(), new F<P2<A, B>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<A, B> p) {
            return P.p(p._1(), p._2(), c);
          }
        }));
  }

  /**
   * The union of this relation and another of the same type.
   *
   * @param r A given relation of the same type as this relation.
   * @return The union of this relation and another of the same type.
   */
  public R3<A, B, C> union(final R3<A, B, C> r) {
    return r3(body.union(r.body));
  }

  /**
   * A first-class union function for relations of arity-3.
   *
   * @return A first-class union function for relations of arity-3.
   */
  public static <A, B, C> F2<R3<A, B, C>, R3<A, B, C>, R3<A, B, C>> union() {
    return new F2<R3<A, B, C>, R3<A, B, C>, R3<A, B, C>>() {
      public R3<A, B, C> f(final R3<A, B, C> r1, final R3<A, B, C> r2) {
        return r1.union(r2);
      }
    };
  }

  /**
   * An empty relation of arity-3.
   *
   * @param oa An ord instance for the first attribute type of the relation.
   * @param ob An ord instance for the second attribute type of the relation.
   * @param oc An ord instance for the third attribute type of the relation.
   * @return An empty relation of arity-3.
   */
  public static <A, B, C> R3<A, B, C> empty(final Ord<A> oa, final Ord<B> ob, final Ord<C> oc) {
    return r3(Set.<P3<A, B, C>>empty(Ord.p3Ord(oa, ob, oc)), oa, ob, oc);
  }

  /**
   * A semigroup for relations of arity-3, with union.
   *
   * @return A semigroup for relations of arity-3, with union.
   */
  public static <A, B, C> Semigroup<R3<A, B, C>> r3Semigroup() {
    return Semigroup.semigroup(R3.<A, B, C>union());
  }

  /**
   * A monoid instance for the union of relations of arity-3.
   *
   * @param oa An ord instance for the first attribute type of the relation.
   * @param ob An ord instance for the second attribute type of the relation.
   * @param oc An ord instance for the third attribute type of the relation.
   * @return A monoid instance for the union of relations of arity-3.
   */
  public static <A, B, C> Monoid<R3<A, B, C>> r3Monoid(final Ord<A> oa, final Ord<B> ob, final Ord<C> oc) {
    return Monoid.monoid(R3.<A, B, C>r3Semigroup(), empty(oa, ob, oc));
  }

  /**
   * Returns the set of tuples that compose the body of this relation.
   *
   * @return the set of tuples that compose the body of this relation.
   */
  public Set<P3<A, B, C>> toSet() {
    return body;
  }

  /**
   * Returns an iterator of this relation's tuples.
   *
   * @return an iterator of this relation's tuples.
   */
  public Iterator<P3<A, B, C>> iterator() {
    return body.iterator();
  }

  /**
   * Returns the boolean function representing the predicate of which this relation is an extension.
   * The function returns true if the arguments correspond to a tuple that is a member of this relation.
   *
   * @return A boolean function that returns true if the arguments correspond to a
   *         tuple that is a member of this relation.
   */
  public F<A, F<B, F<C, Boolean>>> toPredicate() {
    final F<F<C, P3<A, B, C>>, F<C, Boolean>> f =
        Function.<C, P3<A, B, C>, Boolean>compose().f(Set.<P3<A, B, C>>member().f(body));
    return compose2(f, P.<A, B, C>p3());
  }

  /**
   * Inserts the given values, as a tuple, to this relation.
   *
   * @param a The first value of the tuple.
   * @param b The second value of the tuple.
   * @param c The third value of the tuple.
   * @return a new relation with the addition of the given tuple.
   */
  public R3<A, B, C> insert(final A a, final B b, final C c) {
    return union(r3(Set.single(Ord.p3Ord(orda, ordb, ordc), P.p(a, b, c))));
  }

}