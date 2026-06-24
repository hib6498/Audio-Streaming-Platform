package org.hib6498.audio_streaming.store;


public class StorageException extends RuntimeException {
  public StorageException(String failedToUploadFile) {
    super(failedToUploadFile);
  }
}
