package co.siempo.phone.adapters.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.siempo.phone.R;
import co.siempo.phone.app.App;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.utils.BitmapUtility;
import co.siempo.phone.utils.PrefSiempo;


public class TempoNotificationItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.txt_app_name)
    TextView txt_app_name;
    @BindView(R.id.img_block_unblock)
    ImageView img_block_unblock;


    @BindView(R.id.imv_appicon)
    ImageView imv_appicon;

    @BindView(R.id.linearList)
    LinearLayout linearList;
    Context context;

    public TempoNotificationItemViewHolder(View itemView, Context context) {
        super(itemView);

        img_block_unblock = itemView.findViewById(R.id.img_block_unblock);
        txt_app_name = itemView.findViewById(R.id.txt_app_name);
        imv_appicon = itemView.findViewById(R.id.imv_appicon);
        linearList = itemView.findViewById(R.id.linearList);
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void render(String text) {
        txt_app_name.setText(text);
    }

    public void displayToggle() {
        img_block_unblock.setVisibility(View.VISIBLE);
    }


    public LinearLayout getLinearList() {
        return linearList;
    }


    public void displayImage(App app, String errormessage) {
        if (TextUtils.isEmpty(errormessage)) {

            Drawable icon = CoreApplication.getInstance().getBitMapByApp(app);
            if (icon != null) {
                Bitmap bitmap = BitmapUtility.drawableToBitmap(icon);
                imv_appicon.setImageBitmap(bitmap);
            }
            else {
                imv_appicon.setImageBitmap(null);
            }

        } else {
            imv_appicon.setImageBitmap(null);
        }
    }

    public void disableViews() {
        imv_appicon.setVisibility(View.INVISIBLE);
        img_block_unblock.setVisibility(View.INVISIBLE);
        txt_app_name.setTextSize(12);

    }

    public void enableViews() {
        txt_app_name.setTextSize(16);
        img_block_unblock.setVisibility(View.VISIBLE);
        imv_appicon.setVisibility(View.VISIBLE);
    }


    public void addToBlockList(String applicationInfo, boolean ischecked, Set<String> blockedApps, Context context) {

        if (ischecked) {
            blockedApps.remove(applicationInfo);
        }
        if (!ischecked) {
            blockedApps.add(applicationInfo);
        }
        PrefSiempo.getInstance(context).write(PrefSiempo.BLOCKED_APPLIST, blockedApps);


    }


}
