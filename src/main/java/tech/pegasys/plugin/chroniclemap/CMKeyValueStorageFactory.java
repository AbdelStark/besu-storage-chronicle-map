package tech.pegasys.plugin.chroniclemap;

import lombok.RequiredArgsConstructor;
import org.hyperledger.besu.plugin.services.BesuConfiguration;
import org.hyperledger.besu.plugin.services.MetricsSystem;
import org.hyperledger.besu.plugin.services.exception.StorageException;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorage;
import org.hyperledger.besu.plugin.services.storage.SegmentIdentifier;

import java.io.IOException;

@RequiredArgsConstructor
public class CMKeyValueStorageFactory
    implements org.hyperledger.besu.plugin.services.storage.KeyValueStorageFactory {
  private final Options options;

  @Override
  public String getName() {
    return "chronicle-map-storage";
  }

  @Override
  public KeyValueStorage create(
      final SegmentIdentifier segment,
      final BesuConfiguration configuration,
      final MetricsSystem metricsSystem)
      throws StorageException {
    try {
      return new CMKeyValueStorage(segment, configuration, options);
    } catch (final IOException e) {
      throw new StorageException(e);
    }
  }

  @Override
  public boolean isSegmentIsolationSupported() {
    return true;
  }

  @Override
  public void close() {}
}
