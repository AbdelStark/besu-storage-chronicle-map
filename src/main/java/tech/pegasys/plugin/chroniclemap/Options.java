package tech.pegasys.plugin.chroniclemap;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import picocli.CommandLine;

@Builder
@Getter
@ToString
public class Options {
  @CommandLine.Option(names = "--plugin-chronicle-map-storage-average-key-size")
  @Builder.Default
  private Integer averageKeySize = 32;

  @CommandLine.Option(names = "--plugin-chronicle-map-storage-average-value-size")
  @Builder.Default
  private Integer averageValueSize = 32;

  @CommandLine.Option(names = "--plugin-chronicle-map-storage-entries")
  @Builder.Default
  private Integer entries = 1000000;
}
