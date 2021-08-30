package com.zf1976.mayi.common.core.compressors.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ant
 * Create by Ant on 2021/7/31 9:07 PM
 */
public class CountingInputStream extends FilterInputStream {
    private long bytesRead;

    public CountingInputStream(final InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        final int r = in.read();
        if (r >= 0) {
            count(1);
        }
        return r;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        final int r = in.read(b, off, len);
        if (r >= 0) {
            count(r);
        }
        return r;
    }

    /**
     * Increments the counter of already read bytes.
     * Doesn't increment if the EOF has been hit (read == -1)
     *
     * @param read the number of bytes read
     */
    protected final void count(final long read) {
        if (read != -1) {
            bytesRead += read;
        }
    }

    /**
     * Returns the current number of bytes read from this stream.
     *
     * @return the number of read bytes
     */
    public long getBytesRead() {
        return bytesRead;
    }
}