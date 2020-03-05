package tech.pegasys.plugin.chroniclemap;

import net.openhft.chronicle.map.ChronicleMap;

public class CMKeyValueStorageTransaction extends Transaction {

  public CMKeyValueStorageTransaction(final ChronicleMap<byte[], byte[]> db) {
    super(
        new PutAndRemove() {
          @Override
          public void put(byte[] key, byte[] value) {
            db.put(key, value);
          }

          @Override
          public void remove(byte[] key) {
            db.remove(key);
          }
        });
  }
}
