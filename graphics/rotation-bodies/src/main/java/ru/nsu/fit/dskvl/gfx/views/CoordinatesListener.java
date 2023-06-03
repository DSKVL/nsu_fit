package ru.nsu.fit.dskvl.gfx.views;

import java.util.function.Consumer;

public class CoordinatesListener {
    private Consumer<OutlineEditorPoint> action;
    public void updateActivePointPosition(OutlineEditorPoint point) {
        action.accept(point);
    }
    public void setAction(Consumer<OutlineEditorPoint> action) { this.action = action; }
}
