object Factorial {
  def f(n: Int): Int = {
    if (n < 0) throw new IllegalArgumentException("負の数は嫌いです")
    if (n == 0) 1
    else {
      val v = n * f(n - 1)
      if (v < 0)
        throw new IllegalArgumentException("大きな数は嫌いです")
      v
    }
  }

  def test(n: Int): Unit = {
    def factorial(): Unit = {
      println(s"factorial($n) = ${Factorial.f(n)}")
    }
    ExceptionsLab.test(factorial _)
  }
}

object FactNormal extends App {
  for (n <- 0 to 10) {
    Factorial.test(n)
  }
}

object FactNegative extends App {
  // ExceptionsLab.test(() => Factorial.f(-1))
  Factorial.test(-1)
}

object FactTooLarge extends App {
  Factorial.test(17)
}
