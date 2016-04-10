package io.github.leibnik.justifytextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Droidroid on 2016/3/24.
 */
public class JustifyTextView extends TextView {

    private TextPaint mPaint;
    private Layout mLayout;
    // 行高
    private int mTextHeight;
    // 纵坐标
    private float mLineY;
    // view的内容宽度
    private int mContentWidth;
    // 字符间距
    private float mCharacterSpace;
    // 字符间距的一半
    private float mGapWidth;
    // 行间距
    private int mLineSpace;
    // 全角字符宽度
    private float mSBCwith;
    // 是否对齐
    private boolean isToAlignChars;

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JustifyTextView);
        mCharacterSpace = ta.getDimensionPixelSize(ta.getIndex(R.styleable.JustifyTextView_character_space)
                , 0);
        mLineSpace = ta.getDimensionPixelSize(ta.getIndex(R.styleable.JustifyTextView_line_space), 1);
        isToAlignChars = ta.getBoolean(ta.getIndex(R.styleable.JustifyTextView_align_chars), true);
        ta.recycle();
        mGapWidth = mCharacterSpace / 2;
        String text = getText().toString();
        if (text.charAt(text.length() - 1) == 10) {
            text = text.substring(0, text.length() - 1);
            setText(text);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());
        mPaint.drawableState = getDrawableState();

        String text = getText().toString();
        mLayout = getLayout();
        // layout.getLayout()在4.4.3出现NullPointerException
        if (mLayout == null) {
            return;
        }

        Paint.FontMetrics fm = mPaint.getFontMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTextHeight = (int) (Math.ceil(fm.descent - fm.ascent) + getLineSpacingExtra());
        } else {
            mTextHeight = (int) (Math.ceil(fm.descent - fm.ascent) + mLineSpace);
        }
        mTextHeight = (int) (mTextHeight * mLayout.getSpacingMultiplier() + mLayout.getSpacingAdd());

        // view内容的宽度
        mContentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        // 全角字符宽度
        mSBCwith = getSBCwidth(text);

        if (mSBCwith > 0) {
            if (isToAlignChars) {
                drawAligmentLines(canvas, text);
            } else {
                drawSBCLines(canvas, text);
            }
        } else {
            drawDBCLines(canvas, text);
        }
        canvas.restore();
    }

    private void drawSBCLines(Canvas canvas, String text) {

        mLineY = getTextSize() + getPaddingTop();
        float x = getPaddingLeft();
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            if (c.equals(" ")) {
                continue;
            }
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + mCharacterSpace;
            if (x + cw - mCharacterSpace > mContentWidth + getPaddingLeft() || c.equals("\n")) {
                mLineY += mTextHeight;
                x = getPaddingLeft();
            }
            // 一行中的最后一个字符能挤就挤，(●'◡'●)
            else if (x + cw > mContentWidth + getPaddingLeft()
                    && x - mCharacterSpace + cw <= mContentWidth + getPaddingLeft()) {
                x -= mCharacterSpace;
            }
        }
    }

    private void drawAligmentLines(Canvas canvas, String text) {

        mLineY = getTextSize() + getPaddingTop();
        float x = getPaddingLeft();
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            if (c.equals(" ")) {
                continue;
            }
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            float currentGapWidth = getGapWidth(text.charAt(i));
            canvas.drawText(c, x + currentGapWidth, mLineY, getPaint());
            if (i < text.length() - 1) {
                x += cw + 2 * currentGapWidth;
                float nextCw = StaticLayout.getDesiredWidth(String.valueOf(text.charAt(i + 1)), getPaint());
                float nextGapWidth = getGapWidth(text.charAt(i + 1));
                if (x + nextCw + 2 * nextGapWidth > mContentWidth + getPaddingLeft() || c.equals("\n")) {
                    mLineY += mTextHeight;
                    x = getPaddingLeft();
                }
            }
        }
    }

    private void drawDBCLines(Canvas canvas, String text) {
        mLineY = getTextSize() + getPaddingTop();
        float x = getPaddingLeft();

        int lineCount = mLayout.getLineCount();
        if (lineCount > 1) {
            for (int i = 0; i < lineCount; i++) {
                int lineStart = mLayout.getLineStart(i);
                int lineEnd = mLayout.getLineEnd(i);
                float lineWidth = mLayout.getLineWidth(i);
                String line = text.substring(lineStart, lineEnd);
                float wordSpace = (mContentWidth - lineWidth) / (getBlankCount(line) - 1);
                for (int j = 0; j < line.length(); j++) {
                    String c = String.valueOf(line.charAt(j));
                    float cw = StaticLayout.getDesiredWidth(c, getPaint());
                    canvas.drawText(c, x, mLineY, getPaint());
                    if (i < lineCount - 1 && line.charAt(j) == 32) {
                        x += cw + wordSpace;
                    } else {
                        x += cw;
                    }
                }
                mLineY += mTextHeight;
                x = getPaddingLeft();
            }
        } else {
            int lineStart = mLayout.getLineStart(0);
            int lineEnd = mLayout.getLineEnd(0);
            String line = text.substring(lineStart, lineEnd);
            for (int j = 0; j < line.length(); j++) {
                String c = String.valueOf(line.charAt(j));
                float cw = StaticLayout.getDesiredWidth(c, getPaint());
                canvas.drawText(c, x, mLineY, getPaint());
                x += cw;
            }
        }
    }

    private int getBlankCount(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == 32) {
                count++;
            }
        }
        return count;
    }

    private float getSBCwidth(String text) {
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            if (Character.UnicodeBlock.of(text.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return StaticLayout.getDesiredWidth(c, getPaint());
            }
        }
        return -1;
    }

    private float getGapWidth(char c) {
        float gapWidth = (mSBCwith - StaticLayout.getDesiredWidth(String.valueOf(c)
                , getPaint())) / 2 + mGapWidth;
        return gapWidth;
    }

}
