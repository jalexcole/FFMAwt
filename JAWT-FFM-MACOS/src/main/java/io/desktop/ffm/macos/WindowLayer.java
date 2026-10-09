package io.desktop.ffm.macos;

import java.lang.foreign.MemorySegment;

/// Convert to value class
public class WindowLayer {

    private final MemorySegment layer;

    WindowLayer(MemorySegment layer) {
        this.layer = layer;
    }

    public MemorySegment layer() {
        return layer;
    }

}
