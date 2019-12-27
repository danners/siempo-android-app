package co.siempo.phone.db;

import java.util.List;

import co.siempo.phone.log.Tracer;


public class DBClient {

    public void deleteMsgByPackageName(String packageName) {
        try {
            List<TableNotificationSms> tableNotificationSms = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties.PackageName.eq(packageName)).list();
            if (tableNotificationSms != null && tableNotificationSms.size() > 0) {
                Tracer.i("Deleting Msg by PackageName");
                DBUtility.getNotificationDao().deleteInTx(tableNotificationSms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
