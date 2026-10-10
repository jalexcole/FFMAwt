package io.desktop.ffm.windows;
import java.lang.foreign.MemorySegment;
public class XDisplay {
    private final MemorySegment display;

    public XDisplay(MemorySegment display) {
        this.display = display;
    }

    public MemorySegment display() {
        return display;
    }
}
