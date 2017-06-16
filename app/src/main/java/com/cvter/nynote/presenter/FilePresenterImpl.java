package com.cvter.nynote.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Xml;

import com.cvter.nynote.R;
import com.cvter.nynote.model.PathDrawingInfo;
import com.cvter.nynote.model.PathInfo;
import com.cvter.nynote.model.PointInfo;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.utils.ImportListener;
import com.cvter.nynote.utils.SaveListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cvter on 2017/6/9.
 */

public class FilePresenterImpl implements IFilePresenter {

    private static final String PATH_LIST = "PathList";
    private static final String DRAW_PATH = "DrawPath";
    private static final String PAINT = "Paint";
    private static final String COLOR = "color";
    private static final String ALPHA = "alpha";
    private static final String WIDTH = "width";
    private static final String TYPE = "type";
    private static final String GRAPH_TYPE = "GraphType";
    private static final String PATH = "Path";
    private static final String POINT = "point";
    private static final String CODE_TYPE = "UTF-8";

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    public FilePresenterImpl(Context context){
        this.mContext = context;
    }

    @Override
    public void saveAsXML(final List<PathInfo> pathList, final String path, final SaveListener listener) {

        new HandlerThread("saveASXml"){
            @Override
            public void run() {

                OutputStream outputStream = null;
                try {
                    File file = new File(path);
                    if(file.createNewFile()){
                        outputStream = new FileOutputStream(file);

                        XmlSerializer serializer = Xml.newSerializer();
                        serializer.setOutput(outputStream, CODE_TYPE);
                        serializer.startDocument(CODE_TYPE, true);
                        serializer.startTag(null, PATH_LIST);
                        for (PathInfo pathInfo : pathList) {
                            serializer.startTag(null, DRAW_PATH);
                            Paint paint = pathInfo.getPaint();
                            serializer.startTag(null, PAINT);

                            serializer.startTag(null, COLOR);
                            serializer.text(paint.getColor() + "");
                            serializer.endTag(null, COLOR);

                            serializer.startTag(null, ALPHA);
                            serializer.text(paint.getAlpha() + "");
                            serializer.endTag(null, ALPHA);

                            serializer.startTag(null, WIDTH);
                            serializer.text(paint.getStrokeWidth() + "");
                            serializer.endTag(null, WIDTH);

                            serializer.startTag(null, TYPE);
                            serializer.text(String.valueOf(pathInfo.getPaintType()));
                            serializer.endTag(null, TYPE);

                            serializer.startTag(null, GRAPH_TYPE);
                            serializer.text(String.valueOf(pathInfo.getGraphType()));
                            serializer.endTag(null, GRAPH_TYPE);

                            serializer.endTag(null, PAINT);

                            serializer.startTag(null, PATH);
                            ArrayList<PointInfo> mPoints = pathInfo.getPointList();
                            for (PointInfo points : mPoints) {
                                serializer.startTag(null, POINT);
                                serializer.text(points.mPointX + "," + points.mPointY);
                                serializer.endTag(null, POINT);
                            }
                            serializer.endTag(null, PATH);

                            serializer.endTag(null, DRAW_PATH);
                        }
                        serializer.endTag(null, PATH_LIST);
                        serializer.endDocument();

                        outputStream.flush();
                        outputStream.close();
                    }

                } catch (Exception e){
                    saveFail(listener);
                } finally {
                    if (outputStream != null){
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            saveFail(listener);
                        }
                    }
                }
            }
        }.start();

    }

    @Override
    public void saveAsImg(final Bitmap bitmap, final String path, final SaveListener listener) {

        new HandlerThread("saveAsPicture"){
            @Override
            public void run() {
                try{
                    File file = new File(path);
                    if (file.createNewFile()){

                        Bitmap compressBitmap = Constants.getCompressBitmap(bitmap);
                        OutputStream outputStream = new FileOutputStream(file);
                        compressBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess();
                            }
                        });
                    }

                }catch (final Exception e){
                    saveFail(listener);
                }
            }
        }.start();

    }

    @Override
    public void importXML(final String filePath, final ImportListener listener) {

        new HandlerThread("importXml"){
            @Override
            public void run() {
                try {
                    final ArrayList<PathInfo> drawPathList = new ArrayList<>();
                    PathInfo drawPath = null;
                    Path path = null;
                    Paint paint = null;
                    ArrayList<PointInfo> pointList = null;
                    boolean isFirstPoint = true;
                    float startX = 0f;
                    float startY = 0f;

                    InputStream is = new FileInputStream(new File(filePath));
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(is, CODE_TYPE);
                    int eventType = parser.getEventType();
                    while (XmlPullParser.END_DOCUMENT != eventType) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:

                                break;

                            case XmlPullParser.START_TAG:
                                String startTag = parser.getName();
                                switch (startTag) {
                                    case DRAW_PATH:
                                        drawPath = new PathDrawingInfo();
                                        isFirstPoint = true;
                                        break;
                                    case PAINT:
                                        paint = new Paint();
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeCap(Paint.Cap.ROUND);
                                        paint.setAntiAlias(true);

                                        break;

                                    case COLOR:
                                        if (paint != null){
                                            paint.setColor(Integer.parseInt(parser.nextText().trim()));
                                        }
                                        break;

                                    case ALPHA:
                                        if (paint != null){
                                            paint.setAlpha(Integer.parseInt(parser.nextText().trim()));
                                        }
                                        break;

                                    case WIDTH:
                                        if (paint != null){
                                            paint.setStrokeWidth(Float.parseFloat(parser.nextText().trim()));
                                        }
                                        break;

                                    case TYPE:
                                        int type = Integer.parseInt(parser.nextText().trim());
                                        PorterDuff.Mode mode = ( type == 0) ? null : PorterDuff.Mode.CLEAR;
                                        if(mode != null && paint != null){
                                            paint.setXfermode(new PorterDuffXfermode(mode));
                                        }else if(mode == null && paint != null){
                                            paint.setXfermode(null);
                                        }
                                        if (drawPath != null){
                                            drawPath.setPaintType(type);
                                        }
                                        break;

                                    case GRAPH_TYPE:
                                        int graphType = Integer.parseInt(parser.nextText().trim());
                                        if (drawPath != null){
                                            drawPath.setGraphType(graphType);
                                        }
                                        break;

                                    case PATH:
                                        path = new Path();
                                        pointList = new ArrayList<>();
                                        break;

                                    case POINT:
                                        PointInfo point = new PointInfo();
                                        String[] pointArr = parser.nextText().trim().split(",");
                                        point.mPointX = Float.parseFloat(pointArr[0]);
                                        point.mPointY = Float.parseFloat(pointArr[1]);
                                        if (isFirstPoint && path != null) {
                                            startX = point.mPointX;
                                            startY = point.mPointY;
                                            path.moveTo(startX, startY);
                                            isFirstPoint = false;
                                        }
                                        if (pointList != null ){
                                            pointList.add(point);
                                        }

                                        if (drawPath != null){
                                            handleGraphType(path, startX, startY, point.mPointX, point.mPointY, drawPath.getGraphType());
                                        }
                                        startX = point.mPointX;
                                        startY = point.mPointY;
                                        break;

                                    default:
                                        break;
                                }
                                break;

                            case XmlPullParser.END_TAG:
                                String endTag = parser.getName();
                                switch (endTag) {

                                    case PATH:
                                        if(drawPath != null ){
                                            drawPath.setPath(path);
                                            drawPath.setPointList(pointList);
                                        }
                                        break;

                                    case PAINT:
                                        if(drawPath != null ) {
                                            drawPath.setPaint(paint);
                                        }
                                        break;

                                    case DRAW_PATH:
                                        if(drawPath != null ) {
                                            drawPathList.add(drawPath);
                                        }
                                        break;

                                    default:
                                        break;

                                }
                                break;

                            default:
                                break;
                        }
                        eventType = parser.next();
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(drawPathList);
                        }
                    });

                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail(mContext.getString(R.string.import_fail));
                        }
                    });
                }
            }
        }.start();

    }

    private void saveFail(final SaveListener listener){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFail(mContext.getString(R.string.save_fail));
            }
        });
    }

    // 根据Path的类型来处理Path
    private void handleGraphType(Path path, float startX, float startY, float x, float y, int type) {
        switch (type) {
            case Constants.ORDINARY:
                float endX = (x + startX) / 2;
                float endY = (y + startY) / 2;
                path.quadTo(startX, startY, endX, endY);
                break;
            case Constants.CIRCLE:
                RectF rectF = new RectF(startX, startY, x, y);
                path.addOval(rectF, Path.Direction.CW);
                break;
            case Constants.LINE:
                path.moveTo(startX, startY);
                path.lineTo(x, y);
                break;
            case Constants.SQUARE:
                path.addRect(Math.min(startX, x), Math.min(startY, y), Math.max(x, startX), Math.max(y, startY), Path.Direction.CW);
                break;
            case Constants.DELTA:
                float radiusD = (y - startY) * 2 / 3;
                path.moveTo(startX, startY);
                float spaceXD = (float) (radiusD*(Math.sin(Math.PI*60/180)));
                float spaceYD = (float) (radiusD + radiusD*(Math.cos(Math.PI*60/180)));
                path.lineTo((startX + spaceXD), (startY + spaceYD));
                path.moveTo((startX + spaceXD), (startY + spaceYD));
                path.lineTo((startX - spaceXD), (startY + spaceYD));
                path.moveTo((startX - spaceXD), (startY + spaceYD));
                path.lineTo(startX, startY);
                break;
            case Constants.PENTAGON:
                float radiusP = y - startY;
                path.moveTo(startX, startY - radiusP);
                float spaceXP = (float) (radiusP*(Math.sin(Math.PI*72/180)));
                float spaceYP = (float) (radiusP*(Math.cos(Math.PI*72/180)));

                float spaceX2P = (float)(radiusP*(Math.sin(Math.PI*36/180)));
                float spaceY2P = (float)(radiusP*(Math.cos(Math.PI*36/180)));

                path.lineTo((startX + spaceXP), (startY - spaceYP));
                path.moveTo((startX + spaceXP), (startY - spaceYP));

                path.lineTo((startX + spaceX2P), (startY + spaceY2P));
                path.moveTo((startX + spaceX2P), (startY + spaceY2P));

                path.lineTo((startX - spaceX2P), (startY + spaceY2P));
                path.moveTo((startX - spaceX2P), (startY + spaceY2P));

                path.lineTo((startX - spaceXP), (startY - spaceYP));
                path.moveTo((startX - spaceXP), (startY - spaceYP));

                path.lineTo(startX, startY - radiusP);
                break;
            case Constants.STAR:
                float radiusS = y - startY;
                path.moveTo(startX, startY - radiusS);
                float spaceXS = (float) (radiusS*(Math.sin(Math.PI*72/180)));
                float spaceYS = (float) (radiusS*(Math.cos(Math.PI*72/180)));

                float spaceX2S = (float)(radiusS*(Math.sin(Math.PI*36/180)));
                float spaceY2S = (float)(radiusS*(Math.cos(Math.PI*36/180)));

                path.lineTo((startX + spaceX2S), (startY + spaceY2S));
                path.moveTo((startX + spaceX2S), (startY + spaceY2S));

                path.lineTo((startX - spaceXS), (startY - spaceYS));
                path.moveTo((startX - spaceXS), (startY - spaceYS));

                path.lineTo((startX + spaceXS), (startY - spaceYS));
                path.moveTo((startX + spaceXS), (startY - spaceYS));

                path.lineTo((startX - spaceX2S), (startY + spaceY2S));
                path.moveTo((startX - spaceX2S), (startY + spaceY2S));


                path.lineTo(startX, startY - radiusS);
                break;
            default:
                break;
        }
    }

}
