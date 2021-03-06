package com.example.hp.sqlite.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.hp.sqlite.model.Contacts;
import com.example.hp.sqlite.model.Event;
import com.example.hp.sqlite.model.Attendance;


public class EventDAO {

    public static final String TAG = "EventDAO";

    // Database fields
    private SQLiteDatabase mDatabase;
    private DBHelper mDbHelper;
    private Context mContext;
    private String[] mAllColumns = {DBHelper.COLUMN_EVENT_ID,
            DBHelper.COLUMN_DATA_START, DBHelper.COLUMN_TIME_START,
            DBHelper.COLUMN_DATA_END, DBHelper.COLUMN_TIME_END, DBHelper.COLUMN_NOTIFICATIONS_START,
            DBHelper.COLUMN_NOTIFICATIONS_END, DBHelper.COLUMN_AUTO_NOTIFICATIONS,
            DBHelper.COLUMN_RADIUS, DBHelper.COLUMN_START_LOCALISATION_X, DBHelper.COLUMN_START_LOCALISATION_Y,
            DBHelper.COLUMN_END_LOCALISATION_X, DBHelper.COLUMN_END_LOCALISATION_Y, DBHelper.COLUMN_REPETITION, DBHelper.COLUMN_MOTHER_ID};

    public EventDAO(Context context) {
        this.mContext = context;
        mDbHelper = new DBHelper(context);
        try {
            open();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException on openning database " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public Event createEvent(String dataStart, String dataEnd, String timeStart, String timeEnd, boolean notificationsStart, boolean notificationsEnd,
                             boolean autoNotifications, int radius, String startLocalisationX, String startLocalisationY, String endLocalisationX,
                             String endLocalisationY, int repetition, long motherId) {

        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_DATA_START, dataStart);
        values.put(DBHelper.COLUMN_TIME_START, timeStart);
        values.put(DBHelper.COLUMN_DATA_END, dataEnd);
        values.put(DBHelper.COLUMN_TIME_END, timeEnd);
        values.put(DBHelper.COLUMN_NOTIFICATIONS_START, notificationsStart);
        values.put(DBHelper.COLUMN_NOTIFICATIONS_END, notificationsEnd);
        values.put(DBHelper.COLUMN_AUTO_NOTIFICATIONS, autoNotifications);
        values.put(DBHelper.COLUMN_RADIUS, radius);
        values.put(DBHelper.COLUMN_START_LOCALISATION_X, startLocalisationX);
        values.put(DBHelper.COLUMN_START_LOCALISATION_Y, startLocalisationY);
        values.put(DBHelper.COLUMN_END_LOCALISATION_X, endLocalisationX);
        values.put(DBHelper.COLUMN_END_LOCALISATION_Y, endLocalisationY);
        values.put(DBHelper.COLUMN_REPETITION, repetition);
        values.put(DBHelper.COLUMN_MOTHER_ID, motherId);


        long insertId = mDatabase
                .insert(DBHelper.TABLE_EVENTS, null, values);
        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns,
                DBHelper.COLUMN_EVENT_ID + " = " + insertId, null, null,
                null, null);
        cursor.moveToFirst();
        Event newEvent = cursorToEvent(cursor);
        cursor.close();
        return newEvent;
    }

    public void deleteEvent(Event event) {
        long id = event.getId();
        System.out.println("the deleted event has the id: " + id);
        mDatabase.delete(DBHelper.TABLE_EVENTS, DBHelper.COLUMN_EVENT_ID
                + " = " + id, null);
    }

    public void editEvent(Long id, String dataStart, String dataEnd, String timeStart, String timeEnd, boolean notificationsStart, boolean notificationsEnd,
                          boolean autoNotifications, int radius, String startLocalisationX, String startLocalisationY, String endLocalisationX,
                          String endLocalisationY, int repetition, long motherId) {
        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_DATA_START, dataStart);
        values.put(DBHelper.COLUMN_DATA_END, dataEnd);
        values.put(DBHelper.COLUMN_TIME_START, timeStart);
        values.put(DBHelper.COLUMN_TIME_END, timeEnd);
        values.put(DBHelper.COLUMN_NOTIFICATIONS_START, notificationsStart);
        values.put(DBHelper.COLUMN_NOTIFICATIONS_END, notificationsEnd);
        values.put(DBHelper.COLUMN_AUTO_NOTIFICATIONS, autoNotifications);
        values.put(DBHelper.COLUMN_RADIUS, radius);
        values.put(DBHelper.COLUMN_START_LOCALISATION_X, startLocalisationX);
        values.put(DBHelper.COLUMN_START_LOCALISATION_Y, startLocalisationY);
        values.put(DBHelper.COLUMN_END_LOCALISATION_X, endLocalisationX);
        values.put(DBHelper.COLUMN_END_LOCALISATION_Y, endLocalisationY);
        values.put(DBHelper.COLUMN_REPETITION, repetition);
        values.put(DBHelper.COLUMN_MOTHER_ID, motherId);

        mDatabase.update(DBHelper.TABLE_EVENTS, values, DBHelper.COLUMN_EVENT_ID + " = " + id, null);
    }


    public List<Event> getAllEvents() {
        List<Event> listEvents = new ArrayList<Event>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event event = cursorToEvent(cursor);
                listEvents.add(event);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return listEvents;
    }

    public List<Event> getPassEvents(String minDate, String maxDate) {
        List<Event> listEvents = new ArrayList<Event>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns, DBHelper.COLUMN_DATA_END + " BETWEEN ? AND ?" + " ORDER BY DATA_END DESC", new String[]{
                minDate, maxDate}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event event = cursorToEvent(cursor);
                listEvents.add(event);
                cursor.moveToNext();
            }

            cursor.close();
        }
        return listEvents;
    }

    public List<Event> getComingEvents(String minDate, String maxDate) {

        List<Event> listEvents = new ArrayList<Event>();

        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns, DBHelper.COLUMN_DATA_END + " BETWEEN ? AND ?" + " ORDER BY DATA_END ASC", new String[]{
                minDate, maxDate}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event event = cursorToEvent(cursor);
                listEvents.add(event);
                cursor.moveToNext();
            }

            cursor.close();
        }
        return listEvents;
    }

    public List<Event> findLastingEvents() {
        List<Event> listEvents = getAllEvents();
        List<Event> lastingEvents = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String actualDate = dateFormat.format(c.getTime());

        for (Event event : listEvents) {
            try {
                Date actual = dateFormat.parse(actualDate);
                Date start = dateFormat.parse(event.getDataStart() + " " + event.getTimeStart());
                Date end = dateFormat.parse(event.getDataEnd() + " " + event.getTimeEnd());

                if (actual.after(start) && actual.before(end)) {
                    lastingEvents.add(event);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return lastingEvents;
    }

    public int countEvents() {

        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns, null, null, null, null, null);
        if (cursor == null) {
            return 0;
        }
        cursor.moveToLast();
        return cursor.getPosition();
    }

    public Event getEventById(long id) {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns,
                DBHelper.COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Event event = cursorToEvent(cursor);
        cursor.close();
        return event;
    }

    public Event getLastEvent() {
        Cursor cursor = mDatabase.query(DBHelper.TABLE_EVENTS, mAllColumns, null, null, null, null, null);
        cursor.moveToLast();
        Event event = cursorToEvent(cursor);
        return event;
    }

    protected Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setId(cursor.getLong(0));
        event.setDataStart(cursor.getString(1));
        event.setTimeStart(cursor.getString(2));
        event.setDataEnd(cursor.getString(3));
        event.setTimeEnd(cursor.getString(4));
        event.setNotificationsStart(cursor.getInt(5) > 0);
        event.setNotificationsEnd(cursor.getInt(6) > 0);
        event.setAutoNotifications(cursor.getInt(7) > 0);
        event.setRadius(cursor.getInt(8));
        event.setStartLocalisationX(cursor.getString(9));
        event.setStartLocalisationY(cursor.getString(10));
        event.setEndLocalisationX(cursor.getString(11));
        event.setEndLocalisationY(cursor.getString(12));
        event.setRepetition(cursor.getInt(13));
        event.setMotherId(cursor.getLong(14));

        return event;
    }
}
