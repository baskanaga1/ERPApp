package com.guruinfo.scm.tms;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.guruinfo.scm.R;
import com.guruinfo.scm.library.TokenCompleteTextView;
import com.guruinfo.scm.library.TokenTextView;
/**
 * Created by Kannan G on 9/21/2016.
 */
public class ContactsCompletionView extends TokenCompleteTextView<lists> {
    public ContactsCompletionView(Context context) {
        super(context);
    }
    public ContactsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ContactsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected View getViewForObject(lists lists) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(lists.getValue());
        return token;
    }
    @Override
    protected lists defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        int index = completionText.indexOf('@');
        if (index == -1) {
            return null;
        } else {
            return new lists(completionText.substring(0, index), completionText);
        }
    }
}
