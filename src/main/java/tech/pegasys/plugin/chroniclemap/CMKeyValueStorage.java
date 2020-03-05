package tech.pegasys.plugin.chroniclemap;

import com.google.common.collect.Sets;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.hyperledger.besu.plugin.services.BesuConfiguration;
import org.hyperledger.besu.plugin.services.exception.StorageException;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorage;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorageTransaction;
import org.hyperledger.besu.plugin.services.storage.SegmentIdentifier;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class CMKeyValueStorage implements KeyValueStorage {

  private final ChronicleMap<byte[], byte[]> db;

  public CMKeyValueStorage(
      final SegmentIdentifier segmentIdentifier,
      final BesuConfiguration besuConfiguration,
      final Options options)
      throws IOException {
    if (!besuConfiguration.getStoragePath().toFile().exists()
        && !besuConfiguration.getStoragePath().toFile().mkdirs()) {
      throw new StorageException("Cannot create storage directory.");
    }
    this.db =
        ChronicleMapBuilder.of(byte[].class, byte[].class)
            .name(segmentIdentifier.getName())
            .averageKeySize(options.getAverageKeySize())
            .averageValueSize(options.getAverageValueSize())
            .entries(options.getEntries())
            .createPersistedTo(
                besuConfiguration.getStoragePath().resolve(segmentIdentifier.getName()).toFile());
  }

  @Override
  public void clear() throws StorageException {
    db.clear();
  }

  @Override
  public boolean containsKey(byte[] key) throws StorageException {
    return db.containsKey(key);
  }

  @Override
  public Optional<byte[]> get(byte[] key) throws StorageException {
    return Optional.ofNullable(db.get(key));
  }

  @Override
  public long removeAllKeysUnless(Predicate<byte[]> retainCondition) throws StorageException {
    final AtomicLong removedKeys = new AtomicLong(0);
    db.keySet().stream()
        .filter(retainCondition.negate())
        .forEach(
            key -> {
              db.remove(key);
              removedKeys.incrementAndGet();
            });
    return removedKeys.get();
  }

  @Override
  public Set<byte[]> getAllKeysThat(Predicate<byte[]> returnCondition) {
    final Set<byte[]> returnedKeys = Sets.newIdentityHashSet();
    db.keySet().stream().filter(returnCondition).forEach(returnedKeys::add);
    return returnedKeys;
  }

  @Override
  public KeyValueStorageTransaction startTransaction() throws StorageException {
    return new CMKeyValueStorageTransaction(db);
  }

  @Override
  public void close() {
    db.close();
  }
}
