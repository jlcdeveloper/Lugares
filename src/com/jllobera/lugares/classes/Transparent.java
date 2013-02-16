package com.jllobera.lugares.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 *
 * Crea una capa transparente como fondo
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 15/10/11
 * Time: 19:55
 */
public class Transparent extends LinearLayout
{
	private Paint innerPaint, borderPaint ;

	public Transparent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Transparent(Context context) {
		super(context);
		init();
	}

	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {

    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());

    	canvas.drawRoundRect(drawRect, 0, 0, innerPaint);
		canvas.drawRoundRect(drawRect, 0, 0, borderPaint);

		super.dispatchDraw(canvas);
    }
}
