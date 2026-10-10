package io.desktop.ffm.windows;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import io.desktop.ffm.JawtNativeDisplayPointer;

public class WindowsNativeDisplayPointer implements JawtNativeDisplayPointer {

    private final MemorySegment nativeDisplayPointer;

    public WindowsNativeDisplayPointer(MemorySegment nativeDisplayPointer) {
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
		return "WINDOWS";
	}
}
