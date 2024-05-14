package com.codely.parsers

import com.codely.models.{ArrayField, EventSchema, EventType, Field, GenerationSettings, KafkaSettings, SimpleField}
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

object ConfigParser {

  def parseSchema(configFilePath: String): EventSchema = {
    val config = ConfigFactory.parseFile(new java.io.File(configFilePath))
    if (config.isEmpty)
      throw new IllegalArgumentException(
        s"ConfigParser|Empty configuration file: $configFilePath"
      )
    val eventTypes = parseEventTypes(config.getConfigList("eventTypes").asScala)
    val generationSettings = parseGenerationSettings(
      config.getConfig("generationSettings")
    )
    val kafkaSettings = parseKafkaSettings(config.getConfig("kafkaSettings"))
    EventSchema(eventTypes, generationSettings, kafkaSettings)
  }

  private def parseEventTypes(configList: Seq[Config]): List[EventType] = {
    configList.map { config =>
      val eventType = config.getString("type")
      val fields    = parseFields(config.getConfig("fields"))
      EventType(eventType, fields)
    }.toList
  }

  private def parseFields(config: Config): Map[String, Field] = {
    config
      .root()
      .keySet()
      .asScala
      .map { key =>
        val fieldConfig = config.getConfig(key)
        val field = if (fieldConfig.getString("type") == "array") {
          ArrayField(parseSimpleFields(fieldConfig.getConfig("items")))
        } else {
          parseSimpleField(fieldConfig)
        }
        key -> field
      }
      .toMap
  }

  private def parseSimpleFields(config: Config): Map[String, SimpleField] = {
    config
      .root()
      .keySet()
      .asScala
      .map { key =>
        val fieldConfig = config.getConfig(key)
        key -> parseSimpleField(fieldConfig)
      }
      .toMap
  }

  private def parseSimpleField(config: Config): SimpleField = {
    val fieldType = config.getString("type")
    val valueType =
      if (config.hasPath("valueType")) Some(config.getString("valueType"))
      else None
    val pattern  = getOptionalString(config, "pattern")
    val constant = getOptionalString(config, "constant")
    val enum     = getOptionalStringList(config, "enum")
    val minimum  = getOptionalInt(config, "minimum")
    val maximum  = getOptionalInt(config, "maximum")
    val format   = getOptionalString(config, "format")

    SimpleField(
      fieldType,
      valueType,
      pattern,
      constant,
      enum,
      minimum,
      maximum,
      format
    )
  }

  private def parseGenerationSettings(config: Config): GenerationSettings = {
    val numberOfEvents = config.getInt("numberOfEvents")
    val sleepTimeMs =
      if (config.hasPath("sleepTimeMs")) Some(config.getInt("sleepTimeMs"))
      else None
    val duplicatePercentage      = config.getInt("duplicatePercentage")
    val missingFieldsPercentage  = config.getInt("missingFieldsPercentage")
    val incorrectTypesPercentage = config.getInt("incorrectTypesPercentage")

    GenerationSettings(
      numberOfEvents,
      sleepTimeMs,
      duplicatePercentage,
      missingFieldsPercentage,
      incorrectTypesPercentage
    )
  }

  private def parseKafkaSettings(config: Config): KafkaSettings = {
    val bootstrapServers = config.getString("bootstrapServers")
    val topic            = config.getString("topic")

    KafkaSettings(bootstrapServers, topic)
  }

  private def getOptionalString(
      config: Config,
      path: String
  ): Option[String] = {
    if (config.hasPath(path)) Some(config.getString(path)) else None
  }

  private def getOptionalStringList(
      config: Config,
      path: String
  ): Option[List[String]] = {
    if (config.hasPath(path)) Some(config.getStringList(path).asScala.toList)
    else None
  }

  private def getOptionalInt(config: Config, path: String): Option[Int] = {
    if (config.hasPath(path)) Some(config.getInt(path)) else None
  }
}
