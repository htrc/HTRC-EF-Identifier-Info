package exceptions

object ServiceException {
  def apply(message: String, cause: Throwable = null): ServiceException = new ServiceException(message, cause)
}

class ServiceException(message: String, cause: Throwable = null) extends Exception(message, cause)
