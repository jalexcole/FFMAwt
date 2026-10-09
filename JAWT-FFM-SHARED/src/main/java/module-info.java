module org.java.desktop.ffm {
    exports org.java.desktop.ffm.api;

    exports org.java.desktop.ffm to jawt_ffm_unix, jawt_ffm_macos;

    requires java.desktop;

    // uses org.java.desktop.ffm.JawtNativeDisplayPointer;
    uses org.java.desktop.ffm.JawtNativeDisplayPointerFactory;

}
