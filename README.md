Animated FlowLayout
========

ViewGroup representing FlowLayout of custom child Views. It's better to see it once that read about it infinitely:

![Demo](https://cloud.githubusercontent.com/assets/1728123/12536224/58b34dd8-c2b0-11e5-9e2b-1b44ebb260a9.gif)

Usage:
  1. Directly inherit AbstractFlowLayout class, and define how (more)-button should look like (mMoreView), say:
  ```java
    BlobsFlowLayout extends AbstractFlowLayout {
        public BlobsFlowLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            mMoreView = LayoutInflater.from(context).inflate(R.layout.blob_more, null, false);
            mMoreView.setOnClickListener(mDotsViewClickListener);
        }
    }
  ```

  2. Use it in your XML layout:
  ```xml
  	    <com.example.flowlayoutdemo.BlobsFlowLayout
	          android:id="@+id/interests_container"
	          android:layout_width="match_parent"
	          android:layout_height="wrap_content"
	          flow:rowsCount="4"
	          flow:verticalSpacing="8dp"
	          flow:horizontalSpacing="8dp"/>
  ```

See demo sample.

License
=======
See LICENSE.md
