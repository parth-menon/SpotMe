package com.steelsty.spotme;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class NewAppWidget extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK_WIDGET";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = Globals.address;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//        Intent in = new Intent(context,NewAppWidget.class);
//        in.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
//        in.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
//        context.sendBroadcast(in);
        Log.e("receive","receive");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Intent configIntent = new Intent(context, WidgetService.class);
        PendingIntent configPendingIntent = PendingIntent.getService(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        Log.e("Update","update");
        for (int appWidgetId : appWidgetIds) {
                        updateAppWidget(context,appWidgetManager, appWidgetId);
                    }
    }
    @Override
    public void onEnabled(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Intent configIntent = new Intent(context, WidgetService.class);
        PendingIntent configPendingIntent = PendingIntent.getService(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget, configPendingIntent);
        Log.e("Enabled","enabled");
        super.onEnabled(context);
    }
}

