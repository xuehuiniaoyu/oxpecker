package xuehuiniaoyu.github.oxpecker.view;

import android.content.Context;
import android.view.View;

import com.github.jknack.handlebars.Handlebars;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.ny.woods.layout.widget.HView;
import org.ny.woods.template.HTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataBoundView<T extends View> extends HView<T> {
    private HTemplate privateHTemplate;
    public DataBoundView(Context context, JsonValue value) {
        super(context, value);
        privateHTemplate = new HTemplate();
    }


    class ForEach {
        String list;
        String item;
        HashMap<String, String> var;

        public ForEach(JsonValue value) {
            var = new HashMap<>();
            JsonObject jsonObject = value.asObject();
            list = jsonObject.getString("list", null);
            item = jsonObject.getString("item", null);
            JsonObject asObj = jsonObject.get("var").asObject();
            Iterator<JsonObject.Member> iterator = asObj.iterator();
            while (iterator.hasNext()) {
                JsonObject.Member member = iterator.next();
                var.put(member.getName(), (Handlebars.DELIM_START + member.getValue() + Handlebars.DELIM_END).replace("\"", ""));
            }
        }
    }


    private List<ForEach> forEaches;

    /**
     * ForEach
     *
     * @param value
     */
    public void setBound(JsonValue value) {
        if (forEaches == null) {
            forEaches = new ArrayList<>();
        }
        forEaches.add(new ForEach(value));
    }

    @Override
    public void onAdapterGetView(int position, JsonObject positionData, HTemplate privateHTemplate) {
        if (forEaches != null) {
            for (ForEach forEach : forEaches) {
                Object extObj = getOxpecker().getTemplate().get(forEach.list);
                Object positionObj = null;
                if (extObj instanceof List) {
                    positionObj = ((List) extObj).get(position);
                } else if (extObj instanceof HashMap) {
                    positionObj = ((HashMap) extObj).get(position);
                } else if (extObj instanceof Object[]) {
                    positionObj = ((Object[]) extObj)[position];
                }
                if (positionObj != null) {
                    privateHTemplate.as(forEach.item, positionObj);
                    for (String key : forEach.var.keySet()) {
                        privateHTemplate.as(key, forEach.var.get(key));
                    }
                }
            }
        }
    }


    /**
     * 获取内容
     * @param hTemplate
     * @param source
     * @return
     * @throws IOException
     */
    protected String apply(HTemplate hTemplate, String source) throws IOException {
        String result = hTemplate.apply(source);
        if(forEaches != null) {
            return hTemplate.apply(result);
        }
        return result;
    }
}
