package xuehuiniaoyu.github.oxpecker.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

/**
 * 结束确认Dialog
 */
public class FinishDialog {
    private static AlertDialog topAlertDialog;

    private Context context;
    private View tagView; // 需要改变的View
    private OnResultListener onResultListener;

    private String text1, text2;
    private String title;

    private boolean cancelable = true;
    public FinishDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public FinishDialog(Context context, String text1, String text2, String title) {
        this.context = context;
        this.text1 = text1;
        this.text2 = text2;
        this.title = title;
    }

    public FinishDialog setTagView(View tagView) {
        this.tagView = tagView;
        return this;
    }

    /**
     * 结果回到接口
     * @param onResultListener
     * @return
     */
    public FinishDialog setOnResultListener(OnResultListener onResultListener) {
        this.onResultListener = onResultListener;
        return this;
    }

    public void show(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(text1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(onResultListener != null) {
                    onResultListener.onResult(true, tagView);
                }
            }
        });
        builder.setNegativeButton(text2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(onResultListener != null) {
                    onResultListener.onResult(false, tagView);
                }
            }
        });
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.setTitle(title);
        dismiss();
        try {
            topAlertDialog = builder.show();
        } catch (Throwable e) {
            Log.e("Utils", "销毁");
        }
    }

    public void dismiss() {
        if(topAlertDialog != null) {
            topAlertDialog.dismiss();
            topAlertDialog = null;
        }
    }

    public interface OnResultListener {
        void onResult(boolean result, View tagView);
    }
}
