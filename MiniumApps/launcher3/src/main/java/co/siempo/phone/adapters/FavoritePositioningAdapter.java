package co.siempo.phone.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.interfaces.ItemTouchHelperAdapter;
import co.siempo.phone.interfaces.ItemTouchHelperViewHolder;
import co.siempo.phone.interfaces.OnFavoriteItemListChangedListener;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.models.MainListItem;

/**
 * Created by rajeshjadi on 14/2/18.
 */

public class FavoritePositioningAdapter extends RecyclerView.Adapter<FavoritePositioningAdapter.ItemViewHolder> implements ItemTouchHelperAdapter {
    private final Activity context;
    private ArrayList<MainListItem> arrayList;
    private OnStartDragListener mDragStartListener;
    private boolean isHideIconBranding;
    private OnFavoriteItemListChangedListener mListChangedListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public FavoritePositioningAdapter(Activity context, boolean isHideIconBranding, ArrayList<MainListItem> arrayList, OnStartDragListener dragListener,
                                      OnFavoriteItemListChangedListener listChangedListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.isHideIconBranding = isHideIconBranding;
        mDragStartListener = dragListener;
        mListChangedListener = listChangedListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        try {
            if (arrayList != null && arrayList.size() > 0) {

                Collections.swap(arrayList, fromPosition, toPosition);

                mListChangedListener.onFavoriteItemListChanged(arrayList);
                notifyItemMoved(fromPosition, toPosition);
            }
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v;
        v = inflater.inflate(R.layout.list_application_item_grid, parent, false);
//             set the view's size, margins, paddings and layout parameters
        return new ItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final MainListItem item = arrayList.get(position);
        holder.linearLayout.setVisibility(View.VISIBLE);
        if (item == null) {
            holder.linearLayout.setVisibility(View.INVISIBLE);
            return;
        }

        if (!TextUtils.isEmpty(item.getTitle())) {
            //Done as a part of SSA-1454, in order to change the app name
            // based on user selected language, and in case of package nme
            // not available showing the default item name
            if (!TextUtils.isEmpty(item.getPackageName())) {
                holder.text.setText(item.app.displayName);
            } else {
                holder.text.setText(item.getTitle());
            }

        }


        if (isHideIconBranding) {
            holder.imgAppIcon.setVisibility(View.GONE);
            holder.txtAppTextImage.setVisibility(View.VISIBLE);
            holder.imgUnderLine.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(item.getTitle())) {
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


        holder.linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (item != null){
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        ImageView imgAppIcon;
        View imgUnderLine;
        TextView text, txtAppTextImage;
        private LinearLayout linearLayout;

        ItemViewHolder(View v) {
            super(v);
            linearLayout = v.findViewById(R.id.linearList);
            imgUnderLine = v.findViewById(R.id.imgUnderLine);
            text = v.findViewById(R.id.text);
            txtAppTextImage = v.findViewById(R.id.txtAppTextImage);
            imgAppIcon = v.findViewById(R.id.imgAppIcon);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
                CoreApplication.getInstance().logException(e);
            }

        }
    }

}
