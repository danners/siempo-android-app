package co.siempo.phone.util;

import android.Manifest;
import android.content.Context;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.main.MainFragmentMediator;
import co.siempo.phone.token.TokenItem;
import co.siempo.phone.token.TokenItemType;
import co.siempo.phone.token.TokenRouter;
import co.siempo.phone.utils.PermissionUtil;
import co.siempo.phone.utils.UIUtils;

/**
 * Created by roma on 16/4/18.
 */

public class ContactSmsPermissionHelper {


    private boolean isFromTokenParser;
    private TokenRouter router;
    private Context context;
    private PermissionUtil permissionUtil;
    private MainFragmentMediator mediator;
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (isFromTokenParser) {
                router.setCurrent(new TokenItem(TokenItemType.CONTACT));
            } else {
                mediator.loadContacts();
                if (router != null) {
                    router.sendText(context);
                }
            }

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            UIUtils.toast(context, "Permission denied");

        }

    };


    public ContactSmsPermissionHelper(TokenRouter router, Context context,
                                      MainFragmentMediator mediator, boolean
                                              isFromTokenParser, String data) {
        this.router = router;
        this.context = context;
        this.mediator = mediator;
        this.isFromTokenParser = isFromTokenParser;

        permissionUtil = new PermissionUtil(context);

    }

    public void checkForContactAndSMSPermission() {

        if (permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                ) {
            if (isFromTokenParser) {
                router.setCurrent(new TokenItem(TokenItemType.CONTACT));
            } else {
                mediator.loadContacts();
                if (router != null) {
                    router.sendText(context);
                }


            }

        } else {

            if (!permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                    ) {
                try {
                    TedPermission.with(context)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage(R.string.msg_permission_denied)
                            .setPermissions(new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS
                            })
                            .check();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (!permissionUtil.hasGiven(PermissionUtil.CONTACT_PERMISSION)
                    ) {


                try {
                    TedPermission.with(context)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage(R.string.msg_permission_denied)
                            .setPermissions(new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS})
                            .check();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }
}
