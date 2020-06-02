package com.eyeem.chips;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spannable;

import java.util.ArrayList;

public interface BubbleSpan {
    void setPressed(boolean value, Spannable s);

    void resetWidth(int width);

    ArrayList<Rect> rect(ILayoutCallback callback);

    void redraw(Canvas canvas);

    void setData(Object data);

    Object data();
}