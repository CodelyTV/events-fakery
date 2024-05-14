package com.codely

import com.codely.args.CommandLineArgumentsParser
import com.codely.generators.Generators
import com.codely.kafka.KafkaEventProducer
import com.codely.models.EventSchema
import com.codely.parsers.ConfigParser
import com.typesafe.scalalogging.LazyLogging

object EventProducerApp extends App with LazyLogging {
  val cmdArgs = CommandLineArgumentsParser.parse(args)

  val eventSchema: EventSchema =
    ConfigParser.parseSchema(cmdArgs.eventsConfigPath)

  logger.info(s"EventProducerApp|Schema parsed successfully: $eventSchema")

  try {
    // TODO - Implement with parallel collections
    eventSchema.eventTypes.foreach { eventType =>
      for (_ <- 1 to eventSchema.generationSettings.numberOfEvents) {
        val sampleEvent: Map[String, Any] =
          Generators.generateEvents(eventSchema, eventType.eventType).sample.get
        KafkaEventProducer.sendEventToKafka(sampleEvent, eventSchema)

        eventSchema.generationSettings.sleepTimeMs.foreach { sleepTime =>
          Thread.sleep(sleepTime)
        }
      }
    }
  } finally {
    KafkaEventProducer.closeProducer()
  }
}
