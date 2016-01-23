package com.example.flowlayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class InterestItem extends FrameLayout {

  View mRootView;
  TextView mTextView;
  
  public InterestItem(Context context) {
    this(context, null);
  }
  
  public InterestItem(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public InterestItem(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    
    mRootView = LayoutInflater.from(context).inflate(R.layout.profile_interest_blob, this, true);
    mTextView = (TextView) mRootView.findViewById(R.id.text);
  }
  
  public void setText(String text) {
    mTextView.setText(text);
  }
}
