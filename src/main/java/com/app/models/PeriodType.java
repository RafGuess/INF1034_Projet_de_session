package com.app.models;

import javafx.scene.paint.Color;

public class PeriodType {
    private String title;
    private Color color;

    public PeriodType(String title, Color color) {
        this.title = title;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getRGBColor() {
        return String.format("#%02X%02X%02X", (int)color.getRed(), (int)color.getGreen(), (int)color.getBlue());
    }
}
