package io.desktop.ffm.macos;

import java.lang.foreign.SegmentAllocator;

import org.java.desktop.ffm.JawtNativeDisplayPointer;
import org.java.desktop.ffm.JawtNativeDisplayPointerFactory;

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
