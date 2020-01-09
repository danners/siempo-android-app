package co.siempo.phone.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.FavoritesSelectionActivity;
import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.models.AppListInfo;
import co.siempo.phone.utils.Sorting;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by rajeshjadi on 26/2/18.
 */

public class FavoriteFlaggingAdapter extends BaseAdapter implements Filterable {

    private final FavoritesSelectionActivity context;
    private String strSearch = "";
    private ArrayList<AppListInfo> mData;
    private List<AppListInfo> filterList;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public FavoriteFlaggingAdapter(FavoritesSelectionActivity context, ArrayList<AppListInfo> mData) {
        this.context = context;
        this.mData = mData;
        filterList = mData;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return filterList.size();
    }

    public AppListInfo getItem(int position) {
        return filterList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_junkfoodflag, parent, false);
            holder.txtAppName = convertView.findViewById(R.id.txtAppName);
            holder.imgAppIcon = convertView.findViewById(R.id.imgAppIcon);
            holder.imgChevron = convertView.findViewById(R.id.imgChevron);
            holder.imgIsWorkApp = convertView.findViewById(R.id.workApp);
            holder.linTop = convertView.findViewById(R.id.linTop);
            holder.txtNoAppsMessage = convertView.findViewById(R.id.txtNoAppsMessage);
            holder.txtHeader = convertView.findViewById(R.id.txtHeader);
            holder.linearList = convertView.findViewById(R.id.linearList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            final AppListInfo resolveInfo = filterList.get(position);
            if (resolveInfo.isShowHeader && resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.VISIBLE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.str_frequently_used_apps));
                    int color = R.color.transparent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.txtNoAppsMessage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                    holder.txtNoAppsMessage.setText(strSearch.equalsIgnoreCase("") ? context.getString(R.string.tap_apps_below_to_move_them_into_this_section) : context.getString(R
                            .string.no_apps));
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                    int color = R.color.transparent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.txtNoAppsMessage.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
                    }
                    holder.txtNoAppsMessage.setText(strSearch.equalsIgnoreCase("") ? context.getString(R.string.tap_apps_above_to_move_them_into_this_section) : context.getString(R
                            .string.no_apps));
                }
            } else if (resolveInfo.isShowHeader && !resolveInfo.isShowTitle) {
                holder.txtHeader.setVisibility(View.VISIBLE);
                holder.txtNoAppsMessage.setVisibility(View.GONE);
                holder.linTop.setVisibility(View.GONE);
                if (resolveInfo.isFlagApp) {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.str_frequently_used_apps));
                } else {
                    holder.txtHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.unflageapp_header));
                    holder.txtHeader.setText(context.getString(R.string.all_other_installed_apps));
                }
            } else {
                DisplayNormalApp(holder, resolveInfo.app, resolveInfo.isFlagApp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoreApplication.getInstance().logException(e);
        }
        return convertView;
    }

    private void DisplayNormalApp(ViewHolder holder, final App app, final boolean isFlagApp) {
        holder.linTop.setVisibility(View.VISIBLE);
        holder.txtNoAppsMessage.setVisibility(View.GONE);
        holder.txtHeader.setVisibility(View.GONE);

        if (app.isWorkApp) {
            holder.imgIsWorkApp.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgIsWorkApp.setVisibility(View.GONE);
        }

        try {
            holder.txtAppName.setText(app.displayName);
            Drawable bitmap = CoreApplication.getInstance().getBitMapByApp(app);
            holder.imgAppIcon.setImageDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.linearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!app.packageName.equalsIgnoreCase("")) {
                    UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
                    context.showPopUp(v, app, isFlagApp);
                }
            }
        });

        holder.txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
            }
        });
        holder.txtNoAppsMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideSoftKeyboard(context, context.getWindow().getDecorView().getWindowToken());
            }
        });
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder {
        ImageView imgChevron, imgAppIcon, imgIsWorkApp;
        TextView txtNoAppsMessage;
        TextView txtAppName, txtHeader;
        LinearLayout linTop, linearList;
    }

    //added for search facilty
    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase().trim();
            strSearch = searchString;
            FilterResults ret = new FilterResults();
            int count = mData.size();
            final ArrayList<AppListInfo> nlist = new ArrayList<>();
            String filterableString;
            List<AppListInfo> bindingList = new ArrayList<>();

            if (!searchString.isEmpty()) {
                for (int i = 0; i < count; i++) {
                    filterableString = mData.get(i).app.displayName;
                    if (filterableString.toLowerCase().contains(searchString)) {
                        nlist.add(mData.get(i));
                    }
                }

                List<AppListInfo> flagAppList = new ArrayList<>();
                List<AppListInfo> unflageAppList = new ArrayList<>();
                for (AppListInfo resolveInfo : nlist) {
                    if (!resolveInfo.app.packageName.equalsIgnoreCase(context.getPackageName())) {
                        String applicationname = CoreApplication.getInstance()
                               .getApplicationName(resolveInfo.app.packageName);
                        if (!TextUtils.isEmpty(applicationname)) {
                            if (context.adapterList.contains(resolveInfo.app.packageName)) {
                                flagAppList.add(new AppListInfo(resolveInfo.app.packageName, applicationname, false, false, true, resolveInfo.app.isWorkApp));
                            } else {
                                unflageAppList.add(new AppListInfo(resolveInfo.app.packageName, applicationname, false, false, false, resolveInfo.app.isWorkApp));
                            }
                        }
                    }
                }
                //Code for removing the junk app from Favorite Sorted Menu and
                //Favorite List

                if (flagAppList.size() == 0) {
                    flagAppList.add(new AppListInfo("", "", true, true, true, false));
                } else {
                    flagAppList.add(0, new AppListInfo("", "", true, false, true, false));
                }

                flagAppList = Sorting.sortApplication(flagAppList);
                bindingList.addAll(flagAppList);
                if (unflageAppList.size() == 0) {
                    unflageAppList.add(new AppListInfo("", "", true, true, false, false));
                } else {
                    unflageAppList.add(0, new AppListInfo("", "", true, false, false, false));
                }
                unflageAppList = Sorting.sortApplication(unflageAppList);
                bindingList.addAll(unflageAppList);
            } else {
                bindingList.addAll(mData);
            }
            ret.values = bindingList;
            ret.count = bindingList.size();
            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                filterList = (List<AppListInfo>) results.values;
            } else {
                filterList = new ArrayList<>(mData);
            }
            notifyDataSetChanged();
        }
    }


}
