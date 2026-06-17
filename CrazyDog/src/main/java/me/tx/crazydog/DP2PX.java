package me.tx.crazydog;

import android.content.Context;
import android.util.TypedValue;

public class DP2PX {
    /**
     * dp 转 px
     */
    public static int get(Context context, float dp) {
        if (context == null) return 0;
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

}
