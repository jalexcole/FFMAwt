package io.desktop.ffm.macos;

import java.lang.foreign.SegmentAllocator;

import io.desktop.ffm.JawtNativeDisplayPointer;
import io.desktop.ffm.JawtNativeDisplayPointerFactory;

public class CALayerFactory implements JawtNativeDisplayPointerFactory {

	@Override
	public JawtNativeDisplayPointer fetch(SegmentAllocator allocator) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'fetch'");
	}

	@Override
	public String windowingType() {
		return "cocoa";
	}
    
}
