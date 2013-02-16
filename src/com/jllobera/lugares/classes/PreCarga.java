package com.jllobera.lugares.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Diálogo de espera entre acciones
 *
 * Created by IntelliJ IDEA.
 * User: Joanet
 * Date: 16/10/11
 * Time: 2:38
 */
public class PreCarga extends AsyncTask<String, Void, Boolean>{
    private ProgressDialog dialog;
    private Context ctx;

    public PreCarga(Context ctx) {
        this.ctx = ctx;
        dialog = new ProgressDialog(ctx);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }

    // progress dialog to show user that contacting server.
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(ctx, "Cargando la ficha",
                "Porfavor, espera...", true, false);
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


}
