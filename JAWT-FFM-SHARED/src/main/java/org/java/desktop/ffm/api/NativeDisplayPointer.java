package org.java.desktop.ffm.api;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import org.java.desktop.ffm.JawtNativeDisplayPointer;

public sealed interface NativeDisplayPointer permits JawtNativeDisplayPointer {
    
    /**
     * Returns the native display pointer for the current platform as a void pointer.
     *
     * @param allocator the segment allocator to use for memory allocation
     * @return the native display pointer as a MemorySegment
     */
    public MemorySegment nativeDisplay(SegmentAllocator allocator);

    public String platform();


}
