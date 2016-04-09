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
    private String mText;
    private Layout mLayout;
    private int mTextHeight;
    private float mLineY;
    private int mContentWidth;
    private float mGapWidth;
    private int mLineLength;
    private float mBlankSpace;
    private float mGapWidthProportion;
    private int mLineSpace;

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JustifyTextView);
        mGapWidthProportion = ta.getFraction(ta.getIndex(R.styleable.JustifyTextView_gapwidth_proportion_of_fontsize)
                , 1, 1, 0.6f);
        mLineSpace = ta.getDimensionPixelSize(ta.getIndex(R.styleable.JustifyTextView_line_space), 1);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint = getPaint();
        mPaint.setColor(getCurrentTextColor());
        mPaint.drawableState = getDrawableState();

        mText = getText().toString();
        setText(mText);

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

        int firstLineStart = mLayout.getLineStart(0);
        int firstLineEnd = mLayout.getLineEnd(0);
        String line = mText.substring(firstLineStart, firstLineEnd);

        // 单个字符宽度
        float charWidth = getCharWidth(line);
        // 一行中分配的间隙总宽度
        mBlankSpace = mGapWidthProportion * charWidth;
        // view内容的宽度
        mContentWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        // 装满一行的字符个数
        mLineLength = (int) ((mContentWidth - mBlankSpace) / charWidth);
        // 字符与字符间的间隙
        mGapWidth = ((mContentWidth - charWidth * mLineLength) / (mLineLength - 1));

        drawLines(canvas, mText, mTextHeight, mGapWidth);
    }

    private float getCharWidth(String line) {
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            if (!isLetterOfEnglish(line.charAt(i)) && !isHalfPunctuation(line.charAt(i))) {
                return StaticLayout.getDesiredWidth(c, getPaint());
            }
        }
        return StaticLayout.getDesiredWidth(String.valueOf(line.charAt(0)), getPaint());
    }

    private void drawLines(Canvas canvas, String text, int textHeight, float gapWidth) {

        mLineY = getTextSize() + getPaddingTop();
        float x = getPaddingLeft();
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + gapWidth;
            if (x + cw - gapWidth > mContentWidth + getPaddingLeft()) {
                mLineY += textHeight;
                x = getPaddingLeft();
            } else if (x + cw > mContentWidth + getPaddingLeft()
                    && x - gapWidth + cw <= mContentWidth + getPaddingLeft()) {
                x -= gapWidth;
            }
        }
    }

    public static boolean isLetterOfEnglish(char c) {
        int count = (int) c;
        if (count >= 65 && count <= 90) {
            // A ~ Z
            return true;
        } else if (count >= 97 && count <= 122) {
            // a ~ z
            return true;
        } else if (count >= 48 && count <= 57) {
            // 0 ~ 9
            return true;
        }
        return false;
    }

    public static boolean isHalfPunctuation(char c) {
        int count = (int) c;
        if (count >= 33 && count <= 47) {
            // !~/
            return true;
        } else if (count >= 58 && count <= 64) {
            // :~@
            return true;
        } else if (count >= 91 && count <= 96) {
            // [~
            return true;
        } else if (count >= 123 && count <= 126) {
            // {~~
            return true;
        }
        return false;
    }
}
