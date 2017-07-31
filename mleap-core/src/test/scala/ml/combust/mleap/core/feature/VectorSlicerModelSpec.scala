package ml.combust.mleap.core.feature

import ml.combust.mleap.core.types.{StructField, TensorType}
import org.scalatest.FunSpec

class VectorSlicerModelSpec extends FunSpec {

  describe("vector slicer model") {
    val model = VectorSlicerModel(Array())

    it("has the right input schema") {
      assert(model.inputSchema.fields ==
        Seq(StructField("input", TensorType.Double())))
    }

    it("has the right output schema") {
      assert(model.outputSchema.fields ==
        Seq(StructField("output", TensorType.Double())))
    }
  }
}