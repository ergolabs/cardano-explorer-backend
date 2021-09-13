package io.ergolabs.cardano.explorer.api.v1.endpoints

import io.ergolabs.cardano.explorer.api.v1.HttpError
import io.ergolabs.cardano.explorer.api.v1.models.Transaction
import io.ergolabs.cardano.explorer.core.types.TxHash
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody

class TransactionsEndpoints {

  val pathPrefix = "transactions"

  def getByTxHash: Endpoint[TxHash, HttpError, Transaction, Any] =
    baseEndpoint.get
      .in(pathPrefix / path[TxHash])
      .out(jsonBody[Transaction])
}