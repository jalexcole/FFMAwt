package org.java.desktop.ffm;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

import org.java.desktop.ffm.api.DrawingSurfaceInfo;

/** FFM representation of {@code JAWT_FFM_DrawingSurfaceInfo}. */
public final class JawtDrawingSurfaceInfo implements DrawingSurfaceInfo {

    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.ADDRESS.withName("platformInfo"),
            ValueLayout.ADDRESS.withName("ds"),
            JawtRectangle.LAYOUT.withName("bounds"),
            ValueLayout.JAVA_INT.withName("clipSize"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("clip"))
            .withName("JAWT_FFM_DrawingSurfaceInfo");

    private static final long PLATFORM_INFO_OFFSET = offsetOf("platformInfo");
    private static final long DRAWING_SURFACE_OFFSET = offsetOf("ds");
    private static final long BOUNDS_OFFSET = offsetOf("bounds");
    private static final long CLIP_SIZE_OFFSET = offsetOf("clipSize");
    private static final long CLIP_OFFSET = offsetOf("clip");

    private final MemorySegment segment;

    private JawtDrawingSurfaceInfo(MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment, "segment");
        if (segment.byteSize() < LAYOUT.byteSize()) {
            throw new IllegalArgumentException("segment is too small for JAWT_FFM_DrawingSurfaceInfo");
        }
    }

    public static JawtDrawingSurfaceInfo ofAddress(MemorySegment address) {
        Objects.requireNonNull(address, "address");
        if (address.address() == 0) {
            return null;
        }
        return new JawtDrawingSurfaceInfo(address.reinterpret(LAYOUT.byteSize()));
    }

    public MemorySegment segment() {
        return segment;
    }

    public MemorySegment platformInfo() {
        return segment.get(ValueLayout.ADDRESS, PLATFORM_INFO_OFFSET);
    }

    public MemorySegment drawingSurfaceAddress() {
        return segment.get(ValueLayout.ADDRESS, DRAWING_SURFACE_OFFSET);
    }

    public JawtRectangle bounds() {
        return JawtRectangle.of(segment.asSlice(BOUNDS_OFFSET, JawtRectangle.LAYOUT.byteSize()));
    }

    public int clipSize() {
        return segment.get(ValueLayout.JAVA_INT, CLIP_SIZE_OFFSET);
    }

    public MemorySegment clipAddress() {
        return segment.get(ValueLayout.ADDRESS, CLIP_OFFSET);
    }

    private static long offsetOf(String field) {
        return LAYOUT.byteOffset(PathElement.groupElement(field));
    }
}
