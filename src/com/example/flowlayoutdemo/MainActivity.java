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
