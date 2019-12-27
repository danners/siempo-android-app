package co.siempo.phone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;


public class GreenDaoOpenHelper extends co.siempo.phone.db.DaoMaster.OpenHelper {
    public GreenDaoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            case 3:
                MigrationHelper.migrate(db, TableNotificationSmsDao.class);
                break;
            case 4:
                break;
        }

    }
}