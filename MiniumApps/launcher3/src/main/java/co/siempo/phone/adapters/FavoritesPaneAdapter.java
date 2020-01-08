package co.siempo.phone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.FavoriteAppsPositionActivity;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Created by Shahab on 2/23/2017.
 */

public class FavoritesPaneAdapter extends RecyclerView.Adapter<FavoritesPaneAdapter.ViewHolder> {

    private final Context context;
    private List<MainListItem> mainListItemList;
    private boolean isHideIconBranding;

    public FavoritesPaneAdapter(Context context, boolean isHideIconBranding, List<MainListItem> mainListItemList) {
        this.context = context;
        this.mainListItemList = mainListItemList;
        this.isHideIconBranding = isHideIconBranding;
    }

    @Override
    public FavoritesPaneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.list_application_item_grid, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.linearLayout.setVisibility(View.VISIBLE);
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(context, FavoriteAppsPositionActivity.class);
                context.startActivity(intent);
                ((CoreActivity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
        });

        final MainListItem item = mainListItemList.get(position);

        if (item == null) {
            SetEmptyAppSpace(holder);
            return;
        }

        if (!TextUtils.isEmpty(item.getTitle())) {

            //Done as a part of SSA-1454, in order to change the app name
            // based on user selected language, and in case of package nme
            // not available showing the default item name
            if (!TextUtils.isEmpty(item.getPackageName())) {
                String applicationName = CoreApplication.getInstance()
                        .getApplicationNameFromPackageName(item.getPackageName());
                holder.text.setText(applicationName);
            } else {
                holder.text.setText(item.getTitle());
            }
        }
        if (!TextUtils.isEmpty(item.getPackageName())) {
            if (isHideIconBranding) {
                holder.imgAppIcon.setVisibility(View.GONE);
                holder.txtAppTextImage.setVisibility(View.VISIBLE);
                holder.imgUnderLine.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(item.getTitle())) {
                } else {
                    String fontPath = "fonts/robotocondensedregular.ttf";
                    holder.txtAppTextImage.setText("" + item
                            .getTitle().toUpperCase().charAt(0));

                    // Loading Font Face
                    Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
                    // Applying font
                    holder.txtAppTextImage.setTypeface(tf);
                }

            } else {
                holder.imgAppIcon.setVisibility(View.VISIBLE);
                holder.txtAppTextImage.setVisibility(View.GONE);
                holder.imgUnderLine.setVisibility(View.GONE);
                Drawable drawable = CoreApplication.getInstance().getBitMapByApp(item.app);
                if (drawable != null) {
                    holder.imgAppIcon.setImageDrawable(drawable);
                } else {
                    holder.linearLayout.setVisibility(View.INVISIBLE);
                }
            }


        } else {
            SetEmptyAppSpace(holder);
        }



        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.linearLayout.getVisibility() == View.VISIBLE) {
                    if (!TextUtils.isEmpty(item.getPackageName())) {
                        new ActivityHelper(context).openAppWithPackageName(item.getPackageName().trim());
                    }
                }
            }
        });

        boolean isEnable = PrefSiempo.getInstance(context).read(PrefSiempo.DEFAULT_ICON_FAVORITE_TEXT_VISIBILITY_ENABLE, false);
        if(isEnable)
        {
            holder.txtLayout.setVisibility(View.GONE);
        }else
        {
            holder.txtLayout.setVisibility(View.VISIBLE);
        }
    }

    private void SetEmptyAppSpace(ViewHolder holder) {
        holder.imgAppIcon.setImageDrawable(null);
        holder.text.setText("");
        holder.txtAppTextImage.setText("");
        holder.imgUnderLine.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return mainListItemList.size();
    }

    public void setMainListItemList(ArrayList<MainListItem> mainListItemList, boolean hideIconBranding) {
        this.mainListItemList = mainListItemList;
        this.isHideIconBranding = hideIconBranding;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAppIcon;
        View imgUnderLine;
        TextView text, txtAppTextImage;
        private LinearLayout linearLayout;
        LinearLayout txtLayout;

        public ViewHolder(View v) {
            super(v);
            linearLayout = v.findViewById(R.id.linearList);
            imgUnderLine = v.findViewById(R.id.imgUnderLine);
            text = v.findViewById(R.id.text);
            txtAppTextImage = v.findViewById(R.id.txtAppTextImage);
            imgAppIcon = v.findViewById(R.id.imgAppIcon);
            txtLayout = v.findViewById(R.id.favorite_txtLayout);
        }
    }
}
