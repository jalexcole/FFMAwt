module io.desktop.ffm {
    exports io.desktop.ffm.api;

    exports io.desktop.ffm to jawt_ffm_unix, jawt_ffm_macos;

    requires java.desktop;

    // uses org.java.desktop.ffm.JawtNativeDisplayPointer;
    uses io.desktop.ffm.JawtNativeDisplayPointerFactory;

}
