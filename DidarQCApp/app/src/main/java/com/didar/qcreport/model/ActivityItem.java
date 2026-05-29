package com.didar.qcreport.model;

import org.json.JSONObject;

public class ActivityItem {

    public final String title;
    public final String typeName;
    public final String contactName;
    public final String dealTitle;
    public final String ownerName;
    public final String date;
    public final String description;
    public final String status;

    public ActivityItem(JSONObject json) {
        title       = opt(json, "Title", "Subject", "ActivitySubject");
        typeName    = opt(json, "ActivityTypeName", "TypeName");
        contactName = opt(json, "ContactName", "PersonName");
        dealTitle   = opt(json, "DealTitle", "OpportunityTitle");
        ownerName   = opt(json, "OwnerName", "AssigneeName");
        date        = opt(json, "ActivityDate", "DueDate", "CreateDate");
        description = opt(json, "Description", "Note");
        status      = opt(json, "StatusName", "Status");
    }

    private String opt(JSONObject json, String... keys) {
        for (String key : keys) {
            String val = json.optString(key, "").trim();
            if (!val.isEmpty() && !val.equals("null")) return val;
        }
        return "";
    }
}
