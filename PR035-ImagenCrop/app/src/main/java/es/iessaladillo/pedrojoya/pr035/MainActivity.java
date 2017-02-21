package es.iessaladillo.pedrojoya.pr035;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements PickOrCaptureDialogFragment
        .Listener {

    private static final int RC_CAPTURAR_FOTO = 0;
    private static final int RC_SELECCIONAR_FOTO = 1;

    private static final String PREF_PATH_FOTO = "prefPathFoto";
    private static final int OPTION_PICK = 0;

    private String sPathFotoOriginal; // path en el que se guarda la foto capturada.
    private String sNombreArchivo; // Nombre para guardar en privado la foto escalada.

    private ImageView imgFoto;
    private File mArchivoRecortado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgFoto = (ImageView) findViewById(R.id.imgFoto);
        // Se lee de las preferencias el path del archivo con la foto escalada y privada
        // (si fuera una base de datos, leeríamos del registro correspondiente.
        SharedPreferences preferencias = getSharedPreferences(getString(R.string.app_name),
                MODE_PRIVATE);
        String pathFoto = preferencias.getString(PREF_PATH_FOTO, "");
        if (!TextUtils.isEmpty(pathFoto)) {
            // Se muestra en el ImageView.
            imgFoto.setImageURI(Uri.fromFile(new File(pathFoto)));
        }
    }

    // Guarda en preferencias el path de archivo mostrado en el ImageView.
    private void guardarEnPreferencias(String path) {
        // Se almacena en las preferencias el path del archivo con la foto escalada y privada
        SharedPreferences preferencias = getSharedPreferences(getString(R.string.app_name),
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(PREF_PATH_FOTO, path);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuFoto) {
            PickOrCaptureDialogFragment frgDialogo = new PickOrCaptureDialogFragment();
            frgDialogo.show(this.getSupportFragmentManager(), "PickOrCaptureDialogFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    // Envía un intent implícito para seleccionar una foto de la galería.
    // Recibe el nombre que debe tomar el archivo con la foto escalada y guardada en privado.
    @SuppressWarnings("SameParameterValue")
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void seleccionarFoto(String nombreArchivoPrivado) {
        // Se guarda el nombre para uso posterior.
        sNombreArchivo = nombreArchivoPrivado;
        // Se seleccionará un imagen de la galería.
        // (el segundo parámetro es el Data, que corresponde a la Uri de la galería.)
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, RC_SELECCIONAR_FOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForReadExternalStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this).setMessage(R.string.permission_readexternalstorage_rationale)
                .setPositiveButton(R.string.permitir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.rechazar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForReadExternalStorage() {
        Snackbar.make(imgFoto, R.string.no_se_pudo,
                Snackbar.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForReadExternalStorage() {
        Snackbar.make(imgFoto, R.string.permission_readexternalstorage_rationale,
                Snackbar.LENGTH_LONG).setAction(R.string.configurar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInstalledAppDetailsActivity(MainActivity.this);
            }
        }).show();
    }

    private static void startInstalledAppDetailsActivity(@NonNull final Activity context) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        // Para que deje rastro en la pila de actividades se añaden flags.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    // Envía un intent implícito para la captura de una foto.
    // Recibe el nombre que debe tomar el archivo con la foto escalada y guardada en privado.
    @SuppressWarnings("SameParameterValue")
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void capturarFoto(String nombreArchivoPrivado) {
        // Se guarda el nombre para uso posterior.
        sNombreArchivo = nombreArchivoPrivado;
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Si hay alguna actividad que sepa realizar la acción.
        if (i.resolveActivity(getPackageManager()) != null) {
            // Se crea el archivo para la foto en el directorio público (true).
            // Se obtiene la fecha y hora actual.
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    new Date());
            String nombre = "IMG_" + timestamp + "_" + ".jpg";
            File fotoFile = crearArchivoFoto(nombre, true);
            if (fotoFile != null) {
                // Se guarda el path del archivo para cuando se haya hecho la captura.
                sPathFotoOriginal = fotoFile.getAbsolutePath();
                // Se obtiene la Uri correspondiente al archivo creado a través del FileProvider,
                // cuyo autorithies debe coincidir con lo especificado para el FileProvider en el
                // manifiesto (necesario para API >= 25).
                Uri fotoURI = FileProvider.getUriForFile(this,
                        "es.iessaladillo.pedrojoya.pr035.fileprovider", fotoFile);
                // Se añade como extra del intent la uri donde debe guardarse.
                i.putExtra(MediaStore.EXTRA_OUTPUT, fotoURI);
                startActivityForResult(i, RC_CAPTURAR_FOTO);
            }
        }
    }

    // Envía un intent implícito para recortar la imagen. Recibe el path de la foto a recortar.
    // Si no es posible recortar, se llama a cargarImagenEscalada().
    private void recortarImagen(String pathFoto) {
        mArchivoRecortado = crearArchivoFoto(sNombreArchivo, false);
        UCrop.of(Uri.fromFile(new File(pathFoto)), Uri.fromFile(mArchivoRecortado))
                .withMaxResultSize(getResources().getDimensionPixelSize(R.dimen.ancho_visor),
                        getResources().getDimensionPixelSize(R.dimen.alto_visor))
                .start(this);
    }

    // Crea un archivo de foto con el nombre indicado en almacenamiento externo si es posible, o si
    // no en almacenamiento interno, y lo retorna. Retorna null si fallo.
    // Si publico es true -> en la carpeta pública de imágenes.
    // Si publico es false, en la carpeta propia de imágenes.
    private File crearArchivoFoto(String nombre, boolean publico) {
        // Se obtiene el directorio en el que almacenarlo.
        File directorio;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (publico) {
                // En el directorio público para imágenes del almacenamiento externo.
                directorio = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
            } else {
                directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }
        } else {
            // En almacenamiento interno.
            directorio = getFilesDir();
        }
        // Su no existe el directorio, se crea.
        if (directorio != null && !directorio.exists()) {
            if (!directorio.mkdirs()) {
                Log.d(getString(R.string.app_name), "error al crear el directorio");
                return null;
            }
        }
        // Se crea un archivo con ese nombre y la extensión jpg en ese
        // directorio.
        File archivo = null;
        if (directorio != null) {
            archivo = new File(directorio.getPath() + File.separator + nombre);
            Log.d(getString(R.string.app_name), archivo.getAbsolutePath());
        }
        // Se retorna el archivo creado.
        return archivo;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_CAPTURAR_FOTO:
                    // Se agrega la foto a la Galería
                    agregarFotoAGaleria(sPathFotoOriginal);
                    // Se recorta la imagen.
                    recortarImagen(sPathFotoOriginal);
                    break;
                case RC_SELECCIONAR_FOTO:
                    // Se obtiene el path real a partir de la uri retornada por la galería.
                    Uri uriGaleria = intent.getData();
                    sPathFotoOriginal = getRealPath(uriGaleria);
                    // Se recorta la imagen.
                    if (!TextUtils.isEmpty(sPathFotoOriginal)) {
                        recortarImagen(sPathFotoOriginal);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    // Se almacena el path de la foto a mostrar en el ImageView.
                    guardarEnPreferencias(mArchivoRecortado.getAbsolutePath());
                    // Se muestra la foto en el ImageView.
                    imgFoto.setImageBitmap(
                            BitmapFactory.decodeFile(mArchivoRecortado.getAbsolutePath()));
                    break;
            }
        }
    }

    // Obtiene el path real de una imagen a partir de la URI de Galería obtenido con ACTION_PICK.
    private String getRealPath(Uri uriGaleria) {
        String path = "";
        // Se consulta en el content provider de la galería el path real del archivo de la foto.
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uriGaleria, filePath, null, null, null);
        if (c != null && c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(filePath[0]);
            path = c.getString(columnIndex);
            c.close();
        }
        return path;
    }

    // Agrega a la Galería la foto indicada.
    private void agregarFotoAGaleria(String pathFoto) {
        // Se crea un intent implícito con la acción de
        // escaneo de un fichero multimedia.
        Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // Se obtiene la uri del archivo a partir de su path.
        File archivo = new File(pathFoto);
        Uri uri = Uri.fromFile(archivo);
        // Se establece la uri con datos del intent.
        i.setData(uri);
        // Se envía un broadcast con el intent.
        this.sendBroadcast(i);
    }

    // Cuando se selecciona una opción del diálogo PickOrCaptureDialogFragment.
    @Override
    public void onItemClick(DialogFragment dialog, int which) {
        if (which == OPTION_PICK) {
            MainActivityPermissionsDispatcher.seleccionarFotoWithCheck(this, "mifoto.jgp");
        } else {
            MainActivityPermissionsDispatcher.capturarFotoWithCheck(this, "mifoto.jgp");
        }
    }

}
