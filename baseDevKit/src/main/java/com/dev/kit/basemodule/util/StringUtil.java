package com.dev.kit.basemodule.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.math.BigDecimal;

/**
 * 字符串处理工具类
 * Created by cy on 2017/11/16.
 */

public class StringUtil {
    public static String NumericScaleByCeil(String numberValue, int scale) {
        return new BigDecimal(numberValue).setScale(scale, BigDecimal.ROUND_CEILING).toString();
    }

    public static String NumericScaleByFloor(String numberValue, int scale) {
        return new BigDecimal(numberValue).setScale(scale, BigDecimal.ROUND_FLOOR).toString();
    }

    public static SpannableString getSpannableString(String src, int color) {
        SpannableString spannableString = new SpannableString(src);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static SpannableString getSpannableString(String src, int startIndex, int color) {
        SpannableString spannableString = new SpannableString(src);
        spannableString.setSpan(new ForegroundColorSpan(color), startIndex, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
