package io.desktop.ffm.api;

import java.lang.foreign.MemorySegment;

public interface DrawingSurfaceInfo {

	MemorySegment platformInfo();

	MemorySegment drawingSurfaceAddress();

	Rectangle bounds();

	int clipSize();

	MemorySegment clipAddress();
}
