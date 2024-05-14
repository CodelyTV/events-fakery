package es.cherrejim

import org.scalatest.matchers.should.Matchers
import com.dimafeng.testcontainers.KafkaContainer
import common.UnitSpec

class EventProducerTest extends UnitSpec with Matchers {

  val kafkaContainer: KafkaContainer = new KafkaContainer

  def kafka: KafkaContainer = kafkaContainer

  "EventProducer" should "generate and send events to Kafka" in {}
}
