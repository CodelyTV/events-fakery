package com.codely.models

sealed trait Field

case class SimpleField(
    fieldType: String,
    valueType: Option[String],
    pattern: Option[String] = None,
    constant: Option[String] = None,
    enum: Option[List[String]] = None,
    minimum: Option[Int] = None,
    maximum: Option[Int] = None,
    format: Option[String] = None
) extends Field

case class ArrayField(
    items: Map[String, SimpleField]
) extends Field

case class EventType(
    eventType: String,
    fields: Map[String, Field]
)

case class GenerationSettings(
    numberOfEvents: Int,
    sleepTimeMs: Option[Int],
    duplicatePercentage: Int,
    missingFieldsPercentage: Int,
    incorrectTypesPercentage: Int
)

case class KafkaSettings(
    bootstrapServers: String,
    topic: String
)

case class EventSchema(
    eventTypes: List[EventType],
    generationSettings: GenerationSettings,
    kafkaSettings: KafkaSettings
)
