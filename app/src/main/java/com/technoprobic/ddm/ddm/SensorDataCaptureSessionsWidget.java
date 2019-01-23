package com.technoprobic.ddm.ddm;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailActivity;
import com.technoprobic.ddm.ddm.ui.SensorDataCaptureSessionDetailFragment;

// todo - remove after capstone
public class SensorDataCaptureSessionsWidget extends AppWidgetProvider {

    public static final String ACTION_DETAIL = "SENSOR_DATA_CAPTURE_SESSION_DETAIL";
    public static final String EXTRA_ITEM_POSITION = "SENSOR_DATA_CAPTURE_SESSION_ITEM_POSITION";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sensor_data_capture_sessions_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent serviceIntent = new Intent(context, ListViewWidgetRemoteViewsService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widgetListView, serviceIntent);
        views.setEmptyView(R.id.widgetListView, R.id.widgetEmptyView);

        Intent clickIntent = new Intent(context, SensorDataCaptureSessionsWidget.class);
        clickIntent.setAction(ACTION_DETAIL);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
        views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntent);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_DETAIL.equals(intent.getAction())) {

            SensorDataCaptureSession sensorDataCaptureSession = intent.getParcelableExtra(SensorDataCaptureSessionDetailFragment.BUNDLE_KEY);

            Intent sensorDataCaptureSessionDetailIntent = new Intent(context, SensorDataCaptureSessionDetailActivity.class);
            sensorDataCaptureSessionDetailIntent.putExtra(SensorDataCaptureSessionDetailFragment.BUNDLE_KEY, sensorDataCaptureSession);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, sensorDataCaptureSessionDetailIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

        }

        super.onReceive(context, intent);
    }


}

