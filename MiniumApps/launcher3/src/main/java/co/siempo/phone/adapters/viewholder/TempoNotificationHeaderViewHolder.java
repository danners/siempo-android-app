package co.siempo.phone.adapters.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.siempo.phone.R;

public class TempoNotificationHeaderViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.txt_headerName)
    TextView txt_headerName;

    @BindView(R.id.txtHeaderlabel)
    TextView switch_headerNotification;

    public TempoNotificationHeaderViewHolder(View itemView) {
        super(itemView);
        txt_headerName = itemView.findViewById(R.id.txt_headerName);
        switch_headerNotification = itemView.findViewById(R.id.txtHeaderlabel);
        ButterKnife.bind(this, itemView);
    }

    public void render(String text) {
        txt_headerName.setText("" + text);
    }


    public TextView getHeaderToggle() {
        return switch_headerNotification;
    }


}
