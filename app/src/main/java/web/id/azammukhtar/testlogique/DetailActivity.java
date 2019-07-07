package web.id.azammukhtar.testlogique;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import id.zelory.compressor.Compressor;

import static web.id.azammukhtar.testlogique.MainActivity.KEY_ANGSURAN;
import static web.id.azammukhtar.testlogique.MainActivity.KEY_DP;
import static web.id.azammukhtar.testlogique.MainActivity.KEY_HARGA;
import static web.id.azammukhtar.testlogique.MainActivity.KEY_IMAGE;
import static web.id.azammukhtar.testlogique.MainActivity.KEY_KENDARAAN;
import static web.id.azammukhtar.testlogique.MainActivity.KEY_TENOR;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private TextView mHarga, mKendaraan, mTenor, mDP, mAngsuran, mSize;
    private ImageView mImage;
    private Button mSimulasi;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mHarga = findViewById(R.id.textHarga);
        mKendaraan = findViewById(R.id.textKendaraan);
        mTenor = findViewById(R.id.textTenor);
        mDP = findViewById(R.id.textDP);
        mAngsuran = findViewById(R.id.textAngsuran);
        mImage = findViewById(R.id.imageFotoDetail);
        mSize = findViewById(R.id.textSizeDetail);
        getSupportActionBar().hide();

        mSimulasi = findViewById(R.id.buttonSimulasi);

        mSimulasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setData() {
        Intent intent = getIntent();
        mHarga.setText(
                intent.getStringExtra(KEY_HARGA)
        );

        double angsuran = intent.getDoubleExtra(KEY_ANGSURAN, 0);
        Locale localeID = new Locale("in", "ID");
        Log.d(TAG, "setData: ang " + angsuran);
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String angsuranText = formatRupiah.format(angsuran) + " /bulan";
        mAngsuran.setText(angsuranText);

        mKendaraan.setText(
                intent.getStringExtra(KEY_KENDARAAN)
        );
        mDP.setText(
                intent.getStringExtra(KEY_DP)
        );
        mTenor.setText(
                intent.getStringExtra(KEY_TENOR)
        );


        Log.d(TAG, "setData: " + intent.getStringExtra(KEY_IMAGE));
        Log.d(TAG, "setData: " + intent.getIntExtra(KEY_ANGSURAN, 0));

        Uri uri = Uri.parse(intent.getStringExtra(KEY_IMAGE));

        try {
            file = FileUtil.from(this, uri);
            file = new Compressor(this).compressToFile(file);
            mSize.setText(String.format("Size : %s", getReadableFileSize(file.length())));
            mImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
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
}
