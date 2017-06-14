package com.cvter.nynote.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.cvter.nynote.R;
import com.cvter.nynote.view.SaveListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by cvter on 2017/6/9.
 */

public class FilePresenterImpl implements IFilePresenter {

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    public FilePresenterImpl(Context context){
        this.mContext = context;
    }

    @Override
    public void saveAsXML() {

    }

    @Override
    public void saveAsImg(final Bitmap bitmap, final String path, final SaveListener listener) {

        new HandlerThread("saveAsPicture"){
            @Override
            public void run() {
                try{
                    File file = new File(path);
                    if (file.createNewFile()){
                        OutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        outputStream.close();

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess();
                            }
                        });
                    }

                }catch (final Exception e){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail(mContext.getString(R.string.save_fail));
                        }
                    });
                }
            }
        }.start();

    }

    @Override
    public void importXML() {

    }
}