package com.eyeem.chips;

import android.graphics.Point;
import android.text.Spannable;

public interface ILayoutCallback {
    Point getCursorPosition(int pos);

    int getLine(int pos);

    Spannable getSpannable();

    int getLineEnd(int line);

    int getLineHeight();
}
