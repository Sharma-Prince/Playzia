package playzia.com.playzia;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import am.appwise.components.ni.NoInternetDialog.Builder;
import am.appwise.components.ni.NoInternetDialog;


public class MainActivity extends AppCompatActivity {


    NoInternetDialog noInternetDialog;
    Fragment fragment;
    private AdView mAdView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_earn:
                    mAdView.setVisibility(View.VISIBLE);
                    fragment = new EarnFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_play:
                    mAdView.setVisibility(View.VISIBLE);
                    fragment = new PlayFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    mAdView.setVisibility(View.GONE);
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_result:
                    mAdView.setVisibility(View.VISIBLE);
                    fragment = new ResultFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.noInternetDialog = new Builder(this).build();
        fragment = new PlayFragment();
        new CheckUpdater().checkAppUpdate(this);
        loadFragment(fragment);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private  void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainContainer,fragment);
        transaction.commit();
    }
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.noInternetDialog.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

}
