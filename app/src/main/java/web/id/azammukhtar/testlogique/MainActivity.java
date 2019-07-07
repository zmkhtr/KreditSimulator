package web.id.azammukhtar.testlogique;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity implements IPickResult {
    private static final String TAG = "MainActivity";
    public static final String KEY_HARGA = "HARGA_KEY";
    public static final String KEY_KENDARAAN = "KENDARAAN_KEY";
    public static final String KEY_TENOR = "TENOR_KEY";
    public static final String KEY_DP = "DP_KEY";
    public static final String KEY_IMAGE = "IMAGE_KEY";
    public static final String KEY_ANGSURAN = "KEY_ANGSURAN";

    private EditText mHarga, mDP;
    private AutoCompleteTextView mKendaraan, mTenor;
    private Button mButton;
    private String valueHarga, valueDP;
    private ImageView mImage;
    private Uri filepath;
    private File file;
    private TextView mSize;

    private static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHarga = findViewById(R.id.editTextHarga);
        mDP = findViewById(R.id.editTextDP);
        mKendaraan = findViewById(R.id.dropdownKendaraan);
        mTenor = findViewById(R.id.dropdownTenor);
        mButton = findViewById(R.id.buttonHitung);
        mImage = findViewById(R.id.imageFoto);
        mSize = findViewById(R.id.textSize);

        dropDownKendaraan();
        dropDownTenor();
        setRupiahFormat();
        getSupportActionBar().hide();
        openCamera();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    private void setRupiahFormat() {
        mHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = "";
                double parsed;
                if (!s.toString().equals(current)) {
                    mHarga.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp.]", "");

                    valueHarga = cleanString;

                    Log.d(TAG, "onTextChanged: " + valueHarga);

                    if (cleanString.isEmpty()) {
                        parsed = 0;
                    } else {
                        parsed = Double.parseDouble(cleanString);
                    }


                    Locale localeID = new Locale("in", "ID");
                    String formatRupiah = NumberFormat.getCurrencyInstance(localeID).format((parsed));
                    current = formatRupiah;
                    mHarga.setText(formatRupiah);
                    mHarga.setSelection(formatRupiah.length());
                    mHarga.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = "";
                double parsed;
                if (!s.toString().equals(current)) {
                    mDP.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp.]", "");

                    valueDP = cleanString;

                    Log.d(TAG, "onTextChanged: " + valueDP);

                    if (cleanString.isEmpty()) {
                        parsed = 0;
                    } else {
                        parsed = Double.parseDouble(cleanString);
                    }


                    Locale localeID = new Locale("in", "ID");
                    String formatRupiah = NumberFormat.getCurrencyInstance(localeID).format((parsed));

                    current = formatRupiah;
                    mDP.setText(formatRupiah);
                    mDP.setSelection(formatRupiah.length());

                    mDP.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void dropDownKendaraan() {
        String[] KENDARAAN = new String[]
                {
                        "Toyota Innova",
                        "Toyota Avanza",
                        "Toyota Alphard",
                        "Toyota Rush",
                        "Honda CR-V",
                        "Honda BR-V",
                        "Honda Brio",
                        "Honda Jazz"
                };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.dropdown_item,
                        KENDARAAN);

        mKendaraan.setAdapter(adapter);
    }

    private void dropDownTenor() {
        String[] TENOR = new String[]
                {
                        "1 Tahun",
                        "2 Tahun",
                        "3 Tahun",
                        "4 Tahun",
                        "5 Tahun"
                };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getBaseContext(),
                        R.layout.dropdown_item,
                        TENOR);

        mTenor.setAdapter(adapter);
    }

    private void validation() {
        Log.d(TAG, "validation: Harga " + valueHarga);
        Log.d(TAG, "validation: DP " + valueDP);
        Log.d(TAG, "validation: kendaraan " + mKendaraan.getText().toString());
        Log.d(TAG, "validation: tenor " + mTenor.getText().toString());
        String valueHargaValid = mHarga.getText().toString();
        String valueDPValid = mDP.getText().toString();
        if (mKendaraan.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tidak ada kendaraan yang dipilih", Toast.LENGTH_SHORT).show();
        } else if (valueHargaValid.isEmpty()) {
            mHarga.setError("Harga tidak boleh kosong");
        } else if (mTenor.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tidak ada Tenor yang dipilih", Toast.LENGTH_SHORT).show();
        } else if (valueDPValid.isEmpty()) {
            mDP.setError("DP tidak boleh kosong");
        } else if (filepath == null) {
            Toast.makeText(this, "Mohon tambahkan foto", Toast.LENGTH_SHORT).show();
        } else {

            String tenor = mTenor.getText().toString();
            int bulan = 0;

            switch (tenor) {
                case "1 Tahun":
                    bulan = 12;
                    sendData(bulan);
                    break;
                case "2 Tahun":
                    bulan = 24;
                    sendData(bulan);
                    break;
                case "3 Tahun":
                    bulan = 36;
                    sendData(bulan);
                    break;
                case "4 Tahun":
                    bulan = 48;
                    sendData(bulan);
                    break;
                case "5 Tahun":
                    bulan = 60;
                    sendData(bulan);
                    break;
            }


        }
    }

    private void sendData(int bulan) {
        String path = filepath.toString();
        double harga = Double.valueOf(valueHarga);
        double dp = Double.valueOf(valueDP);
        Log.d(TAG, "sendData: " + bulan);
        double bunga = ((harga-dp) *  21/100) / bulan;
        double pokok = (harga-dp) / bulan;
        double angsuran = bunga + pokok;
        Log.d(TAG, "sendData: bung " + bunga);
        Log.d(TAG, "sendData: ang " + angsuran);
        Log.d(TAG, "sendData: harga " + harga);
        Log.d(TAG, "sendData: dp " + dp);
        Log.d(TAG, "sendData: file " + filepath);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(KEY_HARGA, mHarga.getText().toString());
        intent.putExtra(KEY_KENDARAAN, mKendaraan.getText().toString());
        intent.putExtra(KEY_DP, mDP.getText().toString());
        intent.putExtra(KEY_TENOR, mTenor.getText().toString());
        intent.putExtra(KEY_IMAGE, path);
        intent.putExtra(KEY_ANGSURAN, angsuran);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        mImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup().setWidth(100).setHeight(100)).show(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            mImage.setImageURI(filepath);

            try {
                file = FileUtil.from(this, r.getUri());
                file = new Compressor(this).compressToFile(file);
                mSize.setText(String.format("Size : %s", getReadableFileSize(file.length())));
                mImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onPickResult: ", e);
            }


            filepath = r.getUri();
            Log.d(TAG, "onPickResult: filepath " + filepath);

        } else {
            Log.d(TAG, "onPickResult: error image picker " + r.getError().getMessage());
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImage.setImageBitmap(imageBitmap);
        }
    }
}
