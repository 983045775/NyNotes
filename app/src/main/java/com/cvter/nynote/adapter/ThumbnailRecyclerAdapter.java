package com.cvter.nynote.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvter.nynote.activity.DrawActivity;
import com.cvter.nynote.activity.MainActivity;
import com.cvter.nynote.model.NoteInfo;
import com.cvter.nynote.R;
import com.cvter.nynote.utils.Constants;

import java.io.File;
import java.util.List;

/**
 * Created by serenefang on 2017/6/5.
 * 主页面缩略图适配器RecyclerViewAdapter
 */

public class ThumbnailRecyclerAdapter extends RecyclerView.Adapter<ThumbnailRecyclerAdapter.ThumbnailViewHolder> {

    private MainActivity mContext;
    private List<NoteInfo> mNotes;

    private boolean ifDeleteSuccess;
    private static final String TAG = "ThumbnailAdapter";
    private StringBuilder path = new StringBuilder();

    public ThumbnailRecyclerAdapter(Activity mContext, List<NoteInfo> notes) {
        this.mContext = (MainActivity) mContext;
        this.mNotes = notes;
    }

    //清除笔记列表
    public void clearData() {
        mNotes.clear();
    }

    @Override
    public ThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(mContext, R.layout.item_thum_recyclerview, null);
        return new ThumbnailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThumbnailViewHolder holder, final int position) {

        if (mNotes != null && mNotes.get(position) != null) {
            Glide.with(mContext).load(mNotes.get(position).getNotePic()).into(holder.thumbnailImageView);
            String name = mNotes.get(position).getNoteName().replace(".png", "");
            holder.thumbnailTextView.setText(name);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemDelete(position);
                    return true;
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNotes.get(position) != null) {
                        Intent intent = new Intent(mContext, DrawActivity.class);
                        intent.putExtra("noteName", mNotes.get(position).getNoteName());
                        intent.putExtra("skipType", "read_note");
                        mContext.startActivity(intent);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (mNotes != null && !mNotes.isEmpty()) {
            return mNotes.size();
        }
        return 0;
    }

    //删除笔记
    private void onItemDelete(final int position) {

        final AlertDialog isExit = new AlertDialog.Builder(mContext).create();
        isExit.setTitle(mContext.getString(R.string.tips));
        if (position < mNotes.size() && mNotes.get(position) != null && !mNotes.get(position).getNoteName().isEmpty()) {
            path.setLength(0);
            path.append(mContext.getString(R.string.delete_note)).append(" [ ").append(mNotes.get(position).getNoteName().replace(mContext.getString(R.string.png), " ]"));
            isExit.setMessage(path.toString());
            isExit.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.sure), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onDeleteHandle(position);
                }
            });
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isExit.dismiss();
                }
            });
            isExit.show();
        }


    }

    //具体删除操作
    private void onDeleteHandle(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    path.setLength(0);
                    path.append(Constants.PICTURE_PATH).append(mNotes.get(position).getNoteName());
                    File file1 = new File(path.toString());
                    ifDeleteSuccess = mContext.getIMainPresenter().deleteNote(file1);
                    path.setLength(0);
                    path.append(Constants.NOTE_PATH).append(mNotes.get(position).getNoteName().replace(".png", ""));
                    File file = new File(path.toString());
                    mContext.getIMainPresenter().deleteNote(file);

                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ifDeleteSuccess) {
                            mNotes.remove(position);
                            notifyItemRemoved(position);
                            mContext.showToast(mContext.getString(R.string.delete_success));
                        } else {
                            mContext.showToast(mContext.getString(R.string.delete_fail));
                        }
                    }
                });

            }
        }).start();
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView thumbnailTextView;

        private ThumbnailViewHolder(View itemView) {
            super(itemView);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_imageView);
            thumbnailTextView = (TextView) itemView.findViewById(R.id.thumbnail_textView);
        }
    }
}
