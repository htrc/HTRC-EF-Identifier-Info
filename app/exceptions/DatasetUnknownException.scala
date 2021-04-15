package exceptions

case class DatasetUnknownException(name: String) extends ServiceException(name)
