package io.desktop.ffm;

import java.lang.foreign.SegmentAllocator;

public interface JawtNativeDisplayPointerFactory {

    JawtNativeDisplayPointer fetch(SegmentAllocator allocator);

    public String windowingType();
}
