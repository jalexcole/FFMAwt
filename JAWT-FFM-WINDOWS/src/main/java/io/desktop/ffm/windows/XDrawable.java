package io.desktop.ffm.windows;
import java.lang.foreign.MemorySegment;
public class XDrawable {
    private final MemorySegment drawable;

    public XDrawable(MemorySegment drawable) {
        this.drawable = drawable;
    }

    public MemorySegment drawable() {
        return drawable;
    }
}
