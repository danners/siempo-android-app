package co.siempo.phone.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.models.CustomNotification;
import co.siempo.phone.models.MainListItem;

import static java.util.Collections.sort;

/**
 * Created by rajeshjadi on 9/2/18.
 */

public class Sorting {
    /**
     * Sort the application alphabetically.
     *
     * @param list
     * @return
     */
    public synchronized static List<App> sortAppAssignment(List<App> list) {
        sort(list, new Comparator<App>() {
            @Override
            public int compare(final App object1, final App object2) {
                if (object1 != null && object2 != null) {
                    return object1.displayName.compareTo
                            (object2.displayName);
                } else {
                    return 1;
                }
            }
        });
        return list;
    }

    /**
     * Sort the application alphabetically.
     *
     * @param list
     * @return
     */
    public synchronized static ArrayList<String> sortJunkAppAssignment(ArrayList<String> list) {
        sort(list, new Comparator<String>() {
            @Override
            public int compare(final String object1, final String object2) {
                String strObj1;
                String strObj2;
                if (CoreApplication.getInstance().getApplicationName(object1) != null) {
                    strObj1 = CoreApplication.getInstance().getApplicationName(object1);
                } else {
                    strObj1 = CoreApplication.getInstance().getApplicationNameFromPackageName(object1);
                }

                if (CoreApplication.getInstance().getApplicationName(object2) != null) {
                    strObj2 = CoreApplication.getInstance().getApplicationName(object2);
                } else {
                    strObj2 = CoreApplication.getInstance().getApplicationNameFromPackageName(object2);
                }

                return (strObj1 != null ? strObj1.toLowerCase() : "").compareTo((strObj2 != null ? strObj2.toLowerCase() : ""));
            }
        });
        return list;
    }


    public synchronized static ArrayList<MainListItem> SortApplications(final ArrayList<MainListItem> appList) {
        sort(appList, new Comparator<MainListItem>() {
            @Override
            public int compare(final MainListItem object1, final MainListItem object2) {
                return object1.getTitle().toLowerCase().compareTo(object2.getTitle().toLowerCase());
            }
        });
        return appList;
    }


    public synchronized static ArrayList<MainListItem> sortToolAppAssignment(final Context context, ArrayList<MainListItem> list) {
        Collections.sort(list, new Comparator<MainListItem>() {
            @Override
            public int compare(final MainListItem object1, final MainListItem object2) {
                return object1.getTitle().toLowerCase().compareTo(object2.getTitle().toLowerCase());
            }
        });
        return list;
    }

    public synchronized static List<AppListInfo> sortApplication(List<AppListInfo> list) {
        Collections.sort(list, new Comparator<AppListInfo>() {
            @Override
            public int compare(final AppListInfo object1, final AppListInfo object2) {
                String strObj1 = object1.app.displayName;
                String strObj2 = object2.app.displayName;

                return (strObj1 != null ? strObj1.toLowerCase() : "").compareTo((strObj2 != null ? strObj2.toLowerCase() : ""));
            }
        });
        return list;
    }


    public synchronized static ArrayList<TableNotificationSms> sortNotificationByDate(final ArrayList<TableNotificationSms> appList) {
        sort(appList, new Comparator<TableNotificationSms>() {
            @Override
            public int compare(final TableNotificationSms object1, final TableNotificationSms object2) {
                return object2.get_date().compareTo(object1.get_date());
            }
        });
        return appList;
    }

    public synchronized static ArrayList<CustomNotification> sortNotificationByDate1(final ArrayList<CustomNotification> appList) {
        sort(appList, new Comparator<CustomNotification>() {
            @Override
            public int compare(final CustomNotification object1, final CustomNotification object2) {
                return object2.getDate().compareTo(object1.getDate());
            }
        });
        return appList;
    }
}
