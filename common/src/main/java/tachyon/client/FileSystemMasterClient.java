/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.Constants;
import tachyon.MasterClient;
import tachyon.conf.TachyonConf;
import tachyon.thrift.DependencyInfo;
import tachyon.thrift.FileBlockInfo;
import tachyon.thrift.FileDoesNotExistException;
import tachyon.thrift.FileInfo;
import tachyon.thrift.FileSystemMasterService;
import tachyon.thrift.InvalidPathException;

/**
 * The FileSystemMaster client, for clients.
 *
 * Since thrift clients are not thread safe, this class is a wrapper to provide thread safety.
 */
// TODO: better deal with exceptions.
public final class FileSystemMasterClient extends MasterClient {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  private FileSystemMasterService.Client mClient = null;

  // TODO: implement client heartbeat to the master
  private Future<?> mHeartbeat;

  public FileSystemMasterClient(InetSocketAddress masterAddress, ExecutorService executorService,
      TachyonConf tachyonConf) {
    super(masterAddress, executorService, tachyonConf);
  }

  @Override
  protected String getServiceName() {
    return Constants.FILE_SYSTEM_MASTER_SERVICE_NAME;
  }

  @Override
  protected void afterConnect() {
    mClient = new FileSystemMasterService.Client(mProtocol);
    // TODO: get a user id?
    // TODO: start client heartbeat thread, and submit it to the executor service
  }

  @Override
  protected void afterDisconnect() {
    // TODO: implement heartbeat cleanup
  }

  public synchronized long getFileId(String path) throws IOException {
    while (!mIsClosed) {
      connect();
      try {
        return mClient.getFileId(path);
      } catch (InvalidPathException e) {
        throw new IOException(e);
      } catch (TException e) {
        LOG.error(e.getMessage(), e);
        mConnected = false;
      }
    }
    throw new IOException("This connection has been closed.");
  }

  public synchronized FileInfo getFileInfo(long fileId) throws IOException {
    while (!mIsClosed) {
      connect();
      try {
        return mClient.getFileInfo(fileId);
      } catch (FileDoesNotExistException e) {
        throw new IOException(e);
      } catch (TException e) {
        LOG.error(e.getMessage(), e);
        mConnected = false;
      }
    }
    throw new IOException("This connection has been closed.");
  }

  public synchronized FileBlockInfo getFileBlockInfo(long fileId, int fileBlockIndex)
      throws IOException {
    return null;
  }

  public synchronized List<FileBlockInfo> getFileBlockInfoList(long fileId) throws IOException {
    return null;
  }

  public synchronized long getUserId() throws IOException {
    return -1;
  }

  public synchronized long getNewBlockIdForFile(long fileId) throws IOException {
    return -1;
  }

  public synchronized String getUfsAddress() throws IOException {
    return null;
  }

  public synchronized long createFile(String fileId, long blockSizeBytes, boolean recursive)
      throws IOException {
    return -1;
  }

  public synchronized long loadFileFromUfs(long fileId, String ufsPath, boolean recursive)
      throws IOException {
    return -1;
  }

  public synchronized void completeFile(long fileId) throws IOException {

  }

  public synchronized boolean deleteFile(long fileId, boolean recursive) throws IOException {
    return false;
  }

  public synchronized boolean renameFile(long fileId, String dstPath) throws IOException {
    return false;
  }

  public synchronized void setPinned(long fileId, boolean pinned) throws IOException {

  }

  public synchronized boolean createDirectory(long fileId, boolean recursive) throws IOException {
    return false;
  }

  public synchronized boolean freePath(long fileId, boolean recursive) throws IOException {
    return false;
  }

  public synchronized boolean addCheckpoint(long workerId, long fileId, long length,
      String checkpointPath) throws IOException {
    return false;
  }

  public synchronized void userHeartbeat() throws IOException {

  }

  public synchronized int user_createDependency(List<String> parents, List<String> children,
      String commandPrefix, List<ByteBuffer> data, String comment, String framework,
      String frameworkVersion, int dependencyType, long childrenBlockSizeByte) throws IOException {
    return -1;
  }

  public synchronized DependencyInfo getDependencyInfo(int dependencyId) throws IOException {
    return null;
  }

  public synchronized void reportLostFile(long fileId) throws IOException {

  }

  public synchronized void requestFilesInDependency(int depId) throws IOException {

  }
}
