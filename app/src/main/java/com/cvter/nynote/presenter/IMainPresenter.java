package com.cvter.nynote.presenter;

import java.io.File;

/**
 * Created by cvter on 2017/6/2.
 */

public interface IMainPresenter {

    boolean deleteNote(String path);
    void getNoteImage(File file);

}