package com.github.lisicnu.libDroid.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p/>
 * <p/>
 * Author: Eden Lee<p/>
 * Date: 2014/11/24 <p/>
 * Email: checkway@outlook.com <p/>
 * Version: 1.0 <p/>
 */
public class FlushedInputStream extends FilterInputStream {

    public FlushedInputStream(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public long skip(long n) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < n) {
            long bytesSkipped = in.skip(n - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                // EOF check. less than 0.
                int bytesRead = read();
                if (bytesRead < 0) {
                    break; // we reached EOF
                } else {
                    // Since we read one byte we have actually
                    // skipped that byte hence bytesSkipped = 1
                    bytesSkipped = 1; // we read one byte
                }
            }
            // Adding the bytesSkipped to totalBytesSkipped
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}

