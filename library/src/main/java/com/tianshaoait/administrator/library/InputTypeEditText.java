package com.tianshaoait.administrator.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;



/**
 * Created by Administrator on 2017/3/22.
 */

public class InputTypeEditText extends EditText {

    public String flag = "";
    private Drawable mRightDrawable;
    private boolean isHasFocus;

    public InputTypeEditText(Context context) {
        super(context);
        init();
    }

    public InputTypeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
               R.styleable.InputTypeEditText);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InputTypeEditText_inType) {
                flag = a.getString(attr);

            }
        }
        a.recycle();
        init();
    }

    public InputTypeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.InputTypeEditText);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.InputTypeEditText_inType) {
                flag = a.getString(attr);

            }
        }
        a.recycle();
        init();
    }

    private void init(){
        Drawable[] drawables=this.getCompoundDrawables();

        //取得right位置的Drawable
        //即我们在布局文件中设置的android:drawableRight
        mRightDrawable=drawables[2];

        //设置焦点变化的监听
        this.setOnFocusChangeListener(new FocusChangeListenerImpl());
        //设置EditText文字变化的监听
        this.addTextChangedListener(new TextWatcherImpl());
        //初始化时让右边clean图标不可见
        setClearDrawableVisible(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                boolean isClean =(event.getX() > (getWidth() - getTotalPaddingRight()))&&
                        (event.getX() < (getWidth() - getPaddingRight()));
                if (isClean) {
                    setText("");
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private class FocusChangeListenerImpl implements OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            isHasFocus=hasFocus;
            if (isHasFocus) {
                boolean isVisible=getText().toString().length()>=1;
                setClearDrawableVisible(isVisible);
            } else {
                setClearDrawableVisible(false);
            }
        }
    }

    //当输入结束后判断是否显示右边clean的图标
    private class TextWatcherImpl implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isVisible=getText().toString().length()>=1;
            setClearDrawableVisible(isVisible);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,int count) {

        }

    }

    //隐藏或者显示右边clean的图标
    protected void setClearDrawableVisible(boolean isVisible) {
        Drawable rightDrawable;
        if (isVisible) {
            rightDrawable = mRightDrawable;
        } else {
            rightDrawable = null;
        }
        //使用代码设置该控件left, top, right, and bottom处的图标
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],
                rightDrawable,getCompoundDrawables()[3]);
    }


    /**
     * 输入法
     *
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new mInputConnecttion(super.onCreateInputConnection(outAttrs),
                false, flag);
    }
}


class mInputConnecttion extends InputConnectionWrapper implements
        InputConnection {

    public mInputConnecttion(InputConnection target, boolean mutable, String Flag) {
        super(target, mutable);
        flag = Flag;
    }


    private static String[] textType = {"中文", "英文", "数字", "符号"};
    private static String[] textTypeFormat = {"[\u4e00-\u9fa5]+", "[a-zA-Z]+", "[0-9]+", "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]+"};
    private String flag = "";


    /**
     * 对输入的内容进行拦截
     *
     * @param text
     * @param newCursorPosition
     * @return
     */
    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        //  setInput(ed,text);
        // 汉字 "[\u4e00-\u9fa5]+"
        //  英文 "[a-zA-Z /]+"
        //  数字 "[0-9]"
        // 符号 "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"

        if (flag.contains(textType[0]) && flag.contains(textType[1]) && flag.contains(textType[2]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[2]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[1]) && flag.contains(textType[2])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[2])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[1]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[2]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[2]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[1]) && flag.contains(textType[2]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[2]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[1]) && flag.contains(textType[2]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[2]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[1])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[1])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[2])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[2])) {
                return false;
            }
        } else if (flag.contains(textType[0]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[0]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[1]) && flag.contains(textType[2])) {
            if (!text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[2])) {
                return false;
            }
        } else if (flag.contains(textType[1]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[1]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[2]) && flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[2]) && !text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        } else if (flag.contains(textType[0])) {
            if (!text.toString().matches(textTypeFormat[0])) {
                return false;
            }
        } else if (flag.contains(textType[1])) {
            if (!text.toString().matches(textTypeFormat[1])) {
                return false;
            }
        } else if (flag.contains(textType[2])) {
            if (!text.toString().matches(textTypeFormat[2])) {
                return false;
            }
        } else if (flag.contains(textType[3])) {
            if (!text.toString().matches(textTypeFormat[3])) {
                return false;
            }
        }

        return super.commitText(text, newCursorPosition);
    }


    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        return super.sendKeyEvent(event);
    }

    @Override
    public boolean setSelection(int start, int end) {
        return super.setSelection(start, end);
    }

}