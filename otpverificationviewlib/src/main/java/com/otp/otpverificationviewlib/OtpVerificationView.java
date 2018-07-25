package com.otp.otpverificationviewlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class OtpVerificationView extends View {

    StringBuilder stringBuilder;

    boolean inputType;

    private int mWidth;

    private int mHeight;

    //the code builder
    private StringBuilder codeBuilder;

    //text font
    private Typeface typeface = Typeface.DEFAULT;

    private OnTextChangListener listener;

    private int textColor = Color.CYAN;

    //how many words to show
    private int textCount = 4;

    //transparent line between solid lines
    private int blankLine;

    private int solidLine;

    //the paint to draw solid lines
    private Paint linePaint;

    //the paint to draw text
    private Paint textPaint;

    //solid line's width
    private int lineWidth = 5;

    private PointF[] solidPoints;

    public OtpVerificationView(Context context) {
        super(context);
        init(context, null);
    }

    public OtpVerificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public OtpVerificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OtpVerificationView(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                    .OtpVerificationView);
            textColor = typedArray.getColor(R.styleable.OtpVerificationView_OTPTextColor,
                    textColor);
            textCount = typedArray.getInt(R.styleable.OtpVerificationView_OTPTextCount, textCount);
            if (textCount < 2) throw new IllegalArgumentException("Text size must more than 1!");
            lineWidth = typedArray.getDimensionPixelSize(R.styleable
                            .OtpVerificationView_OTPLineWidth,
                    lineWidth);
            inputType = typedArray.getBoolean(R.styleable.OtpVerificationView_OTPInputTypePassword,
                    inputType);
            String font = typedArray.getString(R.styleable.OtpVerificationView_OTPFont);
            if (font != null)
                typeface = Typeface.createFromAsset(context.getAssets(), font);
            typedArray.recycle();
        }

        if (codeBuilder == null) {
            codeBuilder = new StringBuilder();
            stringBuilder = new StringBuilder();
        }
        for (int i = 0; i < textCount; i++)
            stringBuilder.append("*");

        linePaint = new Paint();
        linePaint.setColor(textColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(typeface);
        setFocusableInTouchMode(true); // allows the keyboard to pop up on
        // touch down
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (codeBuilder == null) codeBuilder = new StringBuilder();
        //67 is backspace,7-16 are 0-9
        if (keyCode == 67 && codeBuilder.length() > 0) {
            codeBuilder.deleteCharAt(codeBuilder.length() - 1);
            if (listener != null) {
                listener.afterTextChanged(codeBuilder.toString(), textCount);
            }
            invalidate();
        } else if (keyCode >= 7 && keyCode <= 16 && codeBuilder.length() < textCount) {
            codeBuilder.append(keyCode - 7);
            if (listener != null) {
                listener.afterTextChanged(codeBuilder.toString(), textCount);
            }
            invalidate();
        }
        //hide soft keyboard
        if (codeBuilder.length() >= textCount || keyCode == 66) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //define keyboard to number keyboard
        BaseInputConnection fic = new BaseInputConnection(this, false) {
            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                        sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
        };
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return fic;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        requestFocus();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.i(TAG,"onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            mWidth = ViewUtils.getWidth(getContext()) * 2 / 3;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = ViewUtils.getWidth(getContext()) / 4;
        }
        //calculate line's length
        blankLine = mWidth / (4 * textCount - 1);    //short one
        solidLine = mWidth / (4 * textCount - 1) * 3;  //long one

        if (textPaint != null)
            textPaint.setTextSize(solidLine);
        calculateStartAndEndPoint(textCount);
        setMeasuredDimension(mWidth, mHeight);
    }

    private void drawLine(Canvas canvas) {
        if (codeBuilder == null) return;
        int inputLength = codeBuilder.length();
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        //text's vertical center is view's center
        int baseLine = mHeight / 2 + (fontMetricsInt.bottom - fontMetricsInt.top) / 2 -
                fontMetricsInt.bottom;

        int mLinePosY = mHeight / 2;
        for (int i = 0; i < textCount; i++) {
            if (inputLength > i) {
                if (inputType) {
                    canvas.drawText(stringBuilder.toString(), i, i + 1, solidPoints[i].y -
                            solidLine / 2, baseLine, textPaint);
                } else
                    canvas.drawText(codeBuilder.toString(), i, i + 1, solidPoints[i].y -
                            solidLine / 2, baseLine, textPaint);
            } else {
                canvas.drawLine(solidPoints[i].x, mLinePosY, solidPoints[i].y, mLinePosY,
                        linePaint);
            }
        }
    }

    /**
     * get verify code string
     *
     * @return code
     */
    public String getText() {
        return codeBuilder != null ? codeBuilder.toString() : "";
    }

    /**
     * set verify code (must less than 4 letters)
     *
     * @param code code
     */
    public void setText(String code) {
        if (code == null)
            throw new NullPointerException("Code must not null!");
        if (code.length() > textCount) {
            throw new IllegalArgumentException(String.format("Code must less than %d letters!",
                    textCount));
        }
        codeBuilder = new StringBuilder();
        codeBuilder.append(code);
        invalidate();
    }

    /**
     * calculate every points
     *
     * @param textSize code length
     */
    private void calculateStartAndEndPoint(int textSize) {
        solidPoints = new PointF[textSize];
        for (int i = 1; i <= textSize; i++) {
            solidPoints[i - 1] = new PointF((i - 1) * blankLine + (i - 1) * solidLine, (i - 1) *
                    blankLine + i * solidLine);
        }
    }

    public void setListener(OnTextChangListener listener) {
        this.listener = listener;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorRes int textColor) {
        this.textColor = textColor;
    }

    /**
     * custom font
     *
     * @param typeface font
     */
    public void setFont(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * custom font
     *
     * @param path assets' path
     */
    public void setFont(String path) {
        typeface = Typeface.createFromAsset(getContext().getAssets(), path);
    }

    public interface OnTextChangListener {

        void afterTextChanged(String text, int size);
    }

}
