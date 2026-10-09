package io.desktop.ffm.macos;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

import io.desktop.ffm.JawtNativeDisplayPointer;
import io.desktop.ffm.macos.panama.ffm_awt_macos_h;

public class CALayer implements JawtNativeDisplayPointer {

    private final MemorySegment nativeDisplay;

    CALayer(MemorySegment nativeDisplay) {
        this.nativeDisplay = nativeDisplay;
    }

    @Override
    public MemorySegment nativeDisplay(SegmentAllocator allocator) {
        return nativeDisplay;
    }

    /**
     * Creates a component layer associated with this CALayer.
     * 
     * @note This component layer is held with the same lifetime as the CALayer.
     */
    public ComponentLayer componentLayer() {
        final var compenentLayerPtr = ffm_awt_macos_h.awt_ffm_component_layer(nativeDisplay);
        return new ComponentLayer(compenentLayerPtr);
    }

    public WindowLayer surfaceLayer() {
        final var surfaceLayerPtr = ffm_awt_macos_h.awt_ffm_window_layer(nativeDisplay);
        return new WindowLayer(surfaceLayerPtr);
    }

    @Override
    public String platform() {
        return "cocoa";
    }

}
