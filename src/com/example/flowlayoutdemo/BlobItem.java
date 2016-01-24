/*
 * Copyright (C) 2016 Maxim Alov <alovmax@yandex.ru>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
