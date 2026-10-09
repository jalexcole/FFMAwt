package org.java.desktop.ffm;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.Objects;

import org.java.desktop.ffm.api.DrawingSurface;
import org.java.desktop.ffm.api.DrawingSurfaceInfo;

/** FFM representation of {@code JAWT_FFM_DrawingSurface}. */
public final class JawtDrawingSurface implements DrawingSurface {

    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.ADDRESS.withName("env"),
            ValueLayout.ADDRESS.withName("target"),
            ValueLayout.ADDRESS.withName("Lock"),
            ValueLayout.ADDRESS.withName("GetDrawingSurfaceInfo"),
            ValueLayout.ADDRESS.withName("FreeDrawingSurfaceInfo"),
            ValueLayout.ADDRESS.withName("Unlock"))
            .withName("JAWT_FFM_DrawingSurface");

    private static final long LOCK_OFFSET = offsetOf("Lock");
    private static final long GET_INFO_OFFSET = offsetOf("GetDrawingSurfaceInfo");
    private static final long FREE_INFO_OFFSET = offsetOf("FreeDrawingSurfaceInfo");
    private static final long UNLOCK_OFFSET = offsetOf("Unlock");

    private static final FunctionDescriptor LOCK_DESCRIPTOR = FunctionDescriptor.of(
            ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
    private static final FunctionDescriptor GET_INFO_DESCRIPTOR = FunctionDescriptor.of(
            ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final FunctionDescriptor FREE_INFO_DESCRIPTOR = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS);

    private final MemorySegment segment;

    private JawtDrawingSurface(MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment, "segment");
        if (segment.byteSize() < LAYOUT.byteSize()) {
            throw new IllegalArgumentException("segment is too small for JAWT_FFM_DrawingSurface");
        }
    }

    public static JawtDrawingSurface ofAddress(MemorySegment address) {
        Objects.requireNonNull(address, "address");
        if (address.address() == 0) {
            return null;
        }
        return new JawtDrawingSurface(address.reinterpret(LAYOUT.byteSize()));
    }

    public MemorySegment segment() {
        return segment;
    }

    @Override
    public int lock() throws Throwable {
        return (int) downcall(LOCK_OFFSET, LOCK_DESCRIPTOR).invokeExact(segment);
    }

    @Override
    public DrawingSurfaceInfo drawingSurfaceInfo() throws Throwable {
        MemorySegment info = (MemorySegment) downcall(GET_INFO_OFFSET, GET_INFO_DESCRIPTOR)
                .invokeExact(segment);
        return JawtDrawingSurfaceInfo.ofAddress(info);
    }

    @Override
    public void freeDrawingSurfaceInfo(DrawingSurfaceInfo info) throws Throwable {
        if (!(Objects.requireNonNull(info, "info") instanceof JawtDrawingSurfaceInfo jawtInfo)) {
            throw new IllegalArgumentException("info must be a JawtDrawingSurfaceInfo");
        }
        downcall(FREE_INFO_OFFSET, FREE_INFO_DESCRIPTOR).invokeExact(jawtInfo.segment());
    }

    @Override
    public void unlock() throws Throwable {
        downcall(UNLOCK_OFFSET, FREE_INFO_DESCRIPTOR).invokeExact(segment);
    }

    private MethodHandle downcall(long offset, FunctionDescriptor descriptor) {
        MemorySegment function = segment.get(ValueLayout.ADDRESS, offset);
        if (function.address() == 0) {
            throw new IllegalStateException("JAWT drawing-surface function is unavailable");
        }
        return Linker.nativeLinker().downcallHandle(function, descriptor);
    }

    private static long offsetOf(String field) {
        return LAYOUT.byteOffset(PathElement.groupElement(field));
    }
}
