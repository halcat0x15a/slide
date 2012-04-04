abstract class Nat {
    public static final S<_0> _1 = new S<>(_0._0);
    public static final S<S<_0>> _2 = new S<>(_1);
}

final class _0 extends Nat {
    public static final _0 _0 = new _0();
    private _0() {}
}

final class S<N extends Nat> extends Nat {
    public final N n;
    public S(N n) {
        this.n = n;
    }
}

final class LE<N extends Nat, M extends Nat> {
    private LE() {}
    public static final <N extends Nat, M extends Nat> void check(final N n, final M m, final LE<N, M> ev) {}
    public static final <N extends Nat> LE<N, N> e() {
        return new LE<N, N>();
    }
    public static final <N extends Nat, M extends Nat> LE<N, S<M>> l(final LE<N, M> ev) {
        return new LE<N, S<M>>();
    }
    public static final void example() {
        check(_0._0, _0._0, e());
        check(_0._0, Nat._1, l(LE.<_0>e()));
        check(_0._0, Nat._2, l(l(LE.<_0>e())));
        check(Nat._1, Nat._2, l(LE.<S<_0>>e()));
    }
}
