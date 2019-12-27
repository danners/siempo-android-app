package co.siempo.phone.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.HelpActivity;
import co.siempo.phone.utils.DeviceUtil;
import de.greenrobot.event.EventBus;

/**
 * Created by hardik on 5/1/18.
 */


public class HelpFragment extends Fragment implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtVersionValue;
    private View view;
    private String TAG = "HelpFragment";
    private HelpActivity mActivity;
    private ProgressDialog progressDialog;
    private RelativeLayout relVersion;
    private RelativeLayout relPrivacyPolicy;
    private RelativeLayout relFaq;
    private RelativeLayout relTermsOfCondition;
    private RelativeLayout relFeedback;
    private RelativeLayout relLeaveReview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help, container, false);
        initView();
        return view;
    }

    private void initView() {
        if (null != mActivity) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }


        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.help);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        relPrivacyPolicy = view.findViewById(R.id.relPrivacyPolicy);
        relFaq = view.findViewById(R.id.relFaq);
        relTermsOfCondition = view.findViewById(R.id.relTermsOfCondition);
        relFeedback = view.findViewById(R.id.relFeedback);
        txtVersionValue = view.findViewById(R.id.txtVersionValue);
        relVersion = view.findViewById(R.id.relVersion);
        relLeaveReview = view.findViewById(R.id.relLeaveReview);

        relPrivacyPolicy.setOnClickListener(this);
        relFaq.setOnClickListener(this);
        relTermsOfCondition.setOnClickListener(this);
        relFeedback.setOnClickListener(this);
        relFeedback.setOnClickListener(this);
        relTermsOfCondition.setOnClickListener(this);
        relVersion.setOnClickListener(this);
        relLeaveReview.setOnClickListener(this);


        String version = "";
        if (BuildConfig.FLAVOR.equalsIgnoreCase(getActivity().getString(R.string.alpha))) {
            version = "Siempo version : ALPHA-" + BuildConfig.VERSION_NAME;
        } else if (BuildConfig.FLAVOR.equalsIgnoreCase(getActivity().getString(R.string.beta))) {
            version = "Siempo version : BETA-" + BuildConfig.VERSION_NAME;
        }
        txtVersionValue.setText("" + version);


    }


    void txtSendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = String.format("mailto:%s?subject=%s&body=%s",
                "feedback@siempo.co", Uri.encode("Feedback"),
                Uri.encode(DeviceUtil.getDeviceInfo()));
        Uri uri = Uri.parse(uriText);
        intent.setData(uri);
        startActivity(intent);
    }

    void txtFaq() {
        ((CoreActivity) getActivity()).loadChildFragment(FaqFragment_.builder()
                .build(), R.id.helpView);
    }

    void txtPrivacyPolicy() {
        ((CoreActivity) getActivity()).loadChildFragment(PrivacyPolicyFragment_.builder()
                .build(), R.id.helpView);
    }

    void termsOfServices() {
        ((CoreActivity) getActivity()).loadChildFragment(TermsOfServicesFragment_.builder()
                .build(), R.id.helpView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relFaq:
                txtFaq();
                break;
            case R.id.relPrivacyPolicy:
                txtPrivacyPolicy();
                break;
            case R.id.relTermsOfCondition:
                termsOfServices();
                break;
            case R.id.relFeedback:
                txtSendFeedback();
                break;
            case R.id.relLeaveReview:
                if (getActivity() != null) {
                    final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
                break;
            default:
                break;
        }
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
