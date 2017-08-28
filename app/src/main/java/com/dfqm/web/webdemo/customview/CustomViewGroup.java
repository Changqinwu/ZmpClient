package com.dfqm.web.webdemo.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by 吴 on 2017/8/25.
 */

public class CustomViewGroup extends LinearLayout {
    public CustomViewGroup(Context context) {
        super(context);
    }

    public CustomViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
