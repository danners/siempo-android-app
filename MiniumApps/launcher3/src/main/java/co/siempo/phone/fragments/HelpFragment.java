package co.siempo.phone.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.HelpActivity;

/**
 * Created by hardik on 5/1/18.
 */


public class HelpFragment extends Fragment {

    private View view;
    private HelpActivity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help, container, false);
        initView();
        return view;
    }

    private void initView() {
        if (null != mActivity) {
            ProgressDialog progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.help);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        TextView txtVersionValue = view.findViewById(R.id.txtVersionValue);

        String version = "";
        if (BuildConfig.FLAVOR.equalsIgnoreCase(getActivity().getString(R.string.alpha))) {
            version = "Siempo version : ALPHA-" + BuildConfig.VERSION_NAME;
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(getActivity().getString(R.string.beta))) {
            version = "Siempo version : BETA-" + BuildConfig.VERSION_NAME;
        }
        txtVersionValue.setText("" + version);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActivity = (HelpActivity) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
