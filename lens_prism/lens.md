# Deriving Lenses

[@halcat0x15a](https://twitter.com/halcat0x15a)



# Lens

今回考えるLens

```scala
case class Lens[A, B](get: A => B, set: (A, B) => A)
```



使用例

```scala
case class Person(name: String, age: Int, address: Address)

case class Address(street: String, city: String, postcode: String)

val name = Lens[Person, String](_.name, (person, name) => person.copy(name = name))
val age = Lens[Person, Int](_.age, (person, age) => person.copy(age = age))
val address = Lens[Person, Address](_.address, (person, address) => person.copy(address = address))
val street = Lens[Address, String](_.street, (address, street) => address.copy(street = street))
val city = Lens[Address, String](_.city, (address, city) => address.copy(city = city))
val postcode = Lens[Address, String](_.postcode, (address, postcode) => address.copy(postcode = postcode))
```



# Scrap Your Boilerplate

みっつの方法を紹介

* Reflection
* Macro
* Generic



# Reflection

Lensと対象のデータ型

```java
public abstract class Lens<A, B> {
    public abstract B get(A a);
    public abstract A set(A a, B b);
}
```

```java
public final class Person {
    private final String name;
    private final int age;
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
}
```



リフレクションによるLens

```java
public static <A, B> Optional<Lens<A, B>> lens(Class<A> c, String name) {
    try {
        Constructor ctor = c.getConstructors()[0];
        Parameter[] params = ctor.getParameters();
        Method[] methods = new Method[params.length];
        for (int i = 0; i < params.length; i++) methods[i] = get(c, params[i].getName());
        return Optional.of(lens(ctor, get(c, name), methods));
    } catch (NoSuchMethodException e) {
        return Optional.empty();
    }
}
private static <A> Method get(Class<A> c, String name) throws NoSuchMethodException {
    return c.getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
}
```



```java
public static <A, B> Lens<A, B> lens(Constructor ctor, Method method, Method[] methods) {
    return new Lens<A, B>() {
        public B get(A a) {
            try {
                return (B) method.invoke(a);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        public A set(A a, B b) {
            Stream<Object> args = Arrays.stream(methods).map(m -> {
                try {
                    return m.equals(method) ? b : m.invoke(a);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                return (A) ctor.newInstance(args.toArray());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    };
}
```



使用例

```scala
scala> val name = Lens.lens[Person, String](classOf[Person], "name").get
name: Lens[Person,String] = Lens$1@22962c94

scala> name.get(name.set(new Person("sanshiro", 21), "halcat0x15a"))
res0: String = halcat0x15a
```



## 特徴

* 安全でない
* 遅い
* JavaのADTとは



# Macro

Lensと対象のデータ型

```scala
case class Lens[A, B](get: A => B, set: (A, B) => A)

case class Person(name: String, age: Int)
```



マクロによるLens

```scala
def lens[A](name: String): Any = macro lensImpl[A]

def lensImpl[A: c.WeakTypeTag](c: scala.reflect.macros.whitebox.Context)(name: c.Expr[String]): c.Tree = {
  import c.universe._
  val str = c.eval(c.Expr[String](c.untypecheck(name.tree.duplicate)))
  symbolOf[A].asClass.primaryConstructor.asMethod.paramLists.head.find(_.name.toString == str) match {
    case Some(sym) =>
      val A = weakTypeOf[A]
      val B = sym.info
      val Lens = symbolOf[Lens[_, _]].companion
      val name = sym.asTerm.name
      q"$Lens[$A, $B](_.$name, (a, b) => a.copy($name = b))"
    case None =>
      c.abort(c.enclosingPosition, "no such field")
  }
}
```



使用例

```scala
scala> val name = Lens.lens[Person]("name")
name: Lens[Person,String] = $anon$1@52e9d4c9

scala> name.get(name.set(Person("sanshiro", 21), "halcat0x15a"))
res1: String = halcat0x15a
```



## 特徴

* 型安全
* 速い



# Generic

https://wiki.haskell.org/GHC.Generics

Lensと対象のデータ型

```haskell
{-# LANGUAGE DeriveGeneric, GADTs, DataKinds, TypeOperators, FlexibleInstances, FlexibleContexts, TypeFamilies, MultiParamTypeClasses, LambdaCase #-}
```

```
import GHC.Generics

data Lens a b = Lens {
  get :: a -> b,
  set :: a -> b -> a
}

compose :: Lens b c -> Lens a b -> Lens a c
compose f g = Lens (get f . get g) (\a -> (set g a) . (set f (get g a)))

data Person = Person { name :: String, age :: Int } deriving Generic
```



型レベル自然数とセレクタを決定するNth

```haskell
data Nat = Zero | Succ Nat

data N a where
  Z :: N Zero
  S :: N a -> N (Succ a)

type family Nth (n :: Nat) (f :: * -> *)
type instance Nth n (K1 i c) = c
type instance Nth n (M1 i t f) = Nth n f
type instance Nth Zero (f :*: g) = Nth Zero f
type instance Nth (Succ n) (f :*: g) = Nth n g
```



総称的な型のLens

```haskell
class GenericLens' n f where
  lens' :: N n -> Lens (f a) (Nth n f)

instance GenericLens' n (K1 i c) where
  lens' n = Lens unK1 $ const K1

instance GenericLens' n f => GenericLens' n (M1 i t f) where
  lens' n = compose (lens' n) (Lens unM1 (const M1))

instance GenericLens' Zero f => GenericLens' Zero (f :*: g) where
  lens' n = compose (lens' n) (Lens (\case a :*: _ -> a) (\case _ :*: b -> \a -> a :*: b))

instance GenericLens' n g => GenericLens' (Succ n) (f :*: g) where
  lens' (S n) = compose (lens' n) (Lens (\case _ :*: b -> b) (\case a :*: _ -> \b -> a :*: b))
```



GenericによるLens

```haskell
lens :: (Generic a, GenericLens' n (Rep a)) => N n -> Lens a (Nth n (Rep a))
lens n = Lens (get (lens' n) . from) (\a -> \b -> to $ set (lens' n) (from a) b)
```



使用例

```haskell
personName :: Lens Person String
personName = lens Z

personAge :: Lens Person Int
personAge = lens (S Z)

main = print $ get personName $ set personName (Person "sanshiro" 21) "halcat0x15a"
```



## 特徴

* 型安全
* 遅い



# まとめ

* 型安全に導出できる
* 速いコードは生成する方が簡単
