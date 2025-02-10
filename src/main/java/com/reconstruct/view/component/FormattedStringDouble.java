package com.reconstruct.view.component;

public record FormattedStringDouble(double value) {
    @Override
    public String toString() {
        String formatted = String.format("%.2f", value);
        return formatted.endsWith(".00") ? formatted.split("\\.00")[0] : formatted;
    }
}
