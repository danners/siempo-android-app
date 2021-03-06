package co.siempo.phone.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.widget.Toolbar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.activities.UpdateBackgroundActivity;
import co.siempo.phone.event.NotifyBackgroundChange;
import co.siempo.phone.event.NotifyBackgroundToService;
import co.siempo.phone.event.ThemeChangeEvent;
import co.siempo.phone.service.ScreenFilterService;
import co.siempo.phone.util.AppUtils;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.PrefSiempo;

@EFragment(R.layout.fragment_tempo_home)
public class TempoHomeFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;


    @ViewById
    Switch switchDisableIntentionsControls;

    @ViewById
    RelativeLayout relAllowSpecificApps;

    @ViewById
    Switch switchCustomBackground;

    @ViewById
    RelativeLayout relCustomBackground;

    @ViewById
    Switch switchDarkTheme;

    @ViewById
    Switch switchNotification;


    @ViewById
    Switch switchScreenOverlay;


    @ViewById
    RelativeLayout relDarkTheme;

    @ViewById
    RelativeLayout rel_icon_label;

    private PermissionUtil permissionUtil;

    public TempoHomeFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        // Download siempo images
        if (permissionUtil == null) {
            permissionUtil = new PermissionUtil(getActivity());
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.homescreen);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        switchDisableIntentionsControls.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo
                .IS_INTENTION_ENABLE, false));
        switchDisableIntentionsControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo
                        .IS_INTENTION_ENABLE, isChecked);

            }
        });

        if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo
                .IS_DARK_THEME, false)) {
            switchDarkTheme.setChecked(true);
        } else {
            switchDarkTheme.setChecked(false);
        }

        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(getActivity()).write(PrefSiempo
                        .IS_DARK_THEME, isChecked);
                EventBus.getDefault().postSticky(new ThemeChangeEvent(true));
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != getActivity()) {
                            getActivity().finish();
                        }
                    }
                }, 60);
            }
        });

        switchCustomBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strImage = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG, "");
                boolean isEnable = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG_ENABLE, false);
                if (isEnable
                        && !TextUtils.isEmpty(strImage)) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, false);
                    EventBus.getDefault().post(new NotifyBackgroundToService(false));
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG, "");
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                    switchCustomBackground.setChecked(false);
                } else if (!isEnable && TextUtils.isEmpty(strImage)) {
                    switchCustomBackground.setChecked(false);
                    checkPermissionsForBackground();

                } else if (!isEnable && !TextUtils.isEmpty(strImage)) {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, true);
                    EventBus.getDefault().postSticky(new NotifyBackgroundChange(true));
                    switchCustomBackground.setChecked(true);
                }

            }
        });




        rel_icon_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) getActivity()).loadChildFragment(IconLabelsFragment_.builder().build(), R.id.tempoView);
            }
        });

        switchNotification.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true));
        switchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Switch sb = (Switch) v;
                if(sb.isChecked())
                {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, true);
                }else
                {
                    PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_NOTIFICATION_ENABLE, false);
                }
                AppUtils.notificationBarManaged(getActivity(), null);
            }
        });

        switchScreenOverlay.setChecked(PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false));
        switchScreenOverlay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

                                        Switch sb = (Switch) view;
                                if(sb.isChecked())
                                    {
                                                checkPermissionsForSystemWindow();

                                        }else
                                {
                                            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, false);
                                    Intent command = new Intent(getActivity(), ScreenFilterService.class);
                                    command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 1);
                                    getActivity().startService(command);
                                }

                                    }
        });

    }

    private void checkPermissionsForSystemWindow() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                (PermissionUtil.SYSTEM_WINDOW_ALERT))) {
            try {
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
                                Intent command = new Intent(getActivity(), ScreenFilterService.class);
                                command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
                                getActivity().startService(command);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }

                        })
                        .setDeniedMessage(R.string.msg_permission_denied)
                        .setPermissions(new String[]{
                                Manifest.permission.SYSTEM_ALERT_WINDOW})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_SCREEN_OVERLAY, true);
            Intent command = new Intent(getActivity(), ScreenFilterService.class);
            command.putExtra(ScreenFilterService.BUNDLE_KEY_COMMAND, 0);
            getActivity().startService(command);
        }
    }


    @Click
    void relAllowSpecificApps() {
        switchDisableIntentionsControls.performClick();
    }

    @Click
    void relCustomBackground() {
        checkPermissionsForBackground();
    }

    private void checkPermissionsForBackground() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !permissionUtil.hasGiven
                (PermissionUtil.WRITE_EXTERNAL_STORAGE_PERMISSION))) {
            try {
                TedPermission.with(getActivity())
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 10);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {

                            }
                        })
                        .setDeniedMessage(R.string.msg_permission_denied)
                        .setPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest
                                        .permission
                                        .READ_EXTERNAL_STORAGE})
                        .check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String strImage = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG, "");
        boolean isEnable = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.DEFAULT_BAG_ENABLE, false);
        boolean isPermission = permissionUtil.hasGiven(PermissionUtil
                .WRITE_EXTERNAL_STORAGE_PERMISSION);
        if (isEnable
                && !TextUtils.isEmpty(strImage) && isPermission) {
            switchCustomBackground.setChecked(true);
        } else if (!isEnable && TextUtils.isEmpty(strImage) && !isPermission) {
            switchCustomBackground.setChecked(false);
            PrefSiempo.getInstance(getActivity()).write(PrefSiempo.DEFAULT_BAG_ENABLE, false);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Intention screen Wallpaper selection
        if(requestCode == 10 || requestCode == 7) {
            switch (requestCode) {
                case 10:
                    if(resultCode== Activity.RESULT_OK){
                        Uri uri=data.getData();

                        if(uri !=null && !TextUtils.isEmpty(uri.toString())){

                            if(uri.toString().contains("com.google.android.apps.photos.contentprovider")){
                                return;
                            }

                            if(uri.toString().contains("/storage")){
                                String[] storagepath=uri.toString().split("/storage");
                                if(storagepath.length>1){
                                    String filePath="/storage"+storagepath[1];
                                    Intent mUpdateBackgroundIntent = new Intent(getActivity(),UpdateBackgroundActivity.class);
                                    mUpdateBackgroundIntent.putExtra("imageUri", filePath);
                                    startActivityForResult(mUpdateBackgroundIntent, 3);
                                }
                            }
                            else{
                                String id = DocumentsContract.getDocumentId(uri);

                                if(!TextUtils.isEmpty(id) && uri!=null){

                                    try {
                                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                                        File file = new File(getActivity().getCacheDir().getAbsolutePath()+"/"+id);
                                        writeFile(inputStream, file);
                                        String filePath = file.getAbsolutePath();

                                        if(filePath.contains("raw:")){
                                            String[] downloadPath=filePath.split("raw:");
                                            if(downloadPath.length>1){
                                                filePath =downloadPath[1];
                                            }
                                        }

                                        Intent mUpdateBackgroundIntent = new Intent(getActivity(),UpdateBackgroundActivity.class);
                                        mUpdateBackgroundIntent.putExtra("imageUri", filePath);
                                        startActivityForResult(mUpdateBackgroundIntent, 3);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    }
                    break;
                case 7:
                    if(resultCode == Activity.RESULT_OK){
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Intent mUpdateBackgroundIntent = new Intent(getActivity(),
                                UpdateBackgroundActivity
                                        .class);
                        mUpdateBackgroundIntent.putExtra("imageUri", picturePath);
                        startActivityForResult(mUpdateBackgroundIntent, 3);
                    }
                    break;
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
    @Click
    void relDarkTheme() {
        switchDarkTheme.performClick();
    }
}
