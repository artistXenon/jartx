package moe.artx.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConcatenatedInputStream extends InputStream {
    protected boolean skipInvalidStream = false;
    protected int streamIndex = 0;
    final protected List<InputStream> streamList = new ArrayList<>();

    @Override
    public int read() throws IOException {
        if (streamList.size() == streamIndex) return -1;
        InputStream currentStream = streamList.get(streamIndex);
        if (currentStream == null) {
            if (!skipInvalidStream) throw new IOException();
            ++streamIndex;
            return read();
        }
        int read = currentStream.read();
        if (read == -1) {
            ++streamIndex;
            return read();
        }
        return read;
    }

    @Override
    public int available() throws IOException {
        if (streamList.size() == streamIndex) return 0;
        int availableCount = 0;
        for (int counterIndex = streamIndex; counterIndex < streamList.size(); ++counterIndex)
            availableCount += streamList.get(streamIndex).available();

        return availableCount;
    }

    public boolean isSkipInvalidStream() {
        return skipInvalidStream;
    }

    public void setSkipInvalidStream(boolean skipInvalidStream) {
        this.skipInvalidStream = skipInvalidStream;
    }

    public void concatInputStream(InputStream inputStream) {
        streamList.add(inputStream);
    }
}
