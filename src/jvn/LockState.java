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
    READING,
    WRITING,
    READ_WRITE_CACHED;

    boolean canRead() {
        return this == READING || this == READ_CACHED || this == READ_WRITE_CACHED;
    }

    boolean canWrite() {
        return this == WRITING || this == WRITE_CACHED || this == READ_WRITE_CACHED;
    }
}