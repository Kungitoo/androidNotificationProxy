package com.example.mynotificationproxy;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class NotificationObject {

    private Notification n;

    // General
    private String packageName;
    private long postTime;
    private long systemTime;

    private boolean isClearable;
    private boolean isOngoing;

    private long when;
    private int flags;
    private int defaults;

    // Compat
    private String group;
    private boolean isGroupSummary;
    private String category;
    private int actionCount;
    private boolean isLocalOnly;

    private List people;
    private String style;

    // 16
    private int priority;

    // 18
    private int nid;
    private String tag;

    // 20
    private String key;
    private String sortKey;

    // 21
    private int visibility;
    private int color;

    // Text
    private String tickerText;
    private String title;
    private String titleBig;
    private String text;
    private String textBig;
    private String textInfo;
    private String textSub;
    private String textSummary;
    private String textLines;

    NotificationObject(StatusBarNotification sbn) {

        n = sbn.getNotification();
        packageName = sbn.getPackageName();
        postTime = sbn.getPostTime();
        systemTime = System.currentTimeMillis();

        isClearable = sbn.isClearable();
        isOngoing = sbn.isOngoing();

        nid = sbn.getId();
        tag = sbn.getTag();

        if (Build.VERSION.SDK_INT >= 20) {
            key = sbn.getKey();
            sortKey = n.getSortKey();
        }

        extract();
    }

    private void extract() {
        // General
        when = n.when;
        flags = n.flags;
        defaults = n.defaults;

        // 16
        priority = n.priority;

        // 21
        if (Build.VERSION.SDK_INT >= 21) {
            visibility = n.visibility;
            color = n.color;
        }

        // Compat
        group = NotificationCompat.getGroup(n);
        isGroupSummary = NotificationCompat.isGroupSummary(n);
        category = NotificationCompat.getCategory(n);
        actionCount = NotificationCompat.getActionCount(n);
        isLocalOnly = NotificationCompat.getLocalOnly(n);

        Bundle extras = NotificationCompat.getExtras(n);
        if (extras != null) {
            String[] tmp = extras.getStringArray(NotificationCompat.EXTRA_PEOPLE);
            people = tmp != null ? Arrays.asList(tmp) : null;
            style = extras.getString(NotificationCompat.EXTRA_TEMPLATE);
        }

        // Text
        tickerText = nullToEmptyString(n.tickerText);

        if (extras != null) {
            title = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE));
            titleBig = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TITLE_BIG));
            text = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_TEXT));
            textBig = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT));
            textInfo = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_INFO_TEXT));
            textSub = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT));
            textSummary = nullToEmptyString(extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT));

            CharSequence[] lines = extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES);
            if (lines != null) {
                textLines = "";
                for (CharSequence line : lines) {
                    textLines += line + "\n";
                }
                textLines = textLines.trim();
            }
        }
    }

	private static String nullToEmptyString(CharSequence charsequence) {
		if(charsequence == null) {
			return "";
		} else {
			return charsequence.toString();
		}
	}

    @Override
    public String toString() {
        try {
            JSONObject json = new JSONObject();

            json.put("postTime", postTime);
            json.put("systemTime", systemTime);
            
            // Text
            json.put("tickerText", tickerText);
            json.put("title", title);
            json.put("titleBig", titleBig);
            json.put("text", text);
            json.put("textBig", textBig);
            json.put("textInfo", textInfo);
            json.put("textSub", textSub);
            json.put("textSummary", textSummary);
            json.put("textLines", textLines);

            // General
            json.put("packageName", packageName);
            json.put("offset", TimeZone.getDefault().getOffset(systemTime));
            json.put("version", BuildConfig.VERSION_CODE);
            json.put("sdk", android.os.Build.VERSION.SDK_INT);

            json.put("isOngoing", isOngoing);
            json.put("isClearable", isClearable);

            json.put("when", when);
            json.put("flags", flags);
            json.put("defaults", defaults);

            // Compat
            json.put("group", group);
            json.put("isGroupSummary", isGroupSummary);
            json.put("category", category);
            json.put("actionCount", actionCount);
            json.put("isLocalOnly", isLocalOnly);

            json.put("people", people == null ? 0 : people.size());
            json.put("style", style);

            // 16
            json.put("priority", priority);

            // 18
            json.put("nid", nid);
            json.put("tag", tag);

            // 20
            if (Build.VERSION.SDK_INT >= 20) {
                json.put("key", key);
                json.put("sortKey", sortKey);
            }

            // 21
            if (Build.VERSION.SDK_INT >= 21) {
                json.put("visibility", visibility);
                json.put("color", color);
            }

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
