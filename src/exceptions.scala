import sys.process._
import scala.io.Source

import java.lang.ArithmeticException
import java.io.{ FileNotFoundException, IOException }

/**
 *
 * さまざまな例外を発生させて様子を観察する例題。
 *
 * スライドで説明されたさまざまな状況下をプログラムで再現しています。
 * 個々の場合ごとに try { ... } catch { ... } を書くのが面倒だったので、
 * 例外処理は test 関数にまかせています。個別のケースは関数定義し、
 * その関数を test 関数の引数に渡すことで実現しています。
 * はい、そうなんです、関数の引数に関数を渡すことができるんです。（高階関数といいます。知ってますよね？）
 *
 * test のなかの h という関数は単に g を呼びます。
 * そして g は f を呼びます。
 * g, h にあまり意味はないように見えますし、実際、あまり意味はありません。
 * ただ、例外が発生したときに表示される例外の構造のなかに g と h が出現します。
 * それを観察して欲しいために加えてあります。
 *
 * 例外の構造は時としてに数十行にもなります。目が潰れて嫌になります。
 * 今回の例題ではその長い構造の先頭の10行だけを表示しています。
 **/

object ExceptionsLab extends App {

  /** 例外を発生させるかもしれない関数 f を呼び出すためのテスト
   *  fから発生した例外を try ... catch で捉えています。
   *  発生した例外について、例外が発生した箇所までの関数の呼出順序（スタックトレース）を表示します。
   **/
  def test(f:() => Unit): Unit = {
    def g(): Unit = { f() }
    def h(): Unit = { g() }

    try {
      h()
    } catch {
      case e: Exception => {
        println("-" * 70)
        println(s"例外が発生しました。 -- ${e}")
        println(e.getStackTrace.take(10).mkString("\n"))
        println("-" * 70 + "\n.\n")
      }
    }
  }

  // 困った状況1
  def zeroで除算(): Unit = { 1 / 0; () }

  test(zeroで除算)

  // 困った状況2
  def parse壱(): Unit = { Integer.parseInt("壱"); () }

  test(parse壱)

  // 困った状況3
  def source親の親():           Unit = { Source.fromFile("/../file.txt"); () }
  def source存在しないファイル(): Unit = { Source.fromFile("f" * 255); () }
  def source長すぎるファイル名(): Unit = { Source.fromFile("f" * 256); () }

  test(source親の親)
  test(source存在しないファイル)
  test(source長すぎるファイル名)

  // 困った状況4
  def 空リストに先頭の要素はない(): Unit = {
    (Nil: List[String]) match { case x :: _ => x }; ()
  }
  def 空リストに残りのリストはない(): Unit = {
    (Nil: List[String]) match { case _ :: l => l }; ()
  }
  def ファイルが存在する場合はその先頭から5行を表示(): Unit = {
    println(Source.fromFile("src/exceptions.scala").getLines().take(5).mkString("\n"))
  }
  def ファイル中のファイル(): Unit = { Source.fromFile("src/exceptions.scala/ファイルのなかにファイルがあるわけない"); () }

  test(空リストに先頭の要素はない)
  test(空リストに残りのリストはない)
  test(ファイルが存在する場合はその先頭から5行を表示)
  test(ファイル中のファイル)

  // 困った状況5
  def zip[S, T](l1: List[S], l2: List[T]): List[(S, T)] = {
    (l1, l2) match {
      case (Nil, Nil) => Nil
      case (x1 :: l1, x2 :: l2) => (x1, x2) :: zip(l1, l2)
    }
  }

  def 異なる長さのリストをzipしてはいけない(): Unit = {
    zip(List(1, 2), List("A", "B", "C")); ()
  }

  test(異なる長さのリストをzipしてはいけない)

  // 困った状況6
  val 学校の仲間たち = List("のび太", "ジャイアン", "スネ夫")

  def find(子供: String): Int = {
    def find_i(仲間たち: List[String], i: Int): Int = {
      仲間たち match {
        case 子供 :: _ => i
        case _ :: l => find_i(l, i + 1)
      }
    }
    find_i(学校の仲間たち, 0)
  }

  def しずかちゃんはもう卒業しちゃったよ(): Unit = {
    println(find("のび太"))
    println(find("しずか"))
  }

  test(しずかちゃんはもう卒業しちゃったよ)


  // 困った状況7 - 計算資源を食い潰す例
  def 無限再帰地獄(): Int = { 0 + 無限再帰地獄() }
  def メモリーモンスター(): List[Int] = { Range(0, Int.MaxValue).toList }  // 長さ2^31のリスト

  /* 注意
   *
   * 以下のふたつの過酷な例外が発生すると Scala は「即死」するので補足できません。
   * memory_eater はメモリを食い潰す関数なので実行時間が長いです。一分以上かかるかもしれません。
   * 以下のコメントを外して実行してもパソコンが壊れることはありません。
   * 電源の繋がっていないノートパソコンの場合 memory_eater を実行すると急速に電池残量が失われるかもしれません。
   * 怪しげなこと、先生は危なくないと言っていたけど危なそうなことは「ヤル！」
   */

  test(() => { 無限再帰地獄(); () })
  // test(メモリーモンスター)
}
