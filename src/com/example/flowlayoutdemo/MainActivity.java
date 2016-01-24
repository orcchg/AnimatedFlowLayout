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

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

  private BlobsFlowLayout mFlowLayout;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    mFlowLayout = (BlobsFlowLayout) findViewById(R.id.interests_container);
    
    String[] texts = getResources().getStringArray(R.array.texts);
    
    for (int i = 0; i < 30; ++i) {
      BlobItem itemView = new BlobItem(this);
      itemView.setText(texts[i]);
      mFlowLayout.addView(itemView);
    }
    
    mFlowLayout.enableLayoutTransition(true);
  }
}
