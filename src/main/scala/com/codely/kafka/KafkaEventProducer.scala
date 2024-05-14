package com.codely.kafka

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import com.typesafe.scalalogging.LazyLogging

import java.util.Properties
import io.circe.syntax._
import com.codely.encoders.Encoders._
import com.codely.models.EventSchema

object KafkaEventProducer extends LazyLogging {

  @volatile private var producer: KafkaProducer[String, String] = _

  def initializeProducer(schema: EventSchema): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", schema.kafkaSettings.bootstrapServers)
    props.put("key.serializer", classOf[StringSerializer].getName)
    props.put("value.serializer", classOf[StringSerializer].getName)

    producer = new KafkaProducer[String, String](props)
  }

  def sendEventToKafka(event: Map[String, Any], schema: EventSchema): Unit = {
    if (producer == null) {
      initializeProducer(schema)
    }

    try {
      val jsonString = event.asJson.noSpaces
      logger.info(s"Sending event JSON: $jsonString")
      val record = new ProducerRecord[String, String](
        schema.kafkaSettings.topic,
        jsonString
      )
      logger.info(s"Sending event: ${event.asJson.noSpaces}")
      val metadata: RecordMetadata = producer.send(record).get()
      logger.info(
        s"Event sent to partition ${metadata.partition()} with offset ${metadata.offset()}"
      )
    } catch {
      case e: Exception => logger.error("Error sending event to Kafka", e)
    }
  }

  def closeProducer(): Unit = {
    if (producer != null) {
      producer.close()
    }
  }
}
