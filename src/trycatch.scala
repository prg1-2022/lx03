import scala.io.Source

import java.lang.ArithmeticException
import java.lang.NumberFormatException
import java.io.FileNotFoundException

object TryCatch extends App {
  // 除算をし、0による算術例外が発生した場合は結果を Int.MaxValue とする（お薦めしない）
  def try_div(a: Int, b: Int): Int = {
    try { a / b
    } catch {
      case e: ArithmeticException => Int.MaxValue
    }
  }
  for (i <- -5 to 5) println(s"120 / $i = ${try_div(120, i)}")

  // 数字を読み取り、失敗した場合には失敗数を計上する例
  var number_format_errors = 0
  def print_number(num: String): Unit = {
    try { println(s"$num => ${Integer.parseInt(num)}")
    } catch {
      case e: NumberFormatException =>
        number_format_errors = number_format_errors + 1
    }
  }
  for (num <- List("1", "Zero", "零", "壱")) { print_number(num) }
  println(s"入力エラー数: $number_format_errors")

  // ファイルの先頭1行を表示し、ファイルが存在しない場合はエラーメッセージを表示する
  def try_first_line(path: String): String = {
    try { Source.fromFile(path).getLines().take(1).mkString
    } catch {
      case e: FileNotFoundException => "<<<ファイルは見つかりませんでした>>>"
    }
  }
  val filenames = List("trycatch.scala", "こんなファイルは存在しない", "throws.scala")
  for (filename <- filenames) {
    println(s"$filename => ${try_first_line(filename)}")
  }

  def try_zip[S, T](l1: List[S], l2: List[T]): List[(S, T)] = {
    (l1, l2) match {
      case (Nil, Nil) => Nil
      case (x1 :: l1, x2 :: l2) => (x1, x2) :: try_zip(l1, l2)
    }
  }
  val lists1 = List(List(1), List(1, 2), List(1, 2, 3))
  val list2  = List("A", "B")
  for (list1 <- lists1) {
    val result = try { try_zip(list1, list2)
      } catch {
        case e: MatchError => "<<<パターンマッチに失敗しました>>>"
      }
    println(s"zip($list1, $list2) = $result")
  }
}


object TryFactorial extends App {
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

  for (n <- -3 to 20) {
    val result = {
      try { s"${f(n)}"
      } catch {
        case e: IllegalArgumentException => {
          e.getMessage() match {
            case "負の数は嫌いです"   => "-" * 10
            case "大きな数は嫌いです" => "+" * 10
          }
        }
      }
    }
    println(s"factorial($n) = $result")
  }
}
