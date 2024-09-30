package jvn;

public enum LockState {
    /**
     * No Lock
     */
    NONE,
    /**
     * Read cached
     */
    READ_CACHED,
    /**
     * Writed cahed
     */
    WRITE_CACHED,
    READ,
    WRITE,
    READ_WRITE_CACHED;

    boolean canRead() {
        return this == READ || this == READ_CACHED || this == READ_WRITE_CACHED;
    }

    boolean canWrite() {
        return this == WRITE || this == WRITE_CACHED || this == READ_WRITE_CACHED;
    }
}