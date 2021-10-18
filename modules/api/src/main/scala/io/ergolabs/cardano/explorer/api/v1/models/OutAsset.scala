package io.ergolabs.cardano.explorer.api.v1.models

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import sttp.tapir.Schema
import io.ergolabs.cardano.explorer.api.v1.instances._
import io.ergolabs.cardano.explorer.core.types.{Asset32, PolicyId}

@derive(encoder, decoder)
final case class OutAsset(policy: PolicyId, name: Asset32, quantity: BigInt)

object OutAsset {

  implicit def schema: Schema[OutAsset] =
    Schema
      .derived[OutAsset]
      .modify(_.policy)(_.description("The Asset policy hash."))
      .modify(_.name)(_.description("The Asset name."))
      .modify(_.quantity)(_.description("The Asset quantity."))

}
