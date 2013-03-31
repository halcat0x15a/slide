{-# LANGUAGE RankNTypes #-}

import Data.Machine
import Data.Semigroup
import Control.Monad (when)
import Control.Monad.IO.Class (liftIO)

filtered' :: (a -> Bool) -> Process a a
filtered' p = repeatedly $ do
  a <- await
  if (p a)
    then yield a
    else stop

{-
main = runT machine 
  where
    machine = source [1 .. 10] ~> filtered even ~> auto (* 2)
-}

main = (runT $ tee odds evens plus) >>= print
  where
    odds = numbers ~> filtered odd
    evens = numbers ~> filtered even
    numbers = source [1 .. 10]
    plus = repeatedly $ do
      l <- awaits L
      r <- awaits R
      yield $ l + r

lineSource :: SourceT IO String
lineSource = repeatedly $ do
  s <- liftIO getLine
  yield s

printMachine :: ProcessT IO String ()
printMachine = repeatedly $ do
  s <- await
  liftIO $ putStrLn s

-- main = runT_ $ lineSource ~> printMachine
