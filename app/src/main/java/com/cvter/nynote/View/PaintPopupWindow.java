package com.cvter.nynote.View;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cvter.nynote.Activity.DrawActivity;
import com.cvter.nynote.R;
import com.cvter.nynote.Utils.Constants;


/**
 * Created by cvter on 2017/6/8.
 */

public class PaintPopupWindow extends BasePopupWindow implements View.OnClickListener {

    private DrawActivity mContext;

    private ImageView pencilImageView, fountainImageView, dropperImageView,
            brushImageView, brushWideImageView;
    private ImageView blackImageView, blueImageView, greenImageView,
            yellowImageView, redImageView;
    private SeekBar widthSeekBar;
    private TextView progressTextView;


    public PaintPopupWindow(DrawActivity context, int width, int height){
        super(context, width, height);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        View viewPaint = LayoutInflater.from(mContext).inflate(R.layout.window_pen_species, null);
        this.setContentView(viewPaint);

        pencilImageView = (ImageView) viewPaint.findViewById(R.id.pencil_imageView);
        fountainImageView = (ImageView) viewPaint.findViewById(R.id.fountain_imageView);
        dropperImageView = (ImageView) viewPaint.findViewById(R.id.dropper_imageView);
        brushImageView = (ImageView) viewPaint.findViewById(R.id.brush_imageView);
        brushWideImageView = (ImageView) viewPaint.findViewById(R.id.brush_wide_imageView);

        widthSeekBar = (SeekBar) viewPaint.findViewById(R.id.paint_width_seekBar);
        progressTextView = (TextView) viewPaint.findViewById(R.id.progress_textView);

        blackImageView = (ImageView) viewPaint.findViewById(R.id.black_imageView);
        greenImageView = (ImageView) viewPaint.findViewById(R.id.green_imageView);
        blueImageView = (ImageView) viewPaint.findViewById(R.id.blue_imageView);
        redImageView = (ImageView) viewPaint.findViewById(R.id.red_imageView);
        yellowImageView = (ImageView) viewPaint.findViewById(R.id.yellow_imageView);

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mContext.paintWidth = i;
                progressTextView.setText(i + "");
                mContext.eraserImageView.setSelected(false);
                mContext.drawPaintView.mPaint.setMode(Constants.Mode.DRAW);
                mContext.drawPaintView.mPaint.setPenRawSize(DrawActivity.paintWidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setListener() {
        pencilImageView.setOnClickListener(this);
        fountainImageView.setOnClickListener(this);
        dropperImageView.setOnClickListener(this);
        brushImageView.setOnClickListener(this);
        brushWideImageView.setOnClickListener(this);

        blackImageView.setOnClickListener(this);
        greenImageView.setOnClickListener(this);
        blueImageView.setOnClickListener(this);
        yellowImageView.setOnClickListener(this);
        redImageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pencil_imageView:
                mContext.drawPaintView.mPaint.setOrdinaryPen();
                break;

            case R.id.fountain_imageView:
                mContext.drawPaintView.mPaint.setDashPen();
                break;

            case R.id.dropper_imageView:
                mContext.drawPaintView.mPaint.setTransPen();
                break;

            case R.id.brush_imageView:
                mContext.drawPaintView.mPaint.setDiscretePen();
                break;

            case R.id.brush_wide_imageView:
                mContext.drawPaintView.mPaint.setInkPen();
                break;

            case R.id.black_imageView:
                mContext.drawPaintView.mPaint.setPenColor(Color.BLACK);
                break;

            case R.id.green_imageView:
                mContext.drawPaintView.mPaint.setPenColor(Color.GREEN);
                break;

            case R.id.blue_imageView:
                mContext.drawPaintView.mPaint.setPenColor(Color.BLUE);
                break;

            case R.id.red_imageView:
                mContext.drawPaintView.mPaint.setPenColor(Color.RED);
                break;

            case R.id.yellow_imageView:
                mContext.drawPaintView.mPaint.setPenColor(Color.YELLOW);
                break;
        }
    }
}