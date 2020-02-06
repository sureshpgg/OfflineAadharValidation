package com.example.offlineaadharvalidation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aadharserver.IValidateAidlInterface;

public class MainActivity extends AppCompatActivity {
    protected IValidateAidlInterface aadharService;
static String Tag="MainActivity";
EditText adharnumber;
TextView textview;
    private String serverAppUri = "com.example.aadharserver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adharnumber=findViewById(R.id.editText);
        textview=findViewById(R.id.textView);
    }
    private void initConnection() {
        if (aadharService == null) {
            Intent intent = new Intent(IValidateAidlInterface.class.getName());

            /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
            intent.setAction("service.connectserver");

            /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
            intent.setPackage("com.example.aadharserver");

            // binding to remote service
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(Tag, "Service Connected");
            aadharService = IValidateAidlInterface.Stub.asInterface( iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(Tag, "Service Disconnected");
            aadharService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (aadharService == null) {
            initConnection();
        }
    }
    public void onVerify(View view) throws RemoteException {
        if (appInstalledOrNot(serverAppUri)) {
if(adharnumber!=null&&adharnumber.length()==12) {
    if (aadharService.validate(adharnumber.getText().toString())) {
        Toast.makeText(MainActivity.this, "Number Matched", Toast.LENGTH_SHORT).show();
        textview.setText("Number Matched");

    } else {
        Toast.makeText(MainActivity.this, "Number Not Matched", Toast.LENGTH_SHORT).show();
        textview.setText("Number Not Matched");
    }
}else {
    Toast.makeText(MainActivity.this, "Number Length should be 12", Toast.LENGTH_SHORT).show();
    textview.setText("Number Length should be 12");
}
        }else {
            Toast.makeText(MainActivity.this, "Server App not installed", Toast.LENGTH_SHORT).show();
            textview.setText("Server App not installed");
        }
        }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void onClear(View view) {
        adharnumber.setText("");
        textview.setText("");
    }
}
