package com.technoprobic.ddm.ddm.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.technoprobic.ddm.ddm.R;
import com.technoprobic.ddm.ddm.model.AppDatabase;
import com.technoprobic.ddm.ddm.model.SensorDataCaptureSession;
import com.technoprobic.ddm.ddm.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class ListViewWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListViewWidgetRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int appWidgetId;
        private AppDatabase mDb;
        private List<SensorDataCaptureSession> sensorDataCaptureSessions;

        public ListViewWidgetRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            sensorDataCaptureSessions = new ArrayList<>();
            mDb = AppDatabase.getInstance(mContext);
            //sensorDataCaptureSessions = mDb.sensorDataCaptureSessionDao().loadAllSensorDataCaptureSessionsWidget();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    sensorDataCaptureSessions = mDb.sensorDataCaptureSessionDao().loadAllSensorDataCaptureSessionsWidget();
                }
            });


            /*for(int i=0; i<5; i++) {
                Recipe recipe = new Recipe();
                recipe.setRecipeName("recipe number " + i);
                sensorDataCaptureSessions.add(recipe);
            }
            Log.d("widget", "widget sensorDataCaptureSessions num " + sensorDataCaptureSessions.size()); */


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            SensorDataCaptureSessionsWidget.updateAppWidget(mContext, appWidgetManager, appWidgetId);

        }


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }


        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return sensorDataCaptureSessions.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (position == AdapterView.INVALID_POSITION || sensorDataCaptureSessions == null || sensorDataCaptureSessions.get(position) == null) {
                return null;
            }

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.sensor_data_capture_sessions_widget_list_item);
            rv.setTextViewText(R.id.tv_widget_sensor_data_capture_session_name, sensorDataCaptureSessions.get(position).getUserSessionDescription());

            Intent fillIntent = new Intent();
            fillIntent.putExtra(SensorDataCaptureSessionsWidget.EXTRA_ITEM_POSITION, position);
            fillIntent.putExtra(SensorDataCaptureSessionDetailFragment.BUNDLE_KEY, sensorDataCaptureSessions.get(position));
            rv.setOnClickFillInIntent(R.id.tv_widget_sensor_data_capture_session_name, fillIntent);

            return rv;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }


}
