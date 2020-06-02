package co.siempo.phone.adapters.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.AppAssignmentActivity;
import co.siempo.phone.activities.DashboardActivity;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.service.LoadToolPane;
import co.siempo.phone.utils.DrawableProvider;
import co.siempo.phone.utils.PrefSiempo;


public class AppAssignmentAdapter extends RecyclerView.Adapter<AppAssignmentAdapter.ViewHolder>
        implements Filterable {
    private final AppAssignmentActivity context;
    private List<App> filterList;
    private List<App> appList;
    private DrawableProvider mProvider;
    private int id;
    private String class_name;
    private ItemFilter mFilter = new ItemFilter();

    public AppAssignmentAdapter(AppAssignmentActivity context, int id, List<App> appList, String class_name) {
        this.context = context;
        this.appList = appList;
        this.id = id;
        filterList = appList;
        mProvider = new DrawableProvider(context);
        this.class_name = class_name;
    }

    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public AppAssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_item_app_assignment, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final App item = filterList.get(position);
        if (id == 5 && item == null) {
            holder.txtAppName.setText(context.getString(R.string.label_note));
            holder.btnHideApps.setVisibility(View.GONE);
            holder.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_notes));
        } else if (item != null) {
            holder.txtAppName.setText(item.displayName);
            String packageName = item.packageName;
            List<App> junkApps = PrefSiempo.getInstance(context).readAppList(PrefSiempo.JUNKFOOD_APPS);
            boolean containedInJunk = false;
            for (App app: junkApps)
            {
                if (app.packageName.equals(packageName)) {
                    containedInJunk = true;
                    break;
                }
            }

            if (containedInJunk) {
                holder.btnHideApps.setVisibility(View.VISIBLE);
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = context.getTheme();
                theme.resolveAttribute(R.attr.icon_color, typedValue, true);
                int color = typedValue.data;
                Drawable drawable = mProvider.getRound("" + item.displayName.charAt(0), color, 30);
                holder.imgIcon.setImageDrawable(drawable);
                holder.btnHideApps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, JunkfoodFlaggingActivity.class);
                        context.startActivity(intent);
                    }
                });
            } else {
                Drawable bitmap = CoreApplication.getInstance().getBitMapByApp(item);
                holder.imgIcon.setImageDrawable(bitmap);
                holder.btnHideApps.setVisibility(View.GONE);
            }
        }
        holder.linearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.btnHideApps.getVisibility() != View.VISIBLE) {
                    HashMap<Integer, AppMenu> map = CoreApplication.getInstance().getToolsSettings();
                    boolean isSameApp = false;
                    if (id == 5 && item == null) {
                        if (map.get(id).getApplicationName().equalsIgnoreCase("Notes")) {
                            isSameApp = true;
                        } else {
                            isSameApp = false;
                            map.get(id).setApplicationName(context.getString(R.string.notes));
                        }
                    } else {
                        if (null != item) {
                            map.get(id).setVisible(true);
                            if (map.get(id).getApplicationName().equalsIgnoreCase(item.packageName)) {
                                isSameApp = true;
                            } else {
                                isSameApp = false;
                                map.get(id).setApplication(item);
                            }
                        }
                    }

                    String hashMapToolSettings = new Gson().toJson(map);
                    PrefSiempo.getInstance(context).write(PrefSiempo.TOOLS_SETTING, hashMapToolSettings);

                    new LoadToolPane().execute();
                    if (class_name.equalsIgnoreCase(DashboardActivity.CLASS_NAME)) {
                        if (id == 5 && item == null) {
                            new ActivityHelper(context).openNotesApp(false);
                            context.finish();

                        } else {
                            if (item != null) {
                                new ActivityHelper(context).openAppWithPackageName(item.packageName);
                            }
                            context.finish();
                        }
                    } else {
                        Intent returnIntent = new Intent();
                        context.setResult(isSameApp ? Activity.RESULT_CANCELED : Activity.RESULT_OK, returnIntent);
                        context.finish();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imgIcon;
        TextView txtAppName;
        Button btnHideApps;
        RelativeLayout linearList;

        public ViewHolder(View v) {
            super(v);
            imgIcon = v.findViewById(R.id.imgIcon);
            txtAppName = v.findViewById(R.id.txtAppName);
            btnHideApps = v.findViewById(R.id.btnHideApps);
            linearList = v.findViewById(R.id.linearList);
        }
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String searchString = constraint.toString().toLowerCase().trim();
            FilterResults ret = new FilterResults();

            int count = appList.size();
            List<App> templist = new ArrayList<>();

            String filterableString;

            if (!searchString.isEmpty()) {
                try {
                    for (int i = 0; i < count; i++) {
                        if (id == 5 && appList.get(i) == null) {
                            filterableString = context.getString(R.string.label_note);
                        } else {
                            filterableString = appList.get(i).displayName;
                        }
                        if (filterableString == null) {
                            filterableString = appList.get(i).displayName;
                        }
                        if (filterableString != null) {
                            if (filterableString.toLowerCase().contains(searchString.toLowerCase())) {
                                templist.add(appList.get(i));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                templist = appList;
            }
            ret.values = templist;
            ret.count = templist.size();
            return ret;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                filterList = (ArrayList<App>) results.values;
            } else {
                filterList = new ArrayList<>(appList);
            }
            if (filterList != null && filterList.size() > 0) {
                context.hideOrShowMessage(true);
            } else {
                context.hideOrShowMessage(false);
            }
            notifyDataSetChanged();
        }
    }
}
