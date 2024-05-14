package com.codely.encoders

import io.circe.{Encoder, Json}
import java.time.Instant

object Encoders {

  implicit val encodeMapStringAny: Encoder[Map[String, Any]] =
    Encoder.instance { map =>
      Json.obj(
        map.map {
          case (key, value: String)  => key -> Json.fromString(value)
          case (key, value: Int)     => key -> Json.fromInt(value)
          case (key, value: Long)    => key -> Json.fromLong(value)
          case (key, value: Double)  => key -> Json.fromDoubleOrString(value)
          case (key, value: Boolean) => key -> Json.fromBoolean(value)
          case (key, value: Instant) => key -> Json.fromString(value.toString)
          case (key, value: Map[_, _]) =>
            key -> encodeMapStringAny(value.asInstanceOf[Map[String, Any]])
          case (key, value: Seq[_]) =>
            key -> Json.fromValues(value.map {
              case v: String  => Json.fromString(v)
              case v: Int     => Json.fromInt(v)
              case v: Long    => Json.fromLong(v)
              case v: Double  => Json.fromDoubleOrString(v)
              case v: Boolean => Json.fromBoolean(v)
              case v: Instant => Json.fromString(v.toString)
              case v: Map[_, _] =>
                encodeMapStringAny(v.asInstanceOf[Map[String, Any]])
              case _ => Json.Null
            })
          case (key, _) => key -> Json.Null
        }.toSeq: _*
      )
    }
}
