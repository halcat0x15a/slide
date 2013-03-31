!SLIDE

# Machines

!SLIDE

[@halcat0x15a](https://twitter.com/halcat0x15a)

* Scala and Clojure fan.
* I can read Haskell, but can not write.
* I was interested in the scala-machines.

!SLIDE

# Machines

* Stream processing library
* Author: [@kmett](https://twitter.com/kmett), [@runarorama](https://twitter.com/runarorama)
* implemented by Haskell and Scala

!SLIDE

# Types

* Machine
    * Input and output stream
* Plan
    * for *Machine*
* Process
    * Transducer
* Source

!SLIDE

# Machine

```haskell
data Step k o r
  = Stop
  | Yield o r
  | forall t. Await (t -> r) (k t) r

newtype MachineT m k o = MachineT { runMachineT :: m (Step k o (MachineT m k o)) }

type Machine k o = forall m. Monad m => MachineT m k o
```

!SLIDE

# Primitives

* await: apply input
* yield: output
* stop: end of stream

!SLIDE

# Plan

```haskell
data Plan k o a
  = Done a
  | Yield o (Plan k o a)
  | forall z. Await (z -> Plan o a) (k z) (Plan k o a)
  | Fail

yield :: o -> Plan k o ()
await :: Category k => Plan (k i) o i
stop :: Plan k o a

construct :: Monad m => PlanT k o m a -> MachineT m k o
repeatedly :: Monad m => PlanT k o m a -> MachineT m k o
before :: Monad m => MachineT m k o -> PlanT k o m a -> MachineT m k o
```

!SLIDE

# Construct Machine

Construct *Plan* by *Monad* and then compile *Plan* for *Machine*

```haskell
filtered :: (a -> Bool) -> Process a a
filtered p = repeatedly $ do
  i <- await
  when (p i) $ yield i

source :: Foldable f => f b -> Source b
source xs = construct (traverse_ yield xs)
```

!SLIDE

# Process, Source

```haskell
type Source b = forall k. Machine k b
type SourceT m b = forall k. MachineT m k b

type Process a b = Machine (Is a) b
type ProcessT m a b = MachineT m (Is a) b

data Is a b where
  Refl :: Is a a
```

!SLIDE

# Transduce

```haskell
(<~) :: Monad m => ProcessT m b c -> MachineT m k b -> MachineT m k c
(~>) :: Monad m => MachineT m k b -> ProcessT m b c -> MachineT m k c
```

!SLIDE

# Run

```haskell
Prelude Data.Machine> run $ source [1 .. 10] ~> filtered even
[2,4,6,8,10]
```

!SLIDE

# Automaton

*auto* is lift to *Process*

```haskell
class Automaton k where
  auto :: k a b -> Process a b
```

Automaton (->)

```haskell
Prelude Data.Machine> run $ source [1 .. 5] ~> auto (* 2)
[2,4,6,8,10]
```

!SLIDE

# Tee, Wye

can read from two input stream

```haskell
type Tee a b c = Machine (T a b) c
type TeeT m a b c = MachineT m (T a b) c

data T a b c where
  L :: T a b a
  R :: T a b b

type Wye a b c = Machine (Y a b) c
type WyeT m a b c = MachineT m (Y a b) c

data Y a b c where
  X :: Y a b a
  Y :: Y a b b
  Z :: Y a b (Either a b)
```

!SLIDE

# awaits

choice input source by *awaits*

provide *Source* by *tee* or *wye*

```haskell
main = (runT $ tee odds evens plus) >>= print
  where
    odds = numbers ~> filtered odd
    evens = numbers ~> filtered even
    numbers = source [1 .. 10]
    plus = repeatedly $ do
      l <- awaits L
      r <- awaits R
      yield $ l + r
```

!SLIDE

# I/O

can include side-effect in *Plan* by *liftIO*

```haskell
lineSource :: SourceT IO String
lineSource = repeatedly $ do
  s <- liftIO getLine
  yield s

printMachine :: ProcessT IO String ()
printMachine = repeatedly $ do
  s <- await
  liftIO $ putStrLn s

main = runT_ $ lineSource ~> printMachine
```

!SLIDE

# Summary

* Simple API
* Do not take part in I/O (only MonadIO)

!SLIDE

# scala-machines

* *Monad* transformer does not exist
* using *Procedure* and *Driver*

!SLIDE

# Driver, Procedure

* Driver
    * apply input to continuations
* Procedure
    * drive *Machine* by *Driver*

!SLIDE

# Example

```scala
object Main extends SafeApp {
  override def runc = new Procedure[IO, String] {
    type K = String => Any
    val machine = await[String] flatMap emit repeatedly
    def withDriver[R](f: Driver[IO, K] => IO[R]) =
      new Driver[IO, K] {
        val M = Monad[IO]
        def apply(k: K) = readLn map (k >>> Option[Any])
      } |> f
  } foreach putStrLn
}
```

!SLIDE

# Summary

* different approach
* can separate I/O

!SLIDE

# References

* [Haskell の machines に入門してみた，というお話](http://krdlab.hatenablog.com/entry/2013/03/16/204039)
* [Machines](https://dl.dropbox.com/u/4588997/Machines.pdf) by runarorama
