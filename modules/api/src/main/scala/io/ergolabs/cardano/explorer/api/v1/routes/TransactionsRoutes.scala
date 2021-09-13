package io.ergolabs.cardano.explorer.api.v1.routes

import cats.effect.{Concurrent, ContextShift, Timer}
import io.ergolabs.cardano.explorer.api.v1.endpoints.TransactionsEndpoints
import io.ergolabs.cardano.explorer.api.v1.services.Transactions
import io.ergolabs.cardano.explorer.api.v1.syntax._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

final class TransactionsRoutes[F[_]: Concurrent: ContextShift: Timer](implicit
  service: Transactions[F],
  opts: Http4sServerOptions[F, F]
) {

  private val endpoints = new TransactionsEndpoints
  import endpoints._

  private val interpreter = Http4sServerInterpreter(opts)

  def routes: HttpRoutes[F] = getByTxHashR

  def getByTxHashR: HttpRoutes[F] =
    interpreter.toRoutes(getByTxHash)(q => service.getByTxHash(q).orNotFound(s"Transaction{txHash=$q}"))
}

object TransactionsRoutes {

  def make[F[_]: Concurrent: ContextShift: Timer](implicit
    service: Transactions[F],
    opts: Http4sServerOptions[F, F]
  ): HttpRoutes[F] =
    new TransactionsRoutes().routes
}