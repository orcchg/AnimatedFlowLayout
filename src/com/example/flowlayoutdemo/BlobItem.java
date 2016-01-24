package com.example.flowlayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BlobItem extends FrameLayout {

  View mRootView;
  TextView mTextView;
  
  public BlobItem(Context context) {
    this(context, null);
  }
  
  public BlobItem(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }
  
  public BlobItem(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    
    mRootView = LayoutInflater.from(context).inflate(R.layout.blob, this, true);
    mTextView = (TextView) mRootView.findViewById(R.id.text);
  }
  
  public void setText(String text) {
    mTextView.setText(text);
  }
}
