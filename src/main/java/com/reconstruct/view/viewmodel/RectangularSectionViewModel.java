package com.reconstruct.view.viewmodel;

public class RectangularSectionViewModel {
    public final AppendableProperty<Double> depthProperty = new PositiveDoubleAppendableProperty(450d, "Depth (mm)");
    public final AppendableProperty<Double> widthProperty = new PositiveDoubleAppendableProperty(250d, "Width (mm)");
}
