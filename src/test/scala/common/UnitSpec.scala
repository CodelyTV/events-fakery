package common

import org.scalatest._
import flatspec._
import matchers._

abstract class UnitSpec
    extends AnyFlatSpec
    with should.Matchers
    with OptionValues
    with Inside
    with Inspectors
