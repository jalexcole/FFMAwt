package io.desktop.ffm.api;

public interface Jawt {

    public int version();

    public DrawingSurface getDrawingSurface();

    public void freeDrawingSurface(DrawingSurface surface);

    public void lock(DrawingSurface surface);

    public void unlock(DrawingSurface surface);

    public void getComponent(DrawingSurface surface);

    public void createEmbeddedFrame(DrawingSurface surface);

    public void setBounds(DrawingSurface surface, int x, int y, int width, int height);

    public void synthesizeWindowActivation(DrawingSurface surface, byte active);
}
