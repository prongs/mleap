package ml.combust.mleap.core.types

import ml.combust.mleap.tensor.{ByteString, Tensor}
import org.scalatest.FunSpec

/**
  * Created by hollinwilkins on 9/18/17.
  */
class CastingSpec extends FunSpec {
  def createValue(base: BasicType, value: Double): Any = base match {
    case BasicType.Boolean => if (value == 0) false else true
    case BasicType.Byte => value.toByte
    case BasicType.Short => value.toShort
    case BasicType.Int => value.toInt
    case BasicType.Long => value.toLong
    case BasicType.Float => value.toFloat
    case BasicType.Double => value
    case BasicType.String => value.toString
    case BasicType.ByteString => ByteString(value.toString.getBytes)
  }

  val castTests = Seq(
    (BasicType.Boolean, BasicType.Byte, true, 1.toByte),
    (BasicType.Boolean, BasicType.Short, true, 1.toShort),
    (BasicType.Boolean, BasicType.Int, true, 1),
    (BasicType.Boolean, BasicType.Long, true, 1.toLong),
    (BasicType.Boolean, BasicType.Float, true, 1.0.toFloat),
    (BasicType.Boolean, BasicType.Double, true, 1.0),
    (BasicType.Boolean, BasicType.String, true, "1"),

    (BasicType.Byte, BasicType.Boolean, 7.toByte, true),
    (BasicType.Byte, BasicType.Boolean, 0.toByte, false),
    (BasicType.Byte, BasicType.Short, 13.toByte, 13.toShort),
    (BasicType.Byte, BasicType.Int, 13.toByte, 13),
    (BasicType.Byte, BasicType.Long, 13.toByte, 13.toLong),
    (BasicType.Byte, BasicType.Float, 13.toByte, 13.toFloat),
    (BasicType.Byte, BasicType.Double, 13.toByte, 13.toDouble),
    (BasicType.Byte, BasicType.String, 13.toByte, 13.toString),

    (BasicType.Short, BasicType.Boolean, 7.toShort, true),
    (BasicType.Short, BasicType.Boolean, 0.toShort, false),
    (BasicType.Short, BasicType.Byte, 13.toShort, 13.toByte),
    (BasicType.Short, BasicType.Int, 13.toShort, 13),
    (BasicType.Short, BasicType.Long, 13.toShort, 13.toLong),
    (BasicType.Short, BasicType.Float, 13.toShort, 13.toFloat),
    (BasicType.Short, BasicType.Double, 13.toShort, 13.toDouble),
    (BasicType.Short, BasicType.String, 13.toShort, 13.toString),

    (BasicType.Int, BasicType.Boolean, 7, true),
    (BasicType.Int, BasicType.Boolean, 0, false),
    (BasicType.Int, BasicType.Byte, 13, 13.toByte),
    (BasicType.Int, BasicType.Short, 13, 13.toShort),
    (BasicType.Int, BasicType.Long, 13, 13.toLong),
    (BasicType.Int, BasicType.Float, 13, 13.toFloat),
    (BasicType.Int, BasicType.Double, 13, 13.toDouble),
    (BasicType.Int, BasicType.String, 13, 13.toString),

    (BasicType.Long, BasicType.Boolean, 7.toLong, true),
    (BasicType.Long, BasicType.Boolean, 0.toLong, false),
    (BasicType.Long, BasicType.Byte, 13.toLong, 13.toByte),
    (BasicType.Long, BasicType.Short, 13.toLong, 13.toShort),
    (BasicType.Long, BasicType.Int, 13.toLong, 13),
    (BasicType.Long, BasicType.Float, 13.toLong, 13.toFloat),
    (BasicType.Long, BasicType.Double, 13.toLong, 13.toDouble),
    (BasicType.Long, BasicType.String, 13.toLong, 13.toString),

    (BasicType.Float, BasicType.Boolean, 7.0.toFloat, true),
    (BasicType.Float, BasicType.Boolean, 0.0.toFloat, false),
    (BasicType.Float, BasicType.Byte, 13.0.toFloat, 13.0.toFloat.toByte),
    (BasicType.Float, BasicType.Short, 13.0.toFloat, 13.0.toFloat.toShort),
    (BasicType.Float, BasicType.Int, 13.0.toFloat, 13.0.toFloat.toInt),
    (BasicType.Float, BasicType.Long, 13.0.toFloat, 13.0.toLong),
    (BasicType.Float, BasicType.Double, 13.0.toFloat, 13.0),
    (BasicType.Float, BasicType.String, 13.toFloat, 13.0.toFloat.toString),

    (BasicType.Double, BasicType.Boolean, 7.0, true),
    (BasicType.Double, BasicType.Boolean, 0.0, false),
    (BasicType.Double, BasicType.Byte, 13.0, 13.0.toByte),
    (BasicType.Double, BasicType.Short, 13.0, 13.0.toShort),
    (BasicType.Double, BasicType.Int, 13.0, 13.0.toInt),
    (BasicType.Double, BasicType.Long, 13.0, 13.0.toLong),
    (BasicType.Double, BasicType.Float, 13.0, 13.0.toFloat),
    (BasicType.Double, BasicType.String, 13.0, 13.0.toString),

    (BasicType.String, BasicType.Boolean, "true", true),
    (BasicType.String, BasicType.Boolean, "false", false),
    (BasicType.String, BasicType.Boolean, "", false),
    (BasicType.String, BasicType.Byte, "12", 12.toByte),
    (BasicType.String, BasicType.Short, "77", 77.toShort),
    (BasicType.String, BasicType.Int, "789", 789),
    (BasicType.String, BasicType.Long, "789", 789.toLong),
    (BasicType.String, BasicType.Float, "14.5", 14.5.toFloat),
    (BasicType.String, BasicType.Double, "16.7", 16.7)
  )

  def createTensor(base: BasicType, value: Any): Tensor[_] = base match {
    case BasicType.Boolean => Tensor.denseVector(Array(value.asInstanceOf[Boolean]))
    case BasicType.Byte => Tensor.denseVector(Array(value.asInstanceOf[Byte]))
    case BasicType.Short => Tensor.denseVector(Array(value.asInstanceOf[Short]))
    case BasicType.Int => Tensor.denseVector(Array(value.asInstanceOf[Int]))
    case BasicType.Long => Tensor.denseVector(Array(value.asInstanceOf[Long]))
    case BasicType.Float => Tensor.denseVector(Array(value.asInstanceOf[Float]))
    case BasicType.Double => Tensor.denseVector(Array(value.asInstanceOf[Double]))
    case BasicType.String => Tensor.denseVector(Array(value.asInstanceOf[String]))
    case BasicType.ByteString => Tensor.denseVector(Array(value.asInstanceOf[ByteString]))
  }

  def createTensorScalar(base: BasicType, value: Any): Tensor[_] = base match {
    case BasicType.Boolean => Tensor.scalar(value.asInstanceOf[Boolean])
    case BasicType.Byte => Tensor.scalar(value.asInstanceOf[Byte])
    case BasicType.Short => Tensor.scalar(value.asInstanceOf[Short])
    case BasicType.Int => Tensor.scalar(value.asInstanceOf[Int])
    case BasicType.Long => Tensor.scalar(value.asInstanceOf[Long])
    case BasicType.Float => Tensor.scalar(value.asInstanceOf[Float])
    case BasicType.Double => Tensor.scalar(value.asInstanceOf[Double])
    case BasicType.String => Tensor.scalar(value.asInstanceOf[String])
    case BasicType.ByteString => Tensor.scalar(value.asInstanceOf[ByteString])
  }

  for(((from, to, fromValue, expectedValue), i) <- castTests.zipWithIndex) {
    describe(s"cast from $from to $to - $i") {
      it("casts the scalar") {
        val c = Casting.cast(ScalarType(from), ScalarType(to)).get.get
        val oc = Casting.cast(ScalarType(from).asNullable, ScalarType(to)).get.get
        val co = Casting.cast(ScalarType(from), ScalarType(to).asNullable).get.get
        val oco = Casting.cast(ScalarType(from).asNullable, ScalarType(to).asNullable).get.get

        assert(c(fromValue) == expectedValue)
        assert(oc(Option(fromValue)) == expectedValue)
        assert(co(fromValue) == Option(expectedValue))
        assert(oco(Option(fromValue)) == Option(expectedValue))
      }

      it("casts the list") {
        val fromList = Seq(fromValue, fromValue, fromValue)
        val expectedList = Seq(expectedValue, expectedValue, expectedValue)

        val c = Casting.cast(ListType(from), ListType(to)).get.get
        val oc = Casting.cast(ListType(from).asNullable, ListType(to)).get.get
        val co = Casting.cast(ListType(from), ListType(to).asNullable).get.get
        val oco = Casting.cast(ListType(from).asNullable, ListType(to).asNullable).get.get

        assert(c(fromList) == expectedList)
        assert(oc(Option(fromList)) == expectedList)
        assert(co(fromList) == Option(expectedList))
        assert(oco(Option(fromList)) == Option(expectedList))
      }

      it("casts the tensor") {
        val fromTensor = createTensor(from, fromValue)
        val expectedTensor = createTensor(to, expectedValue)

        val fromScalarTensor = createTensorScalar(from, fromValue)
        val expectedScalarTensor = createTensorScalar(to, expectedValue)

        val c = Casting.cast(TensorType(from), TensorType(to)).get.get
        val oc = Casting.cast(TensorType(from).asNullable, TensorType(to)).get.get
        val co = Casting.cast(TensorType(from), TensorType(to).asNullable).get.get
        val oco = Casting.cast(TensorType(from).asNullable, TensorType(to).asNullable).get.get
        val tc = Casting.cast(ScalarType(from), TensorType(to, Some(Seq()))).get.get
        val ct = Casting.cast(TensorType(from, Some(Seq())), ScalarType(to)).get.get

        assert(c(fromTensor) == expectedTensor)
        assert(oc(Option(fromTensor)) == expectedTensor)
        assert(co(fromTensor) == Option(expectedTensor))
        assert(oco(Option(fromTensor)) == Option(expectedTensor))
        assert(tc(fromValue) == expectedScalarTensor)
        assert(ct(fromScalarTensor) == expectedValue)
      }
    }
  }
}
