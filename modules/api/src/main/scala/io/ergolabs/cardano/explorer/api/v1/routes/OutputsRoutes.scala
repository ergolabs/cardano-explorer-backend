package io.ergolabs.cardano.explorer.api.v1.routes

import cats.effect.{Concurrent, ContextShift, Sync, Timer}
import cats.syntax.semigroupk._
import io.ergolabs.cardano.explorer.api.configs.RequestConfig
import io.ergolabs.cardano.explorer.api.v1.endpoints.OutputsEndpoints
import io.ergolabs.cardano.explorer.api.v1.services.Outputs
import io.ergolabs.cardano.explorer.api.v1.syntax._
import tofu.syntax.monadic._
import org.http4s.HttpRoutes
import sttp.tapir.server.http4s.{Http4sServerInterpreter, Http4sServerOptions}

final class OutputsRoutes[F[_]: Concurrent: ContextShift: Timer](requestConfig: RequestConfig)(implicit
  service: Outputs[F],
  opts: Http4sServerOptions[F, F]
) {

  private val endpoints = new OutputsEndpoints(requestConfig)

  private val interpreter = Http4sServerInterpreter(opts)

  def routes: HttpRoutes[F] =
    getUnspentIndexedR <+>
    searchUnspentR <+>
    getUnspentR <+>
    getUnspentByAddrR <+>
    getUnspentByPCredR <+>
    getUnspentByAssetR <+>
    getByOutRefR

  def getByOutRefR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getByOutRef)(ref => service.getByOutRef(ref).orNotFound(s"Output{ref=$ref}"))

  def getUnspentR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getUnspent) { paging =>
      service.getUnspent(paging).eject
    }

  def getUnspentIndexedR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getUnspentIndexed) { indexing =>
      service.getUnspent(indexing).eject
    }

  def getUnspentByAddrR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getUnspentByAddr) { case (addr, paging) =>
      for {
        test <- service.getUnspentByAddr(addr, paging)
        res <- service.getUnspentByAddr(addr, paging).eject
      } yield res
    }

  def getUnspentByPCredR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getUnspentByPCred) { case (pcred, paging) =>
      service.getUnspentByPCred(pcred, paging).eject
    }

  def getUnspentByAssetR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.getUnspentByAsset) { case (asset, paging) =>
      service.getUnspentByAsset(asset, paging).eject
    }

  def searchUnspentR: HttpRoutes[F] =
    interpreter.toRoutes(endpoints.searchUnspent) { case (paging, query) =>
      service.searchUnspent(query, paging).eject
    }
}

object OutputsRoutes {

  def make[F[_]: Concurrent: ContextShift: Timer](requestConfig: RequestConfig)(implicit
    service: Outputs[F],
    opts: Http4sServerOptions[F, F]
  ): HttpRoutes[F] =
    new OutputsRoutes(requestConfig).routes
}
