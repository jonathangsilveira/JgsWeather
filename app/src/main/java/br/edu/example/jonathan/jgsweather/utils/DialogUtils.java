package br.edu.example.jonathan.jgsweather.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

import br.edu.example.jonathan.jgsweather.R;

public final class DialogUtils {

    private DialogUtils() {}

    public static void showWarningDialog(Context context, @StringRes int resId) {
        String message = context.getString(resId);
        showWarningDialog(context, message);
    }

    public static void showWarningDialog(Context context, String message) {
        int titleId = R.string.warning;
        AlertDialog dialog = createDialog(context, message, titleId);
        dialog.show();
    }

    public static void showErrorDialog(Context context, @StringRes int resId) {
        String message = context.getString(resId);
        showErrorDialog(context, message);
    }

    public static void showErrorDialog(Context context, String message) {
        int titleId = R.string.error;
        AlertDialog dialog = createDialog(context, message, titleId);
        dialog.show();
    }

    private static AlertDialog createDialog(Context context, String message, int titleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

}
