package tech.pegasys.plugin;

import com.google.auto.service.AutoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperledger.besu.plugin.BesuContext;
import org.hyperledger.besu.plugin.BesuPlugin;
import org.hyperledger.besu.plugin.services.PicoCLIOptions;
import org.hyperledger.besu.plugin.services.StorageService;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorageFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@AutoService(BesuPlugin.class)
public class ChronicleMapPlugin implements BesuPlugin {
  private static Logger LOG = LogManager.getLogger();
  private static String PLUGIN_NAME = "chronicle-map-storage";

  private BesuContext context;
  private final Object options = null;
  private KeyValueStorageFactory factory;

  @Override
  public void register(final BesuContext context) {
    LOG.info("Registering plugin {}.", PLUGIN_NAME);
    this.context = context;
    context
        .getService(PicoCLIOptions.class)
        .ifPresentOrElse(
            this::handleCLIOptions, () -> LOG.error("Could not obtain PicoCLIOptions service."));
    context
        .getService(StorageService.class)
        .ifPresentOrElse(
            this::createAndRegister, () -> LOG.error("Could not obtain Storage service."));
  }

  private void handleCLIOptions(final PicoCLIOptions cmdLineOptions) {
    cmdLineOptions.addPicoCLIOptions(PLUGIN_NAME, options);
  }

  @Override
  public void start() {
    LOG.info("Starting plugin {}.", PLUGIN_NAME);
    LOG.info(options.toString());
  }

  private void createAndRegister(final StorageService service) {
    factory = null;
    LOG.info("Registering redis key value storage factory");
    service.registerKeyValueStorage(factory);
  }

  @Override
  public CompletableFuture<Void> reloadConfiguration() {
    LOG.warn("Configuration reloaded is not supported");
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void stop() {
    LOG.info("Stopping plugin {}.", PLUGIN_NAME);
  }

  @Override
  public Optional<String> getName() {
    return Optional.of(PLUGIN_NAME);
  }
}
