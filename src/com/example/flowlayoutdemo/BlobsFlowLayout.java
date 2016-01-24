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
public class BlobsFlowLayout extends AbstractFlowLayout {
    public BlobsFlowLayout(Context context) {
        this(context, null);
    }

    public BlobsFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlobsFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mMoreView = LayoutInflater.from(context).inflate(R.layout.blob_more, null, false);
        mMoreView.setOnClickListener(mDotsViewClickListener);
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
                        ((TextView) mMoreView).setText(String.format(getResources().getString(R.string.more), mRestItems));
                        break;
                    case STATE_EXPANDED:
                        ((TextView) mMoreView).setText(getResources().getString(R.string.more_collapse));
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
