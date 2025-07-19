package in.ecomexpress.sathi.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import in.ecomexpress.sathi.R;

public class SwipeLayout extends ViewGroup {

    private static final String TAG = SwipeLayout.class.getSimpleName();
    private static final float VELOCITY_THRESHOLD = 1500f;
    private ViewDragHelper dragHelper;
    private View leftView;
    private View rightView;
    private View centerView;
    private float velocityThreshold;
    private float touchSlop;
    private OnSwipeListener swipeListener;
    private WeakReference<ObjectAnimator> weakAnimator;
    private final Map<View, Boolean> hackedParents = new WeakHashMap<>();
    private boolean leftSwipeEnabled = true;
    private boolean rightSwipeEnabled = true;
    private static final int TOUCH_STATE_WAIT = 0;
    private static final int TOUCH_STATE_SWIPE = 1;
    private static final int TOUCH_STATE_SKIP = 2;
    private int touchState = TOUCH_STATE_WAIT;
    private float touchX;
    private float touchY;

    public SwipeLayout(Context context){
        super(context);
        init(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs){
        try{
            dragHelper = ViewDragHelper.create(this, 1f, dragCallback);
            velocityThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, VELOCITY_THRESHOLD, getResources().getDisplayMetrics());
            touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if(attrs != null){
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
                if(a.hasValue(R.styleable.SwipeLayout_swipe_enabled)){
                    leftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true);
                    rightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true);
                }
                if(a.hasValue(R.styleable.SwipeLayout_left_swipe_enabled)){
                    leftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_left_swipe_enabled, true);
                }
                if(a.hasValue(R.styleable.SwipeLayout_right_swipe_enabled)){
                    rightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_right_swipe_enabled, true);
                }
                a.recycle();
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void setOnSwipeListener(OnSwipeListener swipeListener){
        this.swipeListener = swipeListener;
    }

    // Reset swipe-layout state to initial position
    public void reset(){
        try{
            if(centerView == null) {
                return;
            }
            finishAnimator();
            dragHelper.abort();
            offsetChildren(null, -centerView.getLeft());
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    // Reset swipe-layout state to initial position with animation (200ms)
    public void animateReset(){
        try{
            if(centerView != null){
                runAnimation(centerView.getLeft(), 0);
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public void animateSwipeRight(){
        try{
            if(centerView != null && leftView != null){
                int target = leftView.getWidth();
                runAnimation(getOffset(), target);
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void runAnimation(int initialX, int targetX){
        try{
            finishAnimator();
            dragHelper.abort();
            ObjectAnimator animator = new ObjectAnimator();
            animator.setTarget(this);
            animator.setPropertyName("offset");
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setIntValues(initialX, targetX);
            animator.setDuration(200);
            animator.start();
            this.weakAnimator = new WeakReference<>(animator);
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void finishAnimator(){
        try{
            if(weakAnimator != null){
                ObjectAnimator animator = this.weakAnimator.get();
                if(animator != null){
                    this.weakAnimator.clear();
                    if(animator.isRunning()){
                        animator.end();
                    }
                }
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    // Get horizontal offset from initial position
    public int getOffset(){
        return centerView == null ? 0 : centerView.getLeft();
    }

    // Set horizontal offset from initial position
    public void setOffset(int offset){
        if(centerView != null){
            offsetChildren(null, offset - centerView.getLeft());
        }
    }

    public boolean isSwipeEnabled(){
        return leftSwipeEnabled || rightSwipeEnabled;
    }

    public void setSwipeEnabled(boolean enabled){
        this.leftSwipeEnabled = enabled;
        this.rightSwipeEnabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int count = getChildCount();
            int maxHeight = 0;

            // Measure children if height mode is EXACTLY
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                measureChildren(widthMeasureSpec, heightMeasureSpec);
            } else {
                // Measure each child and find the maximum height
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child != null) {
                        measureChild(child, widthMeasureSpec, heightMeasureSpec);
                        int childHeight = child.getMeasuredHeight();
                        if (childHeight > maxHeight) {
                            maxHeight = childHeight;
                        }
                    }
                }

                // Use the maximum height to measure children again if needed
                if (maxHeight > 0) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
                    measureChildren(widthMeasureSpec, heightMeasureSpec);
                }
            }

            // Calculate the total height including padding
            maxHeight += getPaddingTop() + getPaddingBottom();
            maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());

            // Resolve the measured dimensions and set the final size
            int measuredWidth = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            int measuredHeight = resolveSize(maxHeight, heightMeasureSpec);
            setMeasuredDimension(measuredWidth, measuredHeight);
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        layoutChildren();
    }

    private void layoutChildren(){
        try{
            final int count = getChildCount();
            final int parentTop = getPaddingTop();
            centerView = null;
            leftView = null;
            rightView = null;
            for(int i = 0; i < count; i++){
                View child = getChildAt(i);
                if(child.getVisibility() == GONE)
                    continue;
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                switch(lp.gravity){
                    case LayoutParams.CENTER:
                        centerView = child;
                        break;
                    case LayoutParams.LEFT:
                        leftView = child;
                        break;
                    case LayoutParams.RIGHT:
                        rightView = child;
                        break;
                }
            }
            if(centerView == null) {
                throw new RuntimeException("Center view must be added");
            }
            for(int i = 0; i < count; i++){
                final View child = getChildAt(i);
                if(child.getVisibility() != GONE){
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();
                    int childLeft;
                    int childTop;
                    int orientation = lp.gravity;
                    switch(orientation){
                        case LayoutParams.LEFT:
                            childLeft = centerView.getLeft() - width;
                            break;
                        case LayoutParams.RIGHT:
                            childLeft = centerView.getRight();
                            break;
                        case LayoutParams.CENTER:
                        default:
                            childLeft = child.getLeft();
                            break;
                    }
                    childTop = parentTop;
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private final ViewDragHelper.Callback dragCallback = new ViewDragHelper.Callback() {

        private int initLeft;

        @Override
        public boolean tryCaptureView(View child, int pointerId){
            initLeft = child.getLeft();
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx){
            if(dx > 0){
                return clampMoveRight(child, left);
            } else{
                return clampMoveLeft(child, left);
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child){
            return getWidth();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel){
            int dx = releasedChild.getLeft() - initLeft;
            if(dx == 0) {
                return;
            }
            boolean handled;
            if(dx > 0){
                handled = xvel >= 0 ? onMoveRightReleased(releasedChild, dx, xvel) : onMoveLeftReleased(releasedChild, dx, xvel);
            } else{
                handled = xvel <= 0 ? onMoveLeftReleased(releasedChild, dx, xvel) : onMoveRightReleased(releasedChild, dx, xvel);
            }
            if(!handled){
                startScrollAnimation(releasedChild, releasedChild.getLeft() - centerView.getLeft(), false, dx > 0);
            }
        }

        private boolean leftViewClampReached(LayoutParams leftViewLP){
            try{
                if(leftView == null) {
                    return false;
                }
                switch(leftViewLP.clamp){
                    case LayoutParams.CLAMP_PARENT:
                        return leftView.getRight() >= getWidth();
                    case LayoutParams.CLAMP_SELF:
                        return leftView.getRight() >= leftView.getWidth();
                    default:
                        return leftView.getRight() >= leftViewLP.clamp;
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
                return false;
            }
        }

        private boolean rightViewClampReached(LayoutParams lp){
            try{
                if(rightView == null) {
                    return false;
                }
                switch(lp.clamp){
                    case LayoutParams.CLAMP_PARENT:
                    case LayoutParams.CLAMP_SELF:
                        return rightView.getRight() <= getWidth();
                    default:
                        return rightView.getLeft() + lp.clamp <= getWidth();
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
                return false;
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy){
            try {
                offsetChildren(changedView, dx);
                if(swipeListener == null) {
                    return;
                }
                int stickyBound;
                if(dx > 0){
                    // Move to right
                    if(leftView != null){
                        stickyBound = getStickyBound(leftView);
                        if(stickyBound != LayoutParams.STICKY_NONE){
                            if(leftView.getRight() - stickyBound > 0 && leftView.getRight() - stickyBound - dx <= 0)
                                swipeListener.onLeftStickyEdge(SwipeLayout.this, true);
                        }
                    }
                    if(rightView != null){
                        stickyBound = getStickyBound(rightView);
                        if(stickyBound != LayoutParams.STICKY_NONE){
                            if(rightView.getLeft() + stickyBound > getWidth() && rightView.getLeft() + stickyBound - dx <= getWidth())
                                swipeListener.onRightStickyEdge(SwipeLayout.this, true);
                        }
                    }
                } else if(dx < 0){
                    // Move to left
                    if(leftView != null){
                        stickyBound = getStickyBound(leftView);
                        if(stickyBound != LayoutParams.STICKY_NONE){
                            if(leftView.getRight() - stickyBound <= 0 && leftView.getRight() - stickyBound - dx > 0)
                                swipeListener.onLeftStickyEdge(SwipeLayout.this, false);
                        }
                    }
                    if(rightView != null){
                        stickyBound = getStickyBound(rightView);
                        if(stickyBound != LayoutParams.STICKY_NONE){
                            if(rightView.getLeft() + stickyBound <= getWidth() && rightView.getLeft() + stickyBound - dx > getWidth())
                                swipeListener.onRightStickyEdge(SwipeLayout.this, false);
                        }
                    }
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private int getStickyBound(View view){
            LayoutParams lp = getLayoutParams(view);
            if(lp.sticky == LayoutParams.STICKY_NONE) {
                return LayoutParams.STICKY_NONE;
            }
            return lp.sticky == LayoutParams.STICKY_SELF ? view.getWidth() : lp.sticky;
        }

        private int clampMoveRight(View child, int left){
            try {
                if(leftView == null){
                    return child == centerView ? Math.min(left, 0) : Math.min(left, getWidth());
                }
                LayoutParams lp = getLayoutParams(leftView);
                switch(lp.clamp){
                    case LayoutParams.CLAMP_PARENT:
                        return Math.min(left, getWidth() + child.getLeft() - leftView.getRight());
                    case LayoutParams.CLAMP_SELF:
                        return Math.min(left, child.getLeft() - leftView.getLeft());
                    default:
                        return Math.min(left, child.getLeft() - leftView.getRight() + lp.clamp);
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
                return child == centerView ? Math.min(left, 0) : Math.min(left, getWidth());
            }
        }

        private int clampMoveLeft(View child, int left){
            try {
                if(rightView == null){
                    return child == centerView ? Math.max(left, 0) : Math.max(left, -child.getWidth());
                }
                LayoutParams lp = getLayoutParams(rightView);
                switch(lp.clamp){
                    case LayoutParams.CLAMP_PARENT:
                        return Math.max(child.getLeft() - rightView.getLeft(), left);
                    case LayoutParams.CLAMP_SELF:
                        return Math.max(left, getWidth() - rightView.getLeft() + child.getLeft() - rightView.getWidth());
                    default:
                        return Math.max(left, getWidth() - rightView.getLeft() + child.getLeft() - lp.clamp);
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
                return child == centerView ? Math.max(left, 0) : Math.max(left, -child.getWidth());
            }
        }

        private boolean onMoveRightReleased(View child, int dx, float xvel){
            try {
                if(xvel > velocityThreshold){
                    int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
                    boolean moveToOriginal = centerView.getLeft() < 0;
                    startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true);
                    return true;
                }
                if(leftView == null){
                    startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, true);
                    return true;
                }
                LayoutParams lp = getLayoutParams(leftView);
                if(dx > 0 && xvel >= 0 && leftViewClampReached(lp)){
                    if(swipeListener != null){
                        swipeListener.onSwipeClampReached(SwipeLayout.this, true);
                    }
                    return true;
                }
                if(dx > 0 && xvel >= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && leftView.getRight() > lp.bringToClamp){
                    int left = centerView.getLeft() < 0 ? child.getLeft() - centerView.getLeft() : getWidth();
                    startScrollAnimation(child, clampMoveRight(child, left), true, true);
                    return true;
                }
                if(lp.sticky != LayoutParams.STICKY_NONE){
                    int stickyBound = lp.sticky == LayoutParams.STICKY_SELF ? leftView.getWidth() : lp.sticky;
                    float amplitude = stickyBound * lp.stickySensitivity;
                    if(isBetween(-amplitude, amplitude, centerView.getLeft() - stickyBound)){
                        boolean toClamp = (lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == leftView.getWidth()) || lp.clamp == stickyBound || (lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == getWidth());
                        startScrollAnimation(child, child.getLeft() - centerView.getLeft() + stickyBound, toClamp, true);
                        return true;
                    }
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
            return false;
        }

        private boolean onMoveLeftReleased(View child, int dx, float xvel){
            try {
                if(-xvel > velocityThreshold){
                    int left = centerView.getLeft() > 0 ? child.getLeft() - centerView.getLeft() : -getWidth();
                    boolean moveToOriginal = centerView.getLeft() > 0;
                    startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false);
                    return true;
                }
                if(rightView == null){
                    startScrollAnimation(child, child.getLeft() - centerView.getLeft(), false, false);
                    return true;
                }
                LayoutParams lp = getLayoutParams(rightView);
                if(dx < 0 && xvel <= 0 && rightViewClampReached(lp)){
                    if(swipeListener != null){
                        swipeListener.onSwipeClampReached(SwipeLayout.this, false);
                    }
                    return true;
                }
                if(dx < 0 && xvel <= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && rightView.getLeft() + lp.bringToClamp < getWidth()){
                    int left = centerView.getLeft() > 0 ? child.getLeft() - centerView.getLeft() : -getWidth();
                    startScrollAnimation(child, clampMoveLeft(child, left), true, false);
                    return true;
                }
                if(lp.sticky != LayoutParams.STICKY_NONE){
                    int stickyBound = lp.sticky == LayoutParams.STICKY_SELF ? rightView.getWidth() : lp.sticky;
                    float amplitude = stickyBound * lp.stickySensitivity;
                    if(isBetween(-amplitude, amplitude, centerView.getRight() + stickyBound - getWidth())){
                        boolean toClamp = (lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == rightView.getWidth()) || lp.clamp == stickyBound || (lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == getWidth());
                        startScrollAnimation(child, child.getLeft() - rightView.getLeft() + getWidth() - stickyBound, toClamp, false);
                        return true;
                    }
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
            return false;
        }

        private boolean isBetween(float left, float right, float check){
            return check >= left && check <= right;
        }
    };

    private void startScrollAnimation(View view, int targetX, boolean moveToClamp, boolean toRight){
        try {
            if(dragHelper.settleCapturedViewAt(targetX, view.getTop())){
                ViewCompat.postOnAnimation(view, new SettleRunnable(view, moveToClamp, toRight));
            } else{
                if(moveToClamp && swipeListener != null){
                    swipeListener.onSwipeClampReached(SwipeLayout.this, toRight);
                }
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private LayoutParams getLayoutParams(View view){
        return (LayoutParams) view.getLayoutParams();
    }

    private void offsetChildren(View skip, int dx){
        try {
            if(dx == 0) {
                return;
            }
            int count = getChildCount();
            for(int i = 0; i < count; i++){
                View child = getChildAt(i);
                if(child == skip) {
                    continue;
                }
                child.offsetLeftAndRight(dx);
                invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void hackParents(){
        try {
            ViewParent parent = getParent();
            while(parent != null){
                if(parent instanceof NestedScrollingParent){
                    View view = (View) parent;
                    hackedParents.put(view, view.isEnabled());
                }
                parent = parent.getParent();
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void unHackParents(){
        for(Map.Entry<View, Boolean> entry : hackedParents.entrySet()){
            View view = entry.getKey();
            if(view != null){
                view.setEnabled(entry.getValue());
            }
        }
        hackedParents.clear();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return isSwipeEnabled() ? internalOnInterceptTouchEvent(event) : super.onInterceptTouchEvent(event);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        try {
            boolean defaultResult = super.onTouchEvent(event);
            if(!isSwipeEnabled()){
                return defaultResult;
            }
            switch(event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    onTouchBegin(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(touchState == TOUCH_STATE_WAIT){
                        float dx = Math.abs(event.getX() - touchX);
                        float dy = Math.abs(event.getY() - touchY);
                        boolean isLeftToRight = (event.getX() - touchX) > 0;
                        if(((isLeftToRight && !leftSwipeEnabled) || (!isLeftToRight && !rightSwipeEnabled)) && getOffset() == 0){
                            return defaultResult;
                        }
                        if(dx >= touchSlop || dy >= touchSlop){
                            touchState = dy == 0 || dx / dy > 1f ? TOUCH_STATE_SWIPE : TOUCH_STATE_SKIP;
                            if(touchState == TOUCH_STATE_SWIPE){
                                requestDisallowInterceptTouchEvent(true);
                                hackParents();
                                if(swipeListener != null) {
                                    swipeListener.onBeginSwipe(this, event.getX() > touchX);
                                }
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if(touchState == TOUCH_STATE_SWIPE){
                        unHackParents();
                        requestDisallowInterceptTouchEvent(false);
                    }
                    touchState = TOUCH_STATE_WAIT;
                    break;
            }
            if(event.getActionMasked() != MotionEvent.ACTION_MOVE || touchState == TOUCH_STATE_SWIPE){
                dragHelper.processTouchEvent(event);
            }
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return true;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams(){
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs){
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p){
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p){
        return p instanceof LayoutParams;
    }

    private boolean internalOnInterceptTouchEvent(MotionEvent event){
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
            onTouchBegin(event);
        }
        return dragHelper.shouldInterceptTouchEvent(event);
    }

    private void onTouchBegin(MotionEvent event){
        touchState = TOUCH_STATE_WAIT;
        touchX = event.getX();
        touchY = event.getY();
    }

    private class SettleRunnable implements Runnable {

        private final View view;
        private final boolean moveToClamp;
        private final boolean moveToRight;

        SettleRunnable(View view, boolean moveToClamp, boolean moveToRight){
            this.view = view;
            this.moveToClamp = moveToClamp;
            this.moveToRight = moveToRight;
        }

        public void run(){
            try{
                if(dragHelper != null && dragHelper.continueSettling(true)){
                    ViewCompat.postOnAnimation(this.view, this);
                } else{
                    if(moveToClamp && swipeListener != null){
                        swipeListener.onSwipeClampReached(SwipeLayout.this, moveToRight);
                    }
                }
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static class LayoutParams extends ViewGroup.LayoutParams {

        public static final int LEFT = -1;
        public static final int RIGHT = 1;
        public static final int CENTER = 0;
        public static final int CLAMP_PARENT = -1;
        public static final int CLAMP_SELF = -2;
        public static final int BRING_TO_CLAMP_NO = -1;
        public static final int STICKY_SELF = -1;
        public static final int STICKY_NONE = -2;
        private static final float DEFAULT_STICKY_SENSITIVITY = 0.9f;
        private int gravity = CENTER;
        private int sticky;
        private float stickySensitivity = DEFAULT_STICKY_SENSITIVITY;
        private int clamp = CLAMP_SELF;
        private int bringToClamp = BRING_TO_CLAMP_NO;

        @SuppressLint("CustomViewStyleable")
        public LayoutParams(Context c, AttributeSet attrs){
            super(c, attrs);
            try{
                TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
                final int N = a.getIndexCount();
                for(int i = 0; i < N; ++i){
                    int attr = a.getIndex(i);
                    if (attr == R.styleable.SwipeLayout_gravity){
                        gravity = a.getInt(attr, CENTER);
                    } else if(attr == R.styleable.SwipeLayout_sticky){
                        sticky = a.getLayoutDimension(attr, STICKY_SELF);
                    } else if(attr == R.styleable.SwipeLayout_clamp){
                        clamp = a.getLayoutDimension(attr, CLAMP_SELF);
                    } else if(attr == R.styleable.SwipeLayout_bring_to_clamp){
                        bringToClamp = a.getLayoutDimension(attr, BRING_TO_CLAMP_NO);
                    } else if(attr == R.styleable.SwipeLayout_sticky_sensitivity){
                        stickySensitivity = a.getFloat(attr, DEFAULT_STICKY_SENSITIVITY);
                    }
                }
                a.recycle();
            } catch (Exception e){
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public LayoutParams(ViewGroup.LayoutParams source){
            super(source);
        }

        public LayoutParams(int width, int height){
            super(width, height);
        }
    }

    public interface OnSwipeListener {

        void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight);

        void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight);

        void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight);

        void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight);
    }
}