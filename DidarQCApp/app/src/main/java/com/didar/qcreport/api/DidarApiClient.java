package com.didar.qcreport.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DidarApiClient {

    private static final String API_KEY = "231020a4-665d-446e-b5d1-2ca5817b4662";
    private static final String BASE_URL = "https://app.didar.me/api";
    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;

    public DidarApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public List<JSONObject> fetchQCActivities() throws IOException, JSONException {
        // ابتدا لیست نوع فعالیت‌ها را گرفته و ID «برگشت از QC» را پیدا می‌کنیم
        int activityTypeId = findActivityTypeId("برگشت از QC");

        List<JSONObject> result = new ArrayList<>();
        int from = 0;
        int limit = 100;

        while (true) {
            JSONObject criteria = new JSONObject();
            criteria.put("From", from);
            criteria.put("Limit", limit);

            if (activityTypeId > 0) {
                criteria.put("ActivityTypeId", activityTypeId);
            } else {
                // fallback: جستجوی متنی در صورت عدم یافتن ID
                criteria.put("Keywords", "برگشت از QC");
            }

            JSONObject payload = new JSONObject();
            payload.put("Criteria", criteria);

            String responseStr = post(BASE_URL + "/activity/search?apikey=" + API_KEY, payload.toString());
            JSONObject response = new JSONObject(responseStr);

            JSONArray items = response.optJSONArray("ResponseData");
            if (items == null) items = response.optJSONArray("Data");
            if (items == null || items.length() == 0) break;

            for (int i = 0; i < items.length(); i++) {
                result.add(items.getJSONObject(i));
            }

            if (items.length() < limit) break;
            from += limit;
        }

        return result;
    }

    public List<JSONObject> fetchActivityTypes() throws IOException, JSONException {
        JSONObject payload = new JSONObject();
        payload.put("Criteria", new JSONObject());

        String responseStr = post(BASE_URL + "/activitytype/list?apikey=" + API_KEY, payload.toString());
        JSONObject response = new JSONObject(responseStr);

        List<JSONObject> types = new ArrayList<>();
        JSONArray items = response.optJSONArray("ResponseData");
        if (items == null) items = response.optJSONArray("Data");
        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                types.add(items.getJSONObject(i));
            }
        }
        return types;
    }

    private int findActivityTypeId(String name) {
        try {
            List<JSONObject> types = fetchActivityTypes();
            for (JSONObject type : types) {
                String title = type.optString("Title", type.optString("Name", ""));
                if (title.equalsIgnoreCase(name) || title.contains("QC") || title.contains("کیو سی")) {
                    return type.optInt("Id", -1);
                }
            }
        } catch (Exception ignored) {}
        return -1;
    }

    private String post(String url, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, JSON_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("خطای سرور: " + response.code());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }
}
