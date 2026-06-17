package me.tx.crazydog;

import android.Manifest;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.dangerous.ReadMediaAudioPermission;
import com.hjq.permissions.permission.dangerous.StandardDangerousPermission;

import java.util.List;

public class CrazyPermission {

    public interface IPermissionResult {
        void result(boolean allGranted);
    }

    public static void camera(Context context, IPermissionResult iPermissionResult) {
        XXPermissions.with(context)
                .permission(new StandardDangerousPermission(Manifest.permission.CAMERA,33))
                .request(new OnPermissionCallback() {
                    @Override
                    public void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList) {
                        for(IPermission p:grantedList){
                            if(p.getPermissionName().equals(Manifest.permission.CAMERA)){
                                iPermissionResult.result(true);
                                return;
                            }
                        }
                        iPermissionResult.result(false);
                    }
                });
    }

}
