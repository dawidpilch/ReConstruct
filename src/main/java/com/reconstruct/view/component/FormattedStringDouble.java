package com.reconstruct.view.component;

public record FormattedStringDouble(double value) {
    @Override
    public String toString() {
        String formatted = String.format("%.3f", value);
        return formatted.endsWith(".000") ? formatted.split("\\.000")[0] : formatted;
    }
}
