sealed trait JValue

sealed abstract class AbstractJValue[A](value: A) extends NewType[A] with JValue

case class JString(value: String) extends AbstractJValue(value)

case class JNumber(value: Double) extends AbstractJValue(value)

case class JBoolean(value: Boolean) extends AbstractJValue(value)

case object JNull extends JValue

case class JObject(value: (JString, JValue)*) extends AbstractJValue(value)

case class JArray(value: JValue*) extends AbstractJValue(value)

object JValue {

  implicit def toJValue[A: JSON](a: A) = implicitly[JSON[A]].toJSON(a)

  lazy val renderJSON: JValue => String = {
    case JNumber(n) => n.shows
    case JString(s) => "\"%s\"".format(s)
    case JBoolean(b) => b.shows
    case JNull => "null"
    case JObject(o @ _*) => o.map(_.fold(renderJSON(_) + ": " + renderJSON(_))).mkString("{", ", ", "}")
    case JArray(a @ _*) => a.map(renderJSON).mkString("[", ", ", "]")
  }

  implicit def JValueShow: Show[JValue] = shows(renderJSON)

}

trait JSON[A] {

  def toJSON(a: A): JValue

}

object JSON {

  def apply[A](f: A => JValue): JSON[A] = new JSON[A] {
    def toJSON(a: A) = f(a)
  }

  implicit def NumericJSON[A: Numeric]: JSON[A] = apply((implicitly[Numeric[A]].toDouble _) >>> JNumber)

}
