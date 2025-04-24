package com.practicando.accionesintent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CAMARA_PERMISO = 1;

    //Clase para manejar los resultados de las actividades
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<String> callPhoneLauncher;
    private String dial;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prueba para commit
        // prueba 2
        setContentView(R.layout.activity_main);
        Button mLLamarButton    = findViewById(R.id.buttonLlamar);
        Button mWebButton       = findViewById(R.id.buttonWeb);
        Button mCamaraButton    = findViewById(R.id.buttonCamara);
        Button mUbicacionButton = findViewById(R.id.buttonUbicacion);
        Button mCalendarButton  = findViewById(R.id.buttonCalendar);
        EditText mPhoneNoEt     = findViewById(R.id.editTextTlf);
        imageView               = findViewById(R.id.imageView);

        /* Para usar ActivityResultLauncher, primero debemos definirlo e inicializarlo.
         Esto se hace en el método onCreate() de la actividad o fragmento */
        camaraLauncher = createCameraLauncher();
        callPhoneLauncher = createCallPhoneLauncher();


        /* Implementaciones de OnClickListener */

        /**
         * Onclick llamar
         */
        mLLamarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = mPhoneNoEt.getText().toString();
                if(!TextUtils.isEmpty(phoneNo)) {
                    dial = "tel:" + phoneNo;
                   // startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));

                    // Si no tiene permiso
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Solicitar permiso
                        callPhoneLauncher.launch(Manifest.permission.CALL_PHONE);
                    } else {
                        // hacerLLamada();
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(dial));
                        startActivity(callIntent);
                    }
                }
            }
        });

        /**
         * Onclick botón cámara
         */
        mCamaraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.
                        checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
                // Si no tiene permiso
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permiso
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 225);
                } else {
                    // Lanzar la cámara: creamos un Intent para abrir la cámara y lanzarlo con el cameraLauncher
                    Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    camaraLauncher.launch(camaraIntent);
                }
            }
        });


        /**
         * Onclick navegar
         */
        mWebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.google.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });




    /**
     * Onclick ubicación
     */
    mUbicacionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Búsqueda de "restaurantes cercanos"
                String uri = "geo:0,0?q=restaurantes+cercanos";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });

        /**
         * Onclick calendario
         */
        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date start = new Date();

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, "title")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, start.getTime());
                    startActivity(intent);

            }
        });

    }


    /**
     * Creamos el launcher de la camara
      * @return ActivityResultLauncher
     */
    private ActivityResultLauncher<Intent> createCameraLauncher() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Capturar la imagen desde los datos de la cámara
                    Intent data = result.getData();
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(imageBitmap); // Mostrar la imagen
                        }
                    }
                }
            });
    }

    /**
     * Creamos el launcher del telefono
     * @return ActivityResultLauncher
     */
    private ActivityResultLauncher<String> createCallPhoneLauncher() {
        return registerForActivityResult( new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(dial));
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "Permiso para llamadas denegado",
                            Toast.LENGTH_SHORT).show();
                }
            }
        );
    }


}
