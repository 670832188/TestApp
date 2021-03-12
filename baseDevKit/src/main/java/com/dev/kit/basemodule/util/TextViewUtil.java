package com.dev.kit.basemodule.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.widget.TextView;

/**
 * Created by cuiyan on 2018/11/9.
 */
public class TextViewUtil {
    /**
     * 增大文本水平间距
     *
     * @param view        TextView | Button | EditText
     * @param letterSpace 字间距 [-0.5, 4F] 之间较为合适， 精度为 0.001F
     */
    public static void addLetterSpacing(TextView view, float letterSpace) {
        if ((view == null)) {
            return;
        }
        addLetterSpacing(view, view.getText().toString(), letterSpace);
    }

    /**
     * 增大文本水平间距
     *
     * @param view        TextView | Button | EditText
     * @param letterSpace 字间距 [-0.5, 4F] 之间较为合适， 精度为 0.001F
     */
    public static void addLetterSpacing(TextView view, String text, float letterSpace) {
        if ((view == null) || (text == null)) {
            return;
        }
        if (letterSpace == 0F) {
            letterSpace = 0.001F;
        }
        /*
         * 先把 String 拆成 字符数组，在每个字符后面添加一个空格，并对这个进来的空格进行 X轴上 缩放
         */
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            builder.append(text.charAt(i));
            if (i + 1 < text.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        for (int i = 1; (builder.toString().length() > 1) && (i < builder.toString().length()); i += 2) {
            finalText.setSpan(new ScaleXSpan(letterSpace), i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        view.setText(finalText, TextView.BufferType.SPANNABLE);
    }

    public static SpannableString getLetterSpacingString(String text, float letterSpace) {
        if (letterSpace == 0F) {
            /* 0 没有效果， 0.001F是最接近0 的 数了，在小一些，也就没有效果了 */
            letterSpace = 0.001F;
        }
        /*
         * 先把 String 拆成 字符数组，在每个字符后面添加一个空格，并对这个进来的空格进行 X轴上 缩放
         */
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            builder.append(text.charAt(i));
            if (i + 1 < text.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        for (int i = 1; (builder.toString().length() > 1) && (i < builder.toString().length()); i += 2) {
            finalText.setSpan(new ScaleXSpan(letterSpace), i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return finalText;
    }
}
