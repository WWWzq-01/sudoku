package com.example.wzq.sudoku.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.R;

/**
 * @author wzq20
 */
public class MapDivider extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    private Context context;

    private Resources resources;

    public MapDivider(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        this.context =context;
        resources = context.getResources();
        a.recycle();
    }

    public void setDrawable(@NonNull Drawable drawable) {
        mDivider = drawable;
    }

    @Override
    public void onDraw(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {
        // 绘制间隔，每一个item，绘制右边和下方间隔样式
        int childCount = parent.getChildCount();
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        int x, y;
        boolean isDrawHorizontalDivider;
        boolean isDrawVerticalDivider;
        Drawable grayDivider = resources.getDrawable(R.drawable.gray_divider,null);
        Drawable blackDivider = resources.getDrawable(R.drawable.black_divider,null);

        for (int i = 0; i < childCount; i++) {
            isDrawVerticalDivider = true;
            isDrawHorizontalDivider = true;

            x = i / 9;
            y = i % 9;

            mDivider = grayDivider;
            if(i == childCount-1) {
                continue;
            }
            if((x+1)%3 == 0&&(y+1)%3==0 ) {
                mDivider = blackDivider;
                drawHorizontalDivider(c,parent,i);
                drawVerticalDivider(c,parent,i);
                continue;
            }
            if ((y + 1) % 3 == 0) {
                mDivider = blackDivider;
                drawVerticalDivider(c,parent,i);
                mDivider = grayDivider;
                drawHorizontalDivider(c,parent,i);
                continue;
            }
            if ((x + 1) % 3 == 0) {
                mDivider = blackDivider;
                drawHorizontalDivider(c,parent,i);
                mDivider = grayDivider;
                drawVerticalDivider(c,parent,i);
                continue;
            }

            // 最后一列
            if ((i + 1) % spanCount == 0) {
                isDrawVerticalDivider = false;
            }
            //最后一行
            if ((i + 1) / spanCount == 8) {
                isDrawHorizontalDivider = false;
            }

            if (isDrawHorizontalDivider) {
                drawHorizontalDivider(c, parent, i);
            }
            if (isDrawVerticalDivider) {
                drawVerticalDivider(c, parent, i);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        int position = parent.getChildLayoutPosition(view);

        // 如果是最后一列，则不画右边
        if ((position + 1) % spanCount == 0) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            return;
        }
        // 如果是最后一行，则不画下面
        if ((position + 1) / spanCount == 8) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            return;
        }

        outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
    }

    /**
     * 绘制竖直间隔线
     */
    private void drawVerticalDivider(Canvas canvas, RecyclerView parent, int position) {
        final View child = parent.getChildAt(position);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        final int top = child.getTop() - params.topMargin;
        final int bottom = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight();
        final int left = child.getRight() + params.rightMargin;
        final int right = left + mDivider.getIntrinsicWidth();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(canvas);
    }

    /**
     * 绘制水平间隔线
     */
    private void drawHorizontalDivider(Canvas canvas, RecyclerView parent, int position) {
        final View child = parent.getChildAt(position);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                .getLayoutParams();
        final int top = child.getBottom() + params.bottomMargin;
        final int bottom = top + mDivider.getIntrinsicHeight();
        final int left = child.getLeft() - params.leftMargin;
        final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(canvas);
    }
}
