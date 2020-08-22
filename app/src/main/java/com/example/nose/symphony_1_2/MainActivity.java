package com.example.nose.symphony_1_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private String mConnectedDeviceName = null;

    com.example.nose.symphony_1_2.BluetoothChatService mChatService;
    BluetoothAdapter mBluetoothAdapter;

    TextView mTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new com.example.nose.symphony_1_2.BluetoothChatService(getApplicationContext(), mHandler);
        mTextView = (TextView)findViewById(R.id.textView4);
        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String send = mTextView.getText().toString();
                mChatService.write(send.getBytes());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_enable_BT) {
            enableBT();
            return true;
        }
        if (id == R.id.action_connect_device) {
            Intent serverIntent = new Intent(getApplicationContext(), com.example.nose.symphony_1_2.devices_available.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        }
        if(id == R.id.action_exit){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.example.nose.symphony_1_2.Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case com.example.nose.symphony_1_2.BluetoothChatService.STATE_CONNECTED:

                            break;
                        case com.example.nose.symphony_1_2.BluetoothChatService.STATE_CONNECTING:
                            break;
                        case com.example.nose.symphony_1_2.BluetoothChatService.STATE_LISTEN:
                            break;
                        case com.example.nose.symphony_1_2.BluetoothChatService.STATE_NONE:
                            break;
                    }
                    break;
                case com.example.nose.symphony_1_2.Constants.MESSAGE_WRITE:
                    break;
                case com.example.nose.symphony_1_2.Constants.MESSAGE_READ:
                    break;
                case com.example.nose.symphony_1_2.Constants.MESSAGE_DEVICE_NAME:

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    com.example.nose.symphony_1_2.Effect fragment = new com.example.nose.symphony_1_2.Effect();
                    transaction.replace(R.id.segundo_frame, fragment).commit();

                    mConnectedDeviceName = msg.getData().getString(com.example.nose.symphony_1_2.Constants.DEVICE_NAME);
                    //if (null != getApplicationContext()) {//no lo creo necesario
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //}
                    break;
                case com.example.nose.symphony_1_2.Constants.MESSAGE_TOAST:

                    Toast.makeText(getApplicationContext(), msg.getData().getString(com.example.nose.symphony_1_2.Constants.TOAST),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                break;
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(com.example.nose.symphony_1_2.devices_available.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    private void enableBT (){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
}