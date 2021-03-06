package io.ergolabs.cardano.explorer.api.configs

import derevo.derive
import derevo.pureconfig.pureconfigReader
import tofu.logging.derivation.loggable

@derive(pureconfigReader, loggable)
final case class RequestConfig(maxLimitTransactions: Int, maxLimitOutputs: Int)
