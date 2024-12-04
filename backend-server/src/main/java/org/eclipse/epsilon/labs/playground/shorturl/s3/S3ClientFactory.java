package org.eclipse.epsilon.labs.playground.shorturl.s3;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Factory
public class S3ClientFactory {

  @Value("${playground.short.s3.region}")
  private String s3Region;

  @io.micronaut.runtime.context.scope.ThreadLocal
  public S3Client createS3Client() {
    Region region = Region.US_EAST_1;
    if (s3Region != null) {
      region = Region.of(s3Region);
    }
    return S3Client.builder().region(region).build();
  }

}
