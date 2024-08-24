package com.example.braille_sync.Helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import androidx.appcompat.widget.AppCompatEditText;

public class NoEnterForEdittext extends AppCompatEditText {

    public NoEnterForEdittext(Context context) {
        super(context);
    }

    public NoEnterForEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoEnterForEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return true; // Consume the Enter key event to prevent new lines
        }
        return super.onKeyPreIme(keyCode, event);
    }

}
