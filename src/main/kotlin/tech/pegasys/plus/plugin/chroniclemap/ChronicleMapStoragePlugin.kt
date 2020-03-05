package tech.pegasys.plus.plugin.chroniclemap

import com.google.auto.service.AutoService
import mu.KotlinLogging
import org.hyperledger.besu.plugin.BesuContext
import org.hyperledger.besu.plugin.BesuPlugin

private val logger = KotlinLogging.logger {}

@AutoService(BesuPlugin::class)
class ChronicleMapStoragePlugin : BesuPlugin {
    override fun start() {
        logger.info { "Starting plugin." }
    }

    override fun register(context: BesuContext?) {
        logger.info { "Registering plugin." }
    }

    override fun stop() {
        logger.info { "Stopping plugin." }
    }
}