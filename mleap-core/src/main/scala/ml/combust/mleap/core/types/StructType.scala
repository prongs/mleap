package ml.combust.mleap.core.types

import java.io.PrintStream
import scala.collection.JavaConverters._

import scala.util.{Failure, Success, Try}

/**  Structured container (schema) for fields.
  * Operations include select, withField, and dropField.
  */
object StructType {
  val empty = StructType(Seq())

  def apply(): StructType = StructType(fields = Seq(),
    nameToIndex = Map(),
    nameToField = Map())

  def apply(field: StructField, fields: StructField *): Try[StructType] = apply(field +: fields)

  def apply(fields: Seq[StructField]): Try[StructType] = {
    StructType().withFields(fields)
  }

  def apply(fields: java.lang.Iterable[StructField]): Try[StructType] = {
    apply(fields.asScala.toSeq)
  }
}

/** Class for storing structured type information.
  *
  * This class is primarily used to define the schema of a LeapFrame.
  * In the future, it could be used to define the structure of fields stored in the LeapFrame itself.
  *
  * @param fields list of fields in this struct
  * @param nameToIndex lookup of field name to field index
  * @param nameToField lookup of field name to field
  */
case class StructType private(fields: Seq[StructField],
                              private val nameToIndex: Map[String, Int],
                              private val nameToField: Map[String, StructField])
  extends Serializable {
  /** Get optional field by name.
    *
    * @param name name of field
    * @return optional field value
    */
  def getField(name: String): Option[StructField] = nameToField.get(name)

  /** If the struct contains field or not.
    *
    * @param name name of field
    * @return true if this contains the field name, false otherwise
    */
  def hasField(name: String): Boolean = nameToIndex.contains(name)

  /** Try to add a field to this struct.
    *
    * @param name name of field
    * @param dataType data type of field
    * @return try new struct with field added
    */
  def withField(name: String, dataType: DataType): Try[StructType] = {
    withField(StructField(name, dataType))
  }

  /** Try to add a field to this struct.
    *
    * @param field field to add
    * @return try new struct with field added
    */
  def withField(field: StructField): Try[StructType] = {
    if(hasField(field.name)) {
      Failure(new IllegalArgumentException(s"struct already has field: ${field.name}"))
    } else {
      val key = field.name

      Success(StructType(fields = fields :+ field,
        nameToIndex = nameToIndex + (key -> fields.length),
        nameToField = nameToField + (key -> field)))
    }
  }

  /** Try to add multiple fields to this struct.
    *
    * @param field first field to add
    * @param fields fields to add
    * @return try new struct with fields added
    */
  def withFields(field: StructField, fields: StructField *): Try[StructType] = {
    withFields(field +: fields)
  }

  /** Try to add multiple fields to this struct.
    *
    * @param fields fields to add
    * @return try new struct with fields added
    */
  def withFields(fields: Seq[StructField]): Try[StructType] = {
    fields.foldLeft(Try(this)) {
      case (schema, field) => schema.flatMap(_.withField(field))
    }
  }

  /** Try to select fields to create a new struct.
    *
    * @param fieldNames names of fields to go into new struct
    * @return try new struct with selected fields
    */
  def select(fieldNames: String *): Try[StructType] = {
    indicesOf(fieldNames: _*).flatMap(selectIndices(_: _*))
  }

  /** Try to select fields by index to create a new struct.
    *
    * @param indices indices of the fields to select
    * @return try new struct with selected indices
    */
  def selectIndices(indices: Int *): Try[StructType] = {
    val invalid = indices.filter(_ >= fields.length)

    if(invalid.nonEmpty) {
      Failure(new IllegalArgumentException(s"invalid indices: ${invalid.mkString(",")}"))
    } else {
      val selection = indices.map(fields)
      StructType(selection)
    }
  }

  /** Try to select fields to create a new struct.
    *
    * @param fieldNames names of fields to go into new struct
    * @return try new struct with selected fields
    */
  def selectFields(fieldNames: String *): Try[Seq[StructField]] = {
    indicesOf(fieldNames: _*).flatMap {
      indices =>
        val invalid = indices.filter(_ >= fields.length)

        if(invalid.nonEmpty) {
          Failure(new IllegalArgumentException(s"invalid indices: ${invalid.mkString(",")}"))
        } else {
          Success(indices.map(fields))
        }
    }
  }

  /** Try to get indices for a list of field names.
    *
    * @param fieldNames names of fields
    * @return try list of indices for fields
    */
  def indicesOf(fieldNames: String *): Try[Seq[Int]] = {
    val invalid = fieldNames.filterNot(nameToIndex.contains)

    if(invalid.nonEmpty) {
      Failure(new IllegalArgumentException(s"invalid fields: ${invalid.mkString(",")}"))
    } else {
      Success(fieldNames.map(nameToIndex))
    }
  }

  /** Try to drop a field from the struct.
    *
    * @param name name of field to drop
    * @return try new struct without field
    */
  def dropField(name: String): Try[StructType] = {
    indexOf(name).flatMap(dropIndex)
  }

  /** Try to drop an index from the struct.
    *
    * @param index index of field to drop
    * @return try new struct without index
    */
  def dropIndex(index: Int): Try[StructType] = {
    if(index >= fields.length) {
      Failure(new IllegalArgumentException(s"invalid index: $index"))
    } else {
      StructType(fields.take(index) ++ fields.drop(index + 1))
    }
  }

  /** Try to get the index of a field.
    *
    * @param name name of field
    * @return try index of field
    */
  def indexOf(name: String): Try[Int] = {
    if(nameToIndex.contains(name)) {
      Success(nameToIndex(name))
    } else {
      Failure(new IllegalArgumentException(s"invalid field: $name"))
    }
  }

  /** Try to get the index and field for a field name.
    *
    * @param name name of field
    * @return try (index of field, field definition)
    */
  def indexedField(name: String): Try[(Int, StructField)] = {
    indexOf(name).map(index => (index, fields(index)))
  }

  /** Try to get the index and field for all field names.
    *
    * @param names names of fields
    * @return try (index of field, field definition)
    */
  def indexedFields(names: String *): Try[Seq[(Int, StructField)]] = {
    indicesOf(names: _*).map {
      indices =>
        indices.map {
          i => (i, fields(i))
        }
    }
  }

  /** Print schema to standard out.
    */
  def print(): Unit = print(System.out)

  /** Print schema to a PrintStream.
    *
    * @param out print stream to write schema to
    */
  def print(out: PrintStream): Unit = {
    out.println("root")
    fields.foreach {
      field => out.println(s" |-- ${field.name}: ${field.dataType.printString}")
    }
  }
}
