package co.siempo.phone.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;

import co.siempo.phone.R;


public class DrawableProvider {

    private final Context mContext;

    public DrawableProvider(Context context) {
        mContext = context;
    }

    public TextDrawable getRound(String text, int color, int fontSize) {
        return TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.DEFAULT)
                .fontSize(toPx(fontSize))
                .textColor(color)
                .bold()
                .endConfig()
                .buildRound(text, R.color.white);
    }

    public int toPx(int dp) {
        Resources resources = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
