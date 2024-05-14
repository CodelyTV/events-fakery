package com.codely.generators

import com.codely.models.{ArrayField, EventSchema, EventType, SimpleField}
import org.scalacheck.Gen

import java.time.Instant
import scala.collection.JavaConverters._

object Generators {
  def generateValue(field: SimpleField): Gen[Any] =
    field.valueType match {
      case Some("constant") => Gen.const(field.constant.get)
      case Some("pattern")  => generatePattern(field.pattern.get)
      case Some("enum")     => Gen.oneOf(field.enum.get)
      case Some("range")    => Gen.choose(field.minimum.get, field.maximum.get)
      case Some("format")   => generateFormat(field.format.get)
      case Some("examples") =>
        Gen.oneOf(
          field.enum.getOrElse(Nil)
        ) // Use `enum` field to handle `examples`
      case _ => Gen.const("")
    }

  // TODO - Implement in a generic way
  private def generatePattern(pattern: String): Gen[String] =
    pattern match {
      case "prod[0-9]+" => Gen.listOfN(7, Gen.numChar).map("prod" + _.mkString)
      case "user[0-9]+" => Gen.listOfN(7, Gen.numChar).map("user" + _.mkString)
      case "trans[0-9]+" =>
        Gen.listOfN(7, Gen.numChar).map("trans" + _.mkString)
      case "cart[0-9]+" => Gen.listOfN(7, Gen.numChar).map("cart" + _.mkString)
      case _            => Gen.alphaNumStr // Fallback for other patterns
    }

  private def generateFormat(format: String): Gen[String] =
    format match {
      case "uuid"      => Gen.uuid.map(_.toString)
      case "date-time" => Gen.const(Instant.now.toString)
      case _           => Gen.const("")
    }

  private def generateObject(
      fields: Map[String, SimpleField]
  ): Gen[Map[String, Any]] = {
    val fieldGens = fields.map {
      case (fieldName, field) =>
        generateValue(field).map(value => fieldName -> value)
    }
    Gen.sequence(fieldGens).map(_.asScala.toMap)
  }

  def generateEvent(eventType: EventType): Gen[Map[String, Any]] = {
    val fieldGens = eventType.fields.map {
      case (fieldName, field: SimpleField) =>
        generateValue(field).map(value => fieldName -> value)
      case (fieldName, field: ArrayField) =>
        Gen.listOf(generateObject(field.items)).map(value => fieldName -> value)
    }
    Gen.sequence(fieldGens).map(_.asScala.toMap)
  }

  def generateEvents(
      schema: EventSchema,
      eventTypeName: String
  ): Gen[Map[String, Any]] = {
    schema.eventTypes.find(_.eventType == eventTypeName) match {
      case Some(eventType) => generateEvent(eventType)
      case None            => Gen.const(Map.empty[String, Any])
    }
  }
}
