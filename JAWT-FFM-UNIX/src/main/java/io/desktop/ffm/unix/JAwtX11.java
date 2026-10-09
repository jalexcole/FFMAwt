package io.desktop.ffm.unix;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import org.java.desktop.ffm.JawtNativeDisplayPointer;

public class JAwtX11 implements JawtNativeDisplayPointer {

    private final MemorySegment nativeDisplayPointer;

    public JAwtX11(MemorySegment nativeDisplayPointer) {
        this.nativeDisplayPointer = nativeDisplayPointer;
    }

    public MemorySegment drawable() {
        throw new UnsupportedOperationException("drawable() is not implemented yet");
    }

	@Override
	public MemorySegment nativeDisplay(SegmentAllocator allocator) {
		return nativeDisplayPointer;
	}

	@Override
	public String platform() {
		return "X11";
	}
}
