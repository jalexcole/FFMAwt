package io.desktop.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.ServiceLoader;

import io.desktop.ffm.api.DrawingSurface;
import io.desktop.ffm.api.Jawt;

/**
 * FFM representation of the {@code JAWT_FFM} structure in {@code jawt_ffm.h}.
 */
public final class JawtFFM {


    private static final ServiceLoader<JawtNativeDisplayPointerFactory> SERVICE_LOADER = ServiceLoader.load(JawtNativeDisplayPointerFactory.class);

    public static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("version"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.ADDRESS.withName("GetDrawingSurface"),
            ValueLayout.ADDRESS.withName("FreeDrawingSurface"),
            ValueLayout.ADDRESS.withName("Lock"),
            ValueLayout.ADDRESS.withName("Unlock"),
            ValueLayout.ADDRESS.withName("GetComponent"),
            ValueLayout.ADDRESS.withName("CreateEmbeddedFrame"),
            ValueLayout.ADDRESS.withName("SetBounds"),
            ValueLayout.ADDRESS.withName("SynthesizeWindowActivation"))
            .withName("JAWT_FFM");

    public static final FunctionDescriptor GET_AWT_DESCRIPTOR = FunctionDescriptor.of(
            ValueLayout.JAVA_INT,
            ValueLayout.ADDRESS);

    public static final int VERSION_1_3 = 0x00010003;
    public static final int VERSION_1_4 = 0x00010004;
    public static final int VERSION_1_7 = 0x00010007;
    public static final int VERSION_9 = 0x00090000;

    public static final int LOCK_ERROR = 0x00000001;
    public static final int LOCK_CLIP_CHANGED = 0x00000002;
    public static final int LOCK_BOUNDS_CHANGED = 0x00000004;
    public static final int LOCK_SURFACE_CHANGED = 0x00000008;

    private static final String GET_AWT_SYMBOL = "JAWT_GetAWT_FFM";
    private static final long GET_DRAWING_SURFACE_OFFSET = offsetOf("GetDrawingSurface");
    private static final long FREE_DRAWING_SURFACE_OFFSET = offsetOf("FreeDrawingSurface");
    private static final long LOCK_OFFSET = offsetOf("Lock");
    private static final long UNLOCK_OFFSET = offsetOf("Unlock");
    private static final long GET_COMPONENT_OFFSET = offsetOf("GetComponent");
    private static final long CREATE_EMBEDDED_FRAME_OFFSET = offsetOf("CreateEmbeddedFrame");
    private static final long SET_BOUNDS_OFFSET = offsetOf("SetBounds");
    private static final long SYNTHESIZE_WINDOW_ACTIVATION_OFFSET = offsetOf("SynthesizeWindowActivation");

    private static final FunctionDescriptor GET_DRAWING_SURFACE_DESCRIPTOR = FunctionDescriptor.of(ValueLayout.ADDRESS,
            ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final FunctionDescriptor FREE_DRAWING_SURFACE_DESCRIPTOR = FunctionDescriptor
            .ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor LOCK_AWT_DESCRIPTOR = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
    private static final FunctionDescriptor GET_COMPONENT_DESCRIPTOR = FunctionDescriptor.of(ValueLayout.ADDRESS,
            ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final FunctionDescriptor CREATE_EMBEDDED_FRAME_DESCRIPTOR = FunctionDescriptor
            .of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
    private static final FunctionDescriptor SET_BOUNDS_DESCRIPTOR = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT,
            ValueLayout.JAVA_INT);
    private static final FunctionDescriptor SYNTHESIZE_WINDOW_ACTIVATION_DESCRIPTOR = FunctionDescriptor.ofVoid(
            ValueLayout.ADDRESS,
            ValueLayout.ADDRESS,
            ValueLayout.JAVA_BYTE);

    private final MemorySegment segment;

    private JawtFFM(MemorySegment segment) {
        this.segment = Objects.requireNonNull(segment, "segment");

        if (segment.byteSize() < LAYOUT.byteSize()) {
            throw new IllegalArgumentException("segment is too small for JAWT_FFM");
        }
    }

    public static JawtFFM allocate(Arena arena, int version) {
        Objects.requireNonNull(arena, "arena");

        JawtFFM jawt = new JawtFFM(arena.allocate(LAYOUT));
        jawt.version(version);
        return jawt;
    }

    public static JawtFFM of(MemorySegment segment) {
        return new JawtFFM(segment);
    }

    public MemorySegment segment() {
        return segment;
    }

    public int version() {
        return segment.get(ValueLayout.JAVA_INT, 0);
    }

    public void version(int version) {
        segment.set(ValueLayout.JAVA_INT, 0, version);
    }

    public static MethodHandle getAwtHandle(SymbolLookup symbols) {
        Objects.requireNonNull(symbols, "symbols");

        MemorySegment function = symbols.find(GET_AWT_SYMBOL)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Native symbol not found: " + GET_AWT_SYMBOL));
        return Linker.nativeLinker().downcallHandle(function, GET_AWT_DESCRIPTOR);
    }

    public static SymbolLookup loadLibrary() {
        System.loadLibrary("jawtffmjni");
        return SymbolLookup.loaderLookup();
    }

    public static boolean getAwt(SymbolLookup symbols, JawtFFM jawt) throws Throwable {
        Objects.requireNonNull(jawt, "jawt");
        return (int) getAwtHandle(symbols).invokeExact(jawt.segment) != 0;
    }

    public JawtDrawingSurface getDrawingSurface(MemorySegment env, MemorySegment target)
            throws Throwable {
        Objects.requireNonNull(env, "env");
        Objects.requireNonNull(target, "target");
        MemorySegment surface = (MemorySegment) downcall(
                GET_DRAWING_SURFACE_OFFSET,
                GET_DRAWING_SURFACE_DESCRIPTOR).invokeExact(env, target);
        return JawtDrawingSurface.ofAddress(surface);
    }

    public void freeDrawingSurface(JawtDrawingSurface surface) throws Throwable {
        Objects.requireNonNull(surface, "surface");
        downcall(FREE_DRAWING_SURFACE_OFFSET, FREE_DRAWING_SURFACE_DESCRIPTOR)
                .invokeExact(surface.segment());
    }

    public void lock(MemorySegment env) throws Throwable {
        Objects.requireNonNull(env, "env");
        downcall(LOCK_OFFSET, LOCK_AWT_DESCRIPTOR).invokeExact(env);
    }

    public void unlock(MemorySegment env) throws Throwable {
        Objects.requireNonNull(env, "env");
        downcall(UNLOCK_OFFSET, LOCK_AWT_DESCRIPTOR).invokeExact(env);
    }

    public MemorySegment component(MemorySegment env, MemorySegment platformInfo) throws Throwable {
        Objects.requireNonNull(env, "env");
        Objects.requireNonNull(platformInfo, "platformInfo");
        return (MemorySegment) downcall(GET_COMPONENT_OFFSET, GET_COMPONENT_DESCRIPTOR)
                .invokeExact(env, platformInfo);
    }

    public MemorySegment createEmbeddedFrame(MemorySegment env, MemorySegment platformInfo)
            throws Throwable {
        Objects.requireNonNull(env, "env");
        Objects.requireNonNull(platformInfo, "platformInfo");
        return (MemorySegment) downcall(
                CREATE_EMBEDDED_FRAME_OFFSET,
                CREATE_EMBEDDED_FRAME_DESCRIPTOR).invokeExact(env, platformInfo);
    }

    public void setBounds(MemorySegment env, MemorySegment embeddedFrame,
            int x, int y, int width, int height) throws Throwable {
        Objects.requireNonNull(env, "env");
        Objects.requireNonNull(embeddedFrame, "embeddedFrame");
        downcall(SET_BOUNDS_OFFSET, SET_BOUNDS_DESCRIPTOR)
                .invokeExact(env, embeddedFrame, x, y, width, height);
    }

    public void synthesizeWindowActivation(MemorySegment env, MemorySegment embeddedFrame,
            boolean activate) throws Throwable {
        Objects.requireNonNull(env, "env");
        Objects.requireNonNull(embeddedFrame, "embeddedFrame");
        byte doActivate = activate ? (byte) 1 : (byte) 0;
        downcall(SYNTHESIZE_WINDOW_ACTIVATION_OFFSET,
                SYNTHESIZE_WINDOW_ACTIVATION_DESCRIPTOR)
                .invokeExact(env, embeddedFrame, doActivate);
    }

    private MethodHandle downcall(long offset, FunctionDescriptor descriptor) {
        MemorySegment function = segment.get(ValueLayout.ADDRESS, offset);
        if (function.address() == 0) {
            throw new IllegalStateException("Requested JAWT operation is unavailable");
        }
        return Linker.nativeLinker().downcallHandle(function, descriptor);
    }

    private static long offsetOf(String field) {
        return LAYOUT.byteOffset(PathElement.groupElement(field));
    }

    public static JawtNativeDisplayPointerFactory getJawtNativeDisplayPointerFactory() {
        return SERVICE_LOADER.findFirst().orElseThrow(() -> new IllegalStateException("No JawtNativeDisplayPointer implementation found"));
    }

    public static Jawt getAwt(SegmentAllocator allocator, int version) {
        return new Jawt() {
            private final int jawtVersion = version;

            @Override
            public int version() {
                return jawtVersion;
            }

            @Override
            public DrawingSurface getDrawingSurface() {
                throw new UnsupportedOperationException("Unimplemented method 'getDrawingSurface'");
            }

            @Override
            public void freeDrawingSurface(DrawingSurface surface) {
                throw new UnsupportedOperationException("Unimplemented method 'freeDrawingSurface'");
            }

            @Override
            public void lock(DrawingSurface surface) {
                throw new UnsupportedOperationException("Unimplemented method 'lock'");
            }

            @Override
            public void unlock(DrawingSurface surface) {
                throw new UnsupportedOperationException("Unimplemented method 'unlock'");
            }

            @Override
            public void getComponent(DrawingSurface surface) {
                throw new UnsupportedOperationException("Unimplemented method 'getComponent'");
            }

            @Override
            public void createEmbeddedFrame(DrawingSurface surface) {
                throw new UnsupportedOperationException("Unimplemented method 'createEmbeddedFrame'");
            }

            @Override
            public void setBounds(DrawingSurface surface, int x, int y, int width, int height) {
                throw new UnsupportedOperationException("Unimplemented method 'setBounds'");
            }

            @Override
            public void synthesizeWindowActivation(DrawingSurface surface, byte active) {
                throw new UnsupportedOperationException("Unimplemented method 'synthesizeWindowActivation'");
            }

        };
    }
    

}
