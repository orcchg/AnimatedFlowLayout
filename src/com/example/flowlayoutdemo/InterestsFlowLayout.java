package com.example.flowlayoutdemo;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by alov on 08.12.15.
 */
public class InterestsFlowLayout extends AbstractFlowLayout {
    public InterestsFlowLayout(Context context) {
        this(context, null);
    }

    public InterestsFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterestsFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDotsView = LayoutInflater.from(context).inflate(R.layout.profile_interest_blob_more, null, false);
        mDotsView.setOnClickListener(mDotsViewClickListener);
        setUpLayoutChangeListener();
    }

    /* Layout changes listener */
    // --------------------------------------------------------------------------------------------
    private void setUpLayoutChangeListener() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switch (mCurrentState) {
                    case STATE_COLLAPSED:
                        ((TextView) mDotsView).setText(String.format(getResources().getString(R.string.profile_material_interests_more), mRestItems));
                        break;
                    case STATE_EXPANDED:
                        ((TextView) mDotsView).setText(getResources().getString(R.string.profile_material_interests_more_collapse));
                        break;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    // ------------------------------------------
    @Override
    protected void onDotsViewClicked() {
        super.onDotsViewClicked();
        setUpLayoutChangeListener();
    }
}
