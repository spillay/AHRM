package com.dsleng.tut

import scala.reflect.runtime.currentMirror
import scala.reflect.api.Quasiquotes
import scala.tools.reflect.ToolBox
import scala.reflect.macros.runtime.Context

import scala.language.experimental.macros
import scala.reflect.macros._

class QuasiTest {
}
object QuasiTest extends App {

  def doTest() = macro impl

  def impl(c: blackbox.Context)(): c.Expr[Unit] = {
    import c.universe._ //access to AST classes
    
    val world = TermName("Earth")
    val tree = q"""println("Hello" + ${world.decodedName.toString})"""

    tree match {
      case q"""println("Hello Earth")""" => println("succeeded")
      case _ => c.abort(c.enclosingPosition, s"huh? was: $tree")
    }

    c.Expr(tree) //wrap tree and tag with its type
  }
}


