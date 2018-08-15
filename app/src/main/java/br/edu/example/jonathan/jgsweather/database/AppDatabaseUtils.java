package br.edu.example.jonathan.jgsweather.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import br.edu.example.jonathan.jgsweather.R;

import static android.arch.persistence.room.Room.databaseBuilder;

public final class AppDatabaseUtils {

    private static AppDatabase DATABASE;

    private AppDatabaseUtils() {}

    public static AppDatabase getInstance(Context context) {
        if (DATABASE == null) {
            String databaseName = context.getString(R.string.app_name);
            RoomDatabase.Builder<AppDatabase> databseBuilder =
                    databaseBuilder(context, AppDatabase.class, databaseName);
            DATABASE = databseBuilder.build();
        }
        return DATABASE;
    }

    static AppDatabase getInMemoryDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries().build();
    }

    public static void closeQuietly() {
        if (DATABASE != null) {
            DATABASE.close();
            DATABASE = null;
        }
    }

    static void closeQuietly(AppDatabase database) {
        if (database != null) {
            database.close();
        }
    }

}
