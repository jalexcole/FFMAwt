package org.java.desktop.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

import org.java.desktop.ffm.api.Rectangle;

/** FFM representation of {@code JAWT_FFM_Rectangle}. */
public final class JawtRectangle implements Rectangle {

    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y"),
            ValueLayout.JAVA_INT.withName("width"),
            ValueLayout.JAVA_INT.withName("height"))
            .withName("JAWT_FFM_Rectangle");

    private static final long X_OFFSET = 0;
    private static final long Y_OFFSET = Integer.BYTES;
    private static final long WIDTH_OFFSET = 2L * Integer.BYTES;
    private static final long HEIGHT_OFFSET = 3L * Integer.BYTES;

    private final MemorySegment segment;

    private JawtRectangle(MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment, "segment");
        if (segment.byteSize() < LAYOUT.byteSize()) {
            throw new IllegalArgumentException("segment is too small for JAWT_FFM_Rectangle");
        }
    }

    public static JawtRectangle allocate(Arena arena) {
        return new JawtRectangle(Objects.requireNonNull(arena, "arena").allocate(LAYOUT));
    }

    public static JawtRectangle of(MemorySegment segment) {
        return new JawtRectangle(segment);
    }

    public MemorySegment segment() {
        return segment;
    }
    @Override
    public int x() {
        return segment.get(ValueLayout.JAVA_INT, X_OFFSET);
    }
    @Override
    public void x(int x) {
        segment.set(ValueLayout.JAVA_INT, X_OFFSET, x);
    }

    @Override
    public int y() {
        return segment.get(ValueLayout.JAVA_INT, Y_OFFSET);
    }
    @Override
    public void y(int y) {
        segment.set(ValueLayout.JAVA_INT, Y_OFFSET, y);
    }

    @Override
    public int width() {
        return segment.get(ValueLayout.JAVA_INT, WIDTH_OFFSET);
    }
    @Override
    public void width(int width) {
        segment.set(ValueLayout.JAVA_INT, WIDTH_OFFSET, width);
    }

    @Override
    public int height() {
        return segment.get(ValueLayout.JAVA_INT, HEIGHT_OFFSET);
    }
    @Override
    public void height(int height) {
        segment.set(ValueLayout.JAVA_INT, HEIGHT_OFFSET, height);
    }
}
