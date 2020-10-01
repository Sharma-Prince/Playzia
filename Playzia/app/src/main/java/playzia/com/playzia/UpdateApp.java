package playzia.com.playzia;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import am.appwise.components.ni.NoInternetDialog;

public class UpdateApp extends AppCompatActivity {

    NoInternetDialog noInternetDialog;
    TextView versiontxt;
    TextView datetxt;
    TextView whatsnewtxt;

    String version;
    String date;
    String updateinfo;
    String updateurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        this.noInternetDialog = new NoInternetDialog.Builder(this).build();
        Button updateButton = findViewById(R.id.updateButton);
        versiontxt = findViewById(R.id.version);
        datetxt = findViewById(R.id.date);
        whatsnewtxt = findViewById(R.id.whatsnew);

        this.version = getIntent().getStringExtra("version");
        this.date = getIntent().getStringExtra("date");
        this.updateinfo = getIntent().getStringExtra("updateinfo");
        this.updateurl = getIntent().getStringExtra("updateurl");
        this.versiontxt.setText(this.version);
        this.datetxt.setText(this.date);
        this.whatsnewtxt.setText(this.updateinfo);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url= UpdateApp.this.updateurl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    protected void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }
}
