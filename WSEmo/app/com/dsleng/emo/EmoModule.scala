package com.dsleng.emo

import play.api.Configuration
import play.api.Environment
import play.api.inject.Binding
import play.api.inject.Module
import scala.concurrent.ExecutionContext


class EmoModule extends Module {
  def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[EmoComponent].to[EmoComponentImpl].eagerly()
    )
  }
}