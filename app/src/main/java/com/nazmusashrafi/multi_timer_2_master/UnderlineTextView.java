package com.nazmusashrafi.multi_timer_2_master;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;


import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created with IntelliJ IDEA.
 * User: Justin
 * Date: 9/11/13
 * Time: 1:10 AM
 */
public class UnderlineTextView extends AppCompatTextView
{
    private boolean m_modifyingText = false;

    public UnderlineTextView(Context context)
    {
        super(context);
        init();
    }

    public UnderlineTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public UnderlineTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //Do nothing here... we don't care
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //Do nothing here... we don't care
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (m_modifyingText)
                    return;

                underlineText();
            }
        });

        underlineText();
    }

    private void underlineText()
    {
        if (m_modifyingText)
            return;

        m_modifyingText = true;

        SpannableString content = new SpannableString(getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        setText(content);

        m_modifyingText = false;
    }
}