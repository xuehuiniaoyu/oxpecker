package org.ny.woods.layout.widget.base;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import org.hjson.JsonValue;
import org.ny.woods.dimens.HDimens;
import org.ny.woods.exception.HException;
import org.ny.woods.layout.tools.FunctionExec;
import org.ny.woods.layout.tools.UiFuncExec;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.parser.Oxpecker;
import org.ny.woods.template.HTemplate;
import org.ny.woods.utils.HAsyncTask;

import java.io.IOException;
import java.util.HashMap;

public class TextWapper<T extends TextView> extends HView<T> {
    private HTemplate hTemplate;
    public TextWapper(Context context, JsonValue value) {
        super(context, value);
        mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mView.setIncludeFontPadding(false);
    }

    @Override
    public void setOxpecker(Oxpecker oxpecker) {
        super.setOxpecker(oxpecker);
        hTemplate = new HTemplate();
    }

    private JsonValue textTemplate;
    private HAsyncTask<String, CharSequence> mTextAsyncTask;
    // 设置内容
    public void setText(JsonValue value) {
        String text = value.asString();
        if(textTemplate == null) {
            textTemplate = value;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (mTextAsyncTask == null) {
                mTextAsyncTask = new HAsyncTask<String, CharSequence>(getContext().getMainLooper()) {
                    @Override
                    protected CharSequence doInBackground(String... strings) {
                        String text = strings[0];
                        if (!HTemplate.isTemplate(text)) {
                            textTemplate = null;
                        }
                        if (textTemplate != null) {
                            if (hTemplate != null) {
                                try {
                                    String tmp = hTemplate.apply(text);
                                    return tmp;
                                    /*String tmp = hTemplate.apply(text)
                                            .replace("\\&quot;", "&auot;")
                                            .replace("&quot;", "")
                                            .replace("&auot;", "\"");
                                    //.replace("\\&quot;", "\"").replace("&quot;", "");
                                    return tmp;*/
                                } catch (IOException e) {
                                    throw new HException(text + "--------- template error!");
                                }
                            }
                            return null;
                        } else {
                            return text;
                        }
                    }

                    @Override
                    protected void onPostExecute(CharSequence result) {
                        if (result != null) {
                            mView.setText(result);
                        }
                    }
                };
            }
            mTextAsyncTask.exec(text);
        }

        /*if(text.contains("{{") && text.contains("}}")) {
            if (textTemplate == null) {
                textTemplate = value;
                return;
            }
            if(hTemplate != null) {
                try {
                    mView.setText(hTemplate.apply(text).replace("&quot;", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            mView.setText(text);
        }*/
    }

    /**
     * 设置字体颜色
     * textColor: "#ffffff"
     *
     * @param value
     */
    public void setTextColor(JsonValue value) {
        mView.setTextColor(parseColor(value));
    }

    public void setTextSize(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec<JsonValue>("setTextSize", value) {
            @Override
            protected void onExec(JsonValue value) {
                float percentSize = getDimens().getPxSize(value, HDimens.AUTO).getSize();
                mView.setTextSize(TypedValue.COMPLEX_UNIT_PX, percentSize);
            }
        });
    }

    public void setTextAlign(JsonValue value) {
        String valueString = value.asString();
        int gravity = 0;
        for(String str : valueString.split("\\|")) {
            switch (str) {
                case "left":
                    gravity |= Gravity.LEFT;
                    break;
                case "right":
                    gravity |= Gravity.RIGHT;
                    break;
                case "top":
                    gravity |= Gravity.TOP;
                    break;
                case "bottom":
                    gravity |= Gravity.BOTTOM;
                    break;
                case "center":
                    gravity |= Gravity.CENTER;
                    break;
                case "center_vertical":
                case "center_v":
                case "centerV":
                    gravity |= Gravity.CENTER_VERTICAL;
                    break;
                case "center_horizontal":
                case "center_h":
                case "centerH":
                    gravity |= Gravity.CENTER_HORIZONTAL;
                    break;
            }
        }
        mView.setGravity(gravity);
    }

    public void setLines(JsonValue value) {
        int lines = value.asInt();
        mView.setLines(lines);
        mView.setSingleLine(lines == 1);
    }

    public void setTextOverFlow(JsonValue value) {
        switch (value.asString()) {
            case "end":
                mView.setEllipsize(TextUtils.TruncateAt.END);
                break;
            case "marquee":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE_1_1) {
                    mView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                }
                break;
            case "start":
                mView.setEllipsize(TextUtils.TruncateAt.START);
                break;
            case "middle":
                mView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
        }
    }

    public void setTextBold(JsonValue value) {
        mView.getPaint().setFakeBoldText(value.asBoolean());
    }

    public void setSelected(JsonValue value) {
        boolean selected = value.asBoolean();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mView.setTextIsSelectable(selected);
        }
        mView.setSelected(selected);
    }

    /**
     * 文本改变监听
     * @param value
     */
    public void setOnTextChange(JsonValue value) {
        mAfterReadyUiFuncExecList.add(new UiFuncExec<JsonValue>("setOnTextChange", value) {
            @Override
            protected void onExec(final JsonValue value) {
                mView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        FunctionExec exec = new FunctionExec(value, s);
                        exec.exec(getContext(), getJsChannel(), getReflect());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
    }

    @Override
    public void onAdapterGetView(HashMap<String, Object> map) {
        if (textTemplate != null) {
            hTemplate.asAll(map);
            setText(textTemplate);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if(mTextAsyncTask != null) {
            mTextAsyncTask.cancel();
            mTextAsyncTask = null;
        }
    }
}
