package br.com.newidea.desafiotattooapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.newidea.desafiotattooapp.R;
import br.com.newidea.desafiotattooapp.utils.AppCameraPermissionsUtil;
import br.com.newidea.desafiotattooapp.utils.CameraUtils;
import br.com.newidea.desafiotattooapp.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    //permissions
    private AppCameraPermissionsUtil appCameraPermissionsUtil;

    //ui
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.action_bau:
                    mTextMessage.setText(R.string.title_bau);
                    return true;
                case R.id.action_camera:
                    mTextMessage.setText(R.string.title_camera);
                    startActivityForResult(
                            CameraUtils.builder()
                                    .activityOwner(MainActivity.this)
                                    .build()
                                    .getPickImageChooserIntent()
                            , 200);
                    break;
                case R.id.action_negocio:
                    mTextMessage.setText(R.string.title_negociacao);
                    return true;
                case R.id.action_config:
                    mTextMessage.setText(R.string.title_config);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setup() {
        setUpMainPermissions();
    }

    private void setUpMainPermissions() {
        appCameraPermissionsUtil = new AppCameraPermissionsUtil(this);
        appCameraPermissionsUtil.setUpAppPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            final Uri uri = CameraUtils.builder()
                    .activityOwner(this)
                    .build()
                    .getPickImageResultUri(data);

            final Uri picUri = (uri != null) ? uri : ImageUtils.getImageUri(getApplicationContext(), (Bitmap) data.getExtras().get("data"));

            TattooPostActivity.call(this, picUri);
        }
    }






}
