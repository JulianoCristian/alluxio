/*
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */

package alluxio.client.file.options;

import alluxio.Configuration;
import alluxio.Constants;
import alluxio.PropertyKey;
import alluxio.client.WriteType;
import alluxio.client.file.policy.FileWriteLocationPolicy;
import alluxio.client.file.policy.RoundRobinPolicy;
import alluxio.grpc.CreateFilePOptions;
import alluxio.security.authorization.Mode;
import alluxio.test.util.CommonUtils;
import alluxio.util.ModeUtils;
import alluxio.util.grpc.GrpcUtils;
import alluxio.wire.TtlAction;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Tests for the {@link CreateFileOptions} class.
 */
public final class CreateFileOptionsTest {
  private final long mDefaultBlockSizeBytes = Configuration.getBytes(
      PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT);
  private final int mDefaultWriteTier =
      Configuration.getInt(PropertyKey.USER_FILE_WRITE_TIER_DEFAULT);
  private final WriteType mDefaultWriteType = Configuration.getEnum(
      PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT, alluxio.client.WriteType.class);

  // TODO(calvin): Test location policy when a factory is created
  @Test
  public void defaults() {
    CreateFileOptions options = CreateFileOptions.defaults();
    Assert.assertTrue(options.isRecursive());
    Assert.assertEquals(mDefaultBlockSizeBytes, options.getBlockSizeBytes());
    Assert.assertEquals(Constants.NO_TTL, options.getTtl());
    Assert.assertEquals(TtlAction.DELETE, options.getTtlAction());
    Assert.assertEquals(mDefaultWriteTier, options.getWriteTier());
    Assert.assertEquals(mDefaultWriteType, options.getWriteType());
    Assert.assertEquals(ModeUtils.applyFileUMask(Mode.defaults()), options.getMode());
  }

  /**
   * Tests getting and setting fields.
   */
  @Test
  public void fields() {
    Random random = new Random();
    long blockSize = random.nextLong();
    FileWriteLocationPolicy policy = new RoundRobinPolicy();
    Mode mode = new Mode((short) random.nextInt());
    boolean recursive = random.nextBoolean();
    long ttl = random.nextLong();
    int writeTier = random.nextInt();
    WriteType writeType = WriteType.NONE;

    CreateFileOptions options = CreateFileOptions.defaults();
    options.setBlockSizeBytes(blockSize);
    options.setLocationPolicy(policy);
    options.setMode(mode);
    options.setRecursive(recursive);
    options.setTtl(ttl);
    options.setTtlAction(TtlAction.FREE);
    options.setWriteTier(writeTier);
    options.setWriteType(writeType);

    Assert.assertEquals(blockSize, options.getBlockSizeBytes());
    Assert.assertEquals(policy, options.getLocationPolicy());
    Assert.assertEquals(mode, options.getMode());
    Assert.assertEquals(recursive, options.isRecursive());
    Assert.assertEquals(ttl, options.getTtl());
    Assert.assertEquals(TtlAction.FREE, options.getTtlAction());
    Assert.assertEquals(writeTier, options.getWriteTier());
    Assert.assertEquals(writeType, options.getWriteType());
  }

  /**
   * Tests conversion to proto representation.
   */
  @Test
  public void toProto() {
    Random random = new Random();
    long blockSize = random.nextLong();
    FileWriteLocationPolicy policy = new RoundRobinPolicy();
    Mode mode = new Mode((short) random.nextInt());
    boolean recursive = random.nextBoolean();
    long ttl = random.nextLong();
    int writeTier = random.nextInt();
    WriteType writeType = WriteType.NONE;

    CreateFileOptions options = CreateFileOptions.defaults();
    options.setBlockSizeBytes(blockSize);
    options.setLocationPolicy(policy);
    options.setMode(mode);
    options.setRecursive(recursive);
    options.setTtl(ttl);
    options.setTtlAction(TtlAction.FREE);
    options.setWriteTier(writeTier);
    options.setWriteType(writeType);

    CreateFilePOptions protoOptions = GrpcUtils.toProto(options);
    Assert.assertEquals(blockSize, protoOptions.getBlockSizeBytes());
    Assert.assertEquals(recursive, protoOptions.getRecursive());
    Assert.assertEquals(writeType.isThrough(), protoOptions.getPersisted());
    Assert.assertEquals(mode.toShort(), protoOptions.getMode());
  }

  @Test
  public void equalsTest() throws Exception {
    CommonUtils.testEquals(CreateFileOptions.class);
  }
}
