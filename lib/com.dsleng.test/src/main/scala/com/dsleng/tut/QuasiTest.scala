package com.dsleng.tut

//import scala.reflect.runtime.currentMirror
//import scala.reflect.api.Quasiquotes
//import scala.tools.reflect.ToolBox
//import scala.reflect.macros.runtime.Context

//import scala.language.experimental.macros
//import scala.reflect.macros._


import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.util.matching._
import java.util.Date
import org.apache.spark.sql.catalyst.expressions.SPArrayContains

object QuasiTest {
  def addCreationDate(): java.util.Date = macro addCreationDateMacro
  
  def greeting: String = macro greetingMacro

  def greetingMacro(c: Context): c.Tree = {
    import c.universe._

    val now = new Date().toString

    q"""
     "Hi! This code was compiled at " +
     $now
     """
  }
  def addCreationDateMacro(c: Context)(): c.Expr[java.util.Date] = {
    import c.universe._
     
    val date = q"new java.util.Date()" // this is the quasiquote
    c.Expr[java.util.Date](date)
  }
  
  def printTree(title: String)(expr: Any): Unit =
    macro printTreeMacro

  def printTreeMacro(c: Context)(title: c.Tree)(expr: c.Tree) = {
    import c.universe._
    import PrettyPrint._

    // `showCode` and `showRaw` are useful methods from the macro API
    // that convert Trees to Strings:
    //
    //  - `showCode` pretty-prints the expression in the tree in a
    //    form that could be passed to an `eval` method
    //
    //  - `showRaw` renders a case-class-like printout of the tree
    //
    // `prettify` is a rudimentary pretty-printer for case-class-like,
    // expressions built specifically for this macro. It does not work
    // in all cases:

    val code: String = showCode(expr)
    val tree: String = prettify(showRaw(expr))

    // Rather than print the values of `code` and `tree` directly, we
    // inject the Strings in `code` and `tree` into a `println` statement.
    //
    // This allows us to interleave calls to `printTree` in our application
    // with regular debugging statements such as `println`:

    q"""
    println(
      $title.toUpperCase + "\n\n" +
      "Desugared code:\n"  + $code + "\n\n" +
      "Underlying tree:\n" + $tree + "\n\n"
    )
    """
  }
}





