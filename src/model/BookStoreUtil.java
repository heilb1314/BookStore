package model;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import bean.JsonBean;

public class BookStoreUtil {

    /**
     * Construct an Ajax response in JSON from a list
     *
     * @param list
     * @return {"code": 0|1, "result":[...], "error":"..."}
     */
    public static JsonObject constructAjaxResponse(List<? extends JsonBean> list) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        try {
            if (list == null) throw new Exception("No Item found.");
            JsonArrayBuilder jab = Json.createArrayBuilder();
            for (int i = 0; i < list.size(); i++) {
                jab.add(list.get(i).toJsonObjectBuilder());
            }
            job.add("code", 1).add("result", jab);
        } catch (Exception e) {
            job.add("code", 0).add("error", e.getMessage());
        }
        return job.build();
    }

    /**
     * Construct an Ajax error response in JSON
     *
     * @param errorMessage
     * @return {"code": 0, "error":"..."}
     */
    public static JsonObject constructAjaxErrorResponse(String errorMessage) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("code", 0).add("error", errorMessage);
        return job.build();
    }
}
