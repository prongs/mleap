package ml.combust.mleap.runtime.transformer.builder

import ml.combust.mleap.core.types.{StructField, StructType}
import ml.combust.mleap.runtime.{ArrayRow, Row, RowUtil}
import ml.combust.mleap.runtime.function.{Selector, UserDefinedFunction}

import scala.util.Try

/**
  * Created by hollinwilkins on 10/30/16.
  */
object RowTransformBuilder {
  def apply(schema: StructType): RowTransformBuilder = RowTransformBuilder(schema, schema, Array())
}

case class RowTransformBuilder private (inputSchema: StructType,
                                        outputSchema: StructType,
                                        transforms: Array[(ArrayRow) => ArrayRow]) extends TransformBuilder[RowTransformBuilder] {
  def arraySize: Int = outputSchema.fields.length

  override def withOutputs(outputs: Seq[String], inputs: Selector *)
                          (udf: UserDefinedFunction): Try[RowTransformBuilder] = {
    val count = udf.outputTypes.size
    val indices = outputSchema.fields.length until outputSchema.fields.length + count
    val fields = outputs.zip(udf.outputTypes).map {
      case (name, dt) => StructField(name, dt)
    }

    RowUtil.createRowSelectors(outputSchema, inputs: _*)(udf).flatMap {
      rowSelectors =>
        outputSchema.withFields(fields).map {
          schema2 =>
            val transform = {
              (row: ArrayRow) =>
                val values = row.udfValue(rowSelectors: _*)(udf).asInstanceOf[Product].productIterator.toSeq
                indices.zip(values).foreach {
                  case (index, value) => row.set(index, value)
                }

                row
            }
            copy(outputSchema = schema2, transforms = transforms :+ transform)
        }
    }
  }

  override def withOutput(name: String, selectors: Selector *)
                         (udf: UserDefinedFunction): Try[RowTransformBuilder] = {
    val index = outputSchema.fields.length

    RowUtil.createRowSelectors(outputSchema, selectors: _*)(udf).flatMap {
      rowSelectors =>
        outputSchema.withField(name, udf.outputTypes.head).map {
          schema2 =>
            val transform = (row: ArrayRow) => row.set(index, row.udfValue(rowSelectors: _*)(udf))
            copy(outputSchema = schema2, transforms = transforms :+ transform)
        }
    }
  }

  /** Transform an input row with the predetermined schema.
    *
    * @param row row to transform
    * @return transformed row
    */
  def transform(row: Row): ArrayRow = {
    val arr = new Array[Any](arraySize)
    row.toArray.copyToArray(arr)
    val arrRow = ArrayRow(arr)

    transforms.foldLeft(arrRow) {
      (r, transform) => transform(r)
    }
  }
}
