package com.core2plus.oalam.foodstudio.SQLite.ORM;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

/**
 * Created by zhakhanyergali on 21.11.17.
 */

public interface InterfaceORM<T> {

    T cursorToObject(Cursor cursor);

    void add(Context context, T t);

    void clearAll(Context context);

    List<T> getAll(Context context);

}
