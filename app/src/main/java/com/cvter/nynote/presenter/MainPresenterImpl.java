package com.cvter.nynote.presenter;

import android.util.Log;

import com.cvter.nynote.model.NoteInfo;
import com.cvter.nynote.utils.Constants;
import com.cvter.nynote.view.IMainView;


import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by cvter on 2017/6/2.
 */

public class MainPresenterImpl implements IMainPresenter{

    private IMainView mIMainView;
    private static final String TAG = "MainPresenterImpl";

    private ArrayList<NoteInfo> mNotes = new ArrayList<>();

    public MainPresenterImpl(IMainView iMainView){
        this.mIMainView = iMainView;
    }

    @Override
    public boolean deleteNote(String path) {
        File file = new File(Constants.PICTURE_PATH  + path);
        if (file.exists()) { // 是否存在
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) { // 如果它是一个目录
                File[] files = file.listFiles();
                for (File file1 : files) { // 遍历目录下所有的文件
                    this.deleteNote(file1.getName()); // 迭代删除
                }
            }
            return file.delete();
        }
        return false;
    }

    @Override
    public void getNoteImage(final File file) {

        Observable.just(file)
                .flatMap(new Func1<File, Observable<File>>() {
                    @Override
                    public Observable<File> call(File file) {//遍历文件夹
                        return Observable.from(file.listFiles());
                    }
                })
                .filter(new Func1<File, Boolean>() {//过滤图片
                    @Override
                    public Boolean call(File file) {

                        return file.getName().endsWith(".png")||file.getName().endsWith(".jpg")||file.getName().endsWith(".jpeg");
                    }
                })
                .map(new Func1<File, NoteInfo>() {
                    @Override
                    public NoteInfo call(File file) {
                        NoteInfo note = new NoteInfo();
                        note.setNoteName(file.getName());
                        note.setNotePic(file.getPath());
                        return note;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NoteInfo>() {
                    @Override
                    public void onCompleted() {//onNext（）执行完后调用
                        mIMainView.onLoadImagesCompleted(mNotes);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }
                    @Override
                    public void onNext(NoteInfo note) {
                        mNotes.add(note);
                    }
                });

    }

}