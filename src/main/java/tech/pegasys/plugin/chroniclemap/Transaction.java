package tech.pegasys.plugin.chroniclemap;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.besu.plugin.services.exception.StorageException;
import org.hyperledger.besu.plugin.services.storage.KeyValueStorageTransaction;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Transaction implements KeyValueStorageTransaction {
  private final PutAndRemove db;
  private final Queue<Consumer<PutAndRemove>> actions = new LinkedBlockingQueue<>();

  @Override
  public void put(byte[] key, byte[] value) {
    System.out.println(Hex.encodeHexString(key));
    System.out.println(Hex.encodeHexString(value));
    actions.add(chronicleMap -> chronicleMap.put(key, value));
  }

  @Override
  public void remove(byte[] key) {
    actions.add(chronicleMap -> chronicleMap.remove(key));
  }

  @Override
  public void commit() throws StorageException {
    while (!actions.isEmpty()) {
      actions.poll().accept(db);
    }
  }

  @Override
  public void rollback() {
    actions.clear();
  }

  public interface PutAndRemove {
    void put(byte[] key, byte[] value);

    void remove(byte[] key);
  }
}
