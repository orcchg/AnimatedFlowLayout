package com.example.flowlayoutdemo;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by alov on 08.12.15.
 */
public class AbstractFlowLayout extends ViewGroup {
    protected final String TAG = this.getClass().getSimpleName();
    private static final int mDotsViewId = 1000;

    private Set<WeakReference<View>> mLastRowViews;

    public static final int STATE_EXPANDED = 0;
    public static final int STATE_COLLAPSED = 1;
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutState {}

    protected final int VERTICAL_SPACING;
    protected final int HORIZONTAL_SPACING;
    protected final int ALLOWED_ROWS_COUNT;

    protected int mHeight, mExpandedHeight;
    protected int mRestItems = 0;
    protected @LayoutState int mCurrentState = STATE_COLLAPSED;
    protected boolean mIsAnimated = false;
    protected boolean mShouldDeferMeasure = false;

    protected View mDotsView;
    protected AbstractFlowLayoutTransition mLayoutTransition;

    public interface OnExpandChangedListener {
        void onExpandChanged(@LayoutState int newState);
    }

    protected OnExpandChangedListener mOnExpandChangedListener;

    public void setOnExpandChangedListener(OnExpandChangedListener listener) {
        mOnExpandChangedListener = listener;
    }

    /* Init */
    // --------------------------------------------------------------------------------------------
    public AbstractFlowLayout(Context context) {
        this(context, null);
    }

    public AbstractFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources resources = context.getResources();
        int defVerticalSpacing = resources.getDimensionPixelSize(R.dimen.interestVerticalSpacing);
        int defHorizontalSpacing = resources.getDimensionPixelSize(R.dimen.interestHorizontalSpacing);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyle, 0);
        ALLOWED_ROWS_COUNT = a.getInt(R.styleable.FlowLayout_rowsCount, 0);
        VERTICAL_SPACING = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, defVerticalSpacing);
        HORIZONTAL_SPACING = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, defHorizontalSpacing);
        a.recycle();

        mLastRowViews = new HashSet<WeakReference<View>>();
    }

    public void enableLayoutTransition(boolean isEnabled) {
        mIsAnimated = isEnabled;
        if (isEnabled && mLayoutTransition == null) {
            mLayoutTransition = new AbstractFlowLayoutTransition(this);
            mLayoutTransition.addTransitionListener(new TransitionListener() {
              @Override
              public void startTransition(LayoutTransition transition, ViewGroup container, View view,
                  int transitionType) {
                // TODO Auto-generated method stub
                
              }
              
              @Override
              public void endTransition(LayoutTransition transition, ViewGroup container, View view,
                  int transitionType) {
                if (view == mDotsView) {
                  String str = "";
                  switch (transitionType) {
                    case LayoutTransition.CHANGE_APPEARING:
                      str = "Expansion ended";
                      break;
                    case LayoutTransition.CHANGE_DISAPPEARING:
                      str = "Collapsion ended";
                      mShouldDeferMeasure = false;
                      requestLayout();
                      break;
                  }
                  if (!TextUtils.isEmpty(str)) {
                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
                  }
                }
              }
            });
        }
        AbstractFlowLayoutTransition layoutTransition = isEnabled ? mLayoutTransition : null;
        setLayoutTransition(layoutTransition);
    }

    /* Listener */
    // --------------------------------------------------------------------------------------------
    protected View.OnClickListener mDotsViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDotsViewClicked();
        }
    };

    protected void onDotsViewClicked() {
      @LayoutState int oldState = mCurrentState;
      mCurrentState = mCurrentState == STATE_COLLAPSED ? STATE_EXPANDED : STATE_COLLAPSED;
      
        if (mIsAnimated && oldState == STATE_EXPANDED) {
         // wait till animation finished - then measure
          mShouldDeferMeasure = true;
        }
        performLayoutRequest();
    }
    
    private void performLayoutRequest() {
      requestLayout();
      invalidateLastRowViews();
      if (mOnExpandChangedListener != null) {
        mOnExpandChangedListener.onExpandChanged(mCurrentState);
      }
    }

    /* Measuring */
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int currentRow = 1;
        int width = View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int count = getChildCount();
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int childHeightMeasureSpec;
        int widthAtMostSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);

        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        mHeight = 0;
        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.measure(widthAtMostSpec, childHeightMeasureSpec);
                int childw = child.getMeasuredWidth();
                mHeight = Math.max(mHeight, child.getMeasuredHeight() + VERTICAL_SPACING);

                if (xpos + childw > width) {
                    ypos += mHeight;
                    if (mCurrentState == STATE_COLLAPSED &&
                        ALLOWED_ROWS_COUNT > 0 &&
                        mDotsView != null && mDotsView.getVisibility() != View.GONE &&
                        currentRow == ALLOWED_ROWS_COUNT) {
                        // measure dots view
                        mDotsView.measure(widthAtMostSpec, childHeightMeasureSpec);
                        int dotsViewWidth = mDotsView.getMeasuredWidth();
                        if (xpos + dotsViewWidth <= width) {  // same line
                            ypos -= mHeight;
                        }
                        break;
                    }

                    xpos = getPaddingLeft();
                    ++currentRow;
                }
                xpos += childw + HORIZONTAL_SPACING;
            }
        }
        
        if (mCurrentState == STATE_EXPANDED) {
          mExpandedHeight = mHeight * currentRow;
        }
        
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.UNSPECIFIED) {
            height = ypos + mHeight;
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST) {
            if (ypos + mHeight < height) {
                height = ypos + mHeight;
            }
        }
        
        if (mShouldDeferMeasure) {
          height = mExpandedHeight;
        }

        setMeasuredDimension(width, height);
    }

    /* Layout */
    // --------------------------------------------------------------------------------------------
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int currentRow = 1;
        int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int count = getChildCount();

        for (int i = 0; i < count; ++i) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int childw = child.getMeasuredWidth();
                int childh = child.getMeasuredHeight();

                if (xpos + childw > width) {
                    if (mCurrentState == STATE_COLLAPSED &&
                        ALLOWED_ROWS_COUNT > 0 &&
                        mDotsView != null && mDotsView.getVisibility() != View.GONE &&
                        currentRow == ALLOWED_ROWS_COUNT) {
                        // layout dots view
                        int dotsViewWidth = mDotsView.getMeasuredWidth();
                        int dotsXpos = getPaddingLeft();
                        int dotsYpos = ypos + mHeight;
                        // Add dots view at the end of flow
                        mDotsView.setId(mDotsViewId);
                        if (findViewById(mDotsViewId) == null) {
                          addView(mDotsView);
                        }
                        if (xpos + dotsViewWidth <= width) {  // same line
                            dotsXpos = xpos;
                            dotsYpos = ypos;
                        }

                        mDotsView.layout(dotsXpos, dotsYpos, dotsXpos + dotsViewWidth, dotsYpos + childh);
                        if (mRestItems == 0) {
                            mRestItems = count - i;
                        }
                        break;
                    }

                    xpos = getPaddingLeft();
                    ypos += mHeight;
                    ++currentRow;
                }

                if (mCurrentState == STATE_EXPANDED &&
                    ALLOWED_ROWS_COUNT > 0 &&
                    currentRow >= ALLOWED_ROWS_COUNT + 1) {  // last row only: ==
                    WeakReference<View> viewRef = new WeakReference<View>(child);
                    mLastRowViews.add(viewRef);
                }

                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + HORIZONTAL_SPACING;
            }
        }
    }

    /* Utility */
    // --------------------------------------------------------------------------------------------
    private void invalidateLastRowViews() {
        int visibility = mCurrentState == STATE_COLLAPSED ? View.GONE : View.VISIBLE;
        for (WeakReference<View> viewRef : mLastRowViews) {
            View child = viewRef.get();
            if (child != null && child != mDotsView) {
                child.setVisibility(visibility);
            }
        }
    }

    /* Transition animation */
    // --------------------------------------------------------------------------------------------
    private static class AbstractFlowLayoutTransition extends LayoutTransition {
        private static final int DEFAULT_DURATION = 300;  // ms
        private WeakReference<AbstractFlowLayout> mLayoutRef;

        public AbstractFlowLayoutTransition(AbstractFlowLayout layout) {
            mLayoutRef = new WeakReference<AbstractFlowLayout>(layout);

           // TODO:
            //disableTransitionType(APPEARING);
        //disableTransitionType(DISAPPEARING);
//            setAnimator(APPEARING, null);
//            setAnimator(DISAPPEARING, null);
            
//            disableTransitionType(CHANGE_APPEARING);
//        disableTransitionType(CHANGE_DISAPPEARING);
//        disableTransitionType(CHANGING);
            //setStartDelay(CHANGE_APPEARING, 0);
            //setStartDelay(CHANGE_DISAPPEARING, 0);

//            PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
//            PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
//            PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
//            PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);
//            ObjectAnimator expandAnimator = ObjectAnimator.ofPropertyValuesHolder((Object) null, pvhLeft, pvhTop, pvhRight, pvhBottom);
//            expandAnimator.setDuration(DEFAULT_DURATION);
//            expandAnimator.setStartDelay(0);
//            expandAnimator.setInterpolator(new DecelerateInterpolator());
//            ObjectAnimator collapseAnimator = expandAnimator.clone();
//            collapseAnimator.setStartDelay(DEFAULT_DURATION);
//
//            setAnimator(CHANGE_APPEARING, expandAnimator);
//            setAnimator(CHANGE_DISAPPEARING, collapseAnimator);
        }
    }
    
    public void expand() {
      expand(this);
    }
    
    public void collapse() {
      collapse(this);
    }

    public void expand(final View v) {
      v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      final int targetHeight = v.getMeasuredHeight();

      // Older versions of android (pre API 21) cancel animations for views with a height of 0.
      v.getLayoutParams().height = 1;
      v.setVisibility(View.VISIBLE);
      Animation a = new Animation()
      {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
              v.getLayoutParams().height = interpolatedTime == 1
                      ? LayoutParams.WRAP_CONTENT
                      : (int)(targetHeight * interpolatedTime);
              v.requestLayout();
          }

          @Override
          public boolean willChangeBounds() {
              return true;
          }
      };

      // 1dp/ms
      a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
      v.startAnimation(a);
  }

  public void collapse(final View v) {
      final int initialHeight = v.getMeasuredHeight();

      Animation a = new Animation()
      {
          @Override
          protected void applyTransformation(float interpolatedTime, Transformation t) {
              if(interpolatedTime == 1){
                  v.setVisibility(View.GONE);
              }else{
                  v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                  v.requestLayout();
              }
          }

          @Override
          public boolean willChangeBounds() {
              return true;
          }
      };

      // 1dp/ms
      a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
      v.startAnimation(a);
  }
}