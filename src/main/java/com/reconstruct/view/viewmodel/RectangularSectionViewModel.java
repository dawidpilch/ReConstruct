package com.reconstruct.view.viewmodel;

public class RectangularSectionViewModel {
    public final AppendableProperty<Double> depthProperty = new PositiveDoubleAppendableProperty(100d, "Depth (mm)");
    public final AppendableProperty<Double> widthProperty = new PositiveDoubleAppendableProperty(100d, "Width (mm)");
}
