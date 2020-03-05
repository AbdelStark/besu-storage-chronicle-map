package tech.pegasys.plugin.chroniclemap;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.besu.plugin.BesuContext;
import org.hyperledger.besu.plugin.BesuPlugin;
import org.hyperledger.besu.plugin.services.PicoCLIOptions;
import org.hyperledger.besu.plugin.services.StorageService;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorageFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@AutoService(BesuPlugin.class)
@Slf4j
public class Plugin implements BesuPlugin {
  private static String PLUGIN_NAME = "chronicle-map-storage";

  private BesuContext context;
  private final Options options = Options.builder().build();
  private KeyValueStorageFactory factory;

  @Override
  public void register(final BesuContext context) {
    log.info("Registering plugin {}.", PLUGIN_NAME);
    this.context = context;
    context
        .getService(PicoCLIOptions.class)
        .ifPresentOrElse(
            this::handleCLIOptions, () -> log.error("Could not obtain PicoCLIOptions service."));
    context
        .getService(StorageService.class)
        .ifPresentOrElse(
            this::createAndRegister, () -> log.error("Could not obtain Storage service."));
  }

  private void handleCLIOptions(final PicoCLIOptions cmdLineOptions) {
    cmdLineOptions.addPicoCLIOptions(PLUGIN_NAME, options);
  }

  @Override
  public void start() {
    log.info("Starting plugin {}.", PLUGIN_NAME);
  }

  private void createAndRegister(final StorageService service) {
    factory = new CMKeyValueStorageFactory(options);
    log.info("Registering chronicle map key value storage factory");
    service.registerKeyValueStorage(factory);
  }

  @Override
  public CompletableFuture<Void> reloadConfiguration() {
    log.warn("Configuration reloaded is not supported");
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void stop() {
    log.info("Stopping plugin {}.", PLUGIN_NAME);
    if (factory != null) {
      try {
        factory.close();
      } catch (IOException e) {
        log.error("Failed to stop plugin: {}", e.getMessage(), e);
      }
    }
  }

  @Override
  public Optional<String> getName() {
    return Optional.of(PLUGIN_NAME);
  }
}
