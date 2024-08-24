package com.example.braille_sync.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braille_sync.Interface.HistoryRecyclerviewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Map;

public abstract class HistoryHelperSwipe extends ItemTouchHelper.SimpleCallback {
    int ButtonWidth;
    private final RecyclerView rv;
    private List<myButton> btnlist;
    private final GestureDetector gesturedetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private final Map<Integer, List<myButton>> buttonBuffer;
    private final Queue<Integer> removeQueue;

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            for (myButton btn : btnlist) {
                if (btn.onClick(e.getX(), e.getY())) {
                    break;
                }
            }
            return true;
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            if (swipePosition < 0) return false;
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());

            RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = Objects.requireNonNull(viewHolder).itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_UP) {
                if (rect.top < point.y && rect.bottom > point.y) {
                    gesturedetector.onTouchEvent(e);
                    v.performClick(); // Call performClick for accessibility
                } else {
                    removeQueue.add(swipePosition);
                    swipePosition = -1;
                    recoverSwipeItem();
                }
            }
            return false;
        }
    };


    private synchronized void recoverSwipeItem() {
        Integer pos;
        while (!removeQueue.isEmpty()) {
            pos = removeQueue.poll();
            if (pos != null && pos > -1) {
                Objects.requireNonNull(rv.getAdapter()).notifyItemChanged(pos);
            }

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public HistoryHelperSwipe(Context context, RecyclerView recyclerView, int buttonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.ButtonWidth = buttonWidth;
        this.rv = recyclerView;
        this.btnlist = new ArrayList<>();
        this.gesturedetector = new GestureDetector(context, gestureListener);
        this.buttonBuffer = new HashMap<>();

        removeQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };
        rv.setOnTouchListener(onTouchListener);
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    public static class myButton {
        private final String text;
        private final int textSize;
        private final int imageResource;
        private final int color;
        private int pos;
        private RectF clickRegion;
        private final HistoryRecyclerviewInterface listener;
        private final Context context;

        public myButton(Context context, String text, int imageResource, int textSize, int color, HistoryRecyclerviewInterface listener) {
            this.text = text;
            this.textSize = textSize;
            this.imageResource = imageResource;
            this.color = color;
            this.listener = listener;
            this.context = context;
            context.getResources();
        }

        public boolean onClick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                listener.toHome(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas c, RectF rectF, int pos) {
            Paint p = new Paint();
            p.setColor(color);

            // Define smaller button dimensions
            float buttonWidth = rectF.width() * 0.7f;  // 70% of the original width
            float buttonHeight = rectF.height() * 0.7f;  // 70% of the original height
            float left = rectF.left + (rectF.width() - buttonWidth) / 2;
            float top = rectF.top + (rectF.height() - buttonHeight) / 2;
            float right = left + buttonWidth;
            float bottom = top + buttonHeight;

            RectF smallerRectF = new RectF(left, top, right, bottom);

            // Drawing a rounded rectangle
            float cornerRadius = 30f; // Adjust the radius as needed
            c.drawRoundRect(smallerRectF, cornerRadius, cornerRadius, p);

            // Draw text or image in the center of the smaller button
            p.setColor(Color.BLACK);
            p.setTextSize(textSize);

            Rect r = new Rect();
            float cHeight = smallerRectF.height();
            float cWidth = smallerRectF.width();
            p.setTextAlign(Paint.Align.LEFT);
            p.getTextBounds(text, 0, text.length(), r);
            float x, y;

            if (imageResource == 0) {
                x = cWidth / 2f - r.width() / 2f - r.left;
                y = cHeight / 2f + r.height() / 2f - r.bottom;
                c.drawText(text, smallerRectF.left + x, smallerRectF.top + y, p);
            } else {
                Drawable b = ContextCompat.getDrawable(context, imageResource);
                Bitmap bitmap = drawableToBitmap(b);
                float bitmapWidth = bitmap.getWidth();
                float bitmapHeight = bitmap.getHeight();
                float centerX = smallerRectF.left + (cWidth - bitmapWidth) / 2f;
                float centerY = smallerRectF.top + (cHeight - bitmapHeight) / 2f;
                c.drawBitmap(bitmap, centerX, centerY, p);
            }

            clickRegion = smallerRectF;
            this.pos = pos;
        }
    }

    private static Bitmap drawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable) return ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    //Override
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getBindingAdapterPosition();
        if (swipePosition != pos)
            removeQueue.add(swipePosition);
        swipePosition = pos;
        if (buttonBuffer.containsKey(swipePosition))
            btnlist = buttonBuffer.get(swipePosition);
        else
            btnlist.clear();
        buttonBuffer.clear();
        swipeThreshold = 0.5f * btnlist.size() * ButtonWidth;
        recoverSwipeItem();
    }


    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getBindingAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0) {
            swipePosition = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                List<myButton> buffer = new ArrayList<>();
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateButton(viewHolder, buffer);
                    buttonBuffer.put(pos, buffer);
                } else {
                    buffer = buttonBuffer.get(pos);
                }
                assert buffer != null;
                translationX = dX * buffer.size() * ButtonWidth / itemView.getWidth();
                drawButton(c, itemView, buffer, pos, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<myButton> buffer, int pos, float translationX) {
        float right = itemView.getRight();
        float defaultWidth = -1 * translationX / buffer.size();
        for (myButton btn : buffer) {
            float left = right - defaultWidth;
            btn.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }

    }

    public abstract void instantiateButton(RecyclerView.ViewHolder viewHolder, List<myButton> buffer);


}
