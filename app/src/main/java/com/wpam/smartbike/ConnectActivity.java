package com.wpam.smartbike;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;



public class ConnectActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private final String TAG = MainActivity.class.getSimpleName();
    TextView mReadBuffer, mBluetoothStatus, range, voltage, speed, power, deadtime;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button BTButton, SearchButton, SynchroButton;
    TextView Dev_amount;
    public ListView list;
    final ArrayList<mDevice> Device_list = new ArrayList<mDevice>();
    mDevice selectedDevice;
    private BluetoothSocket mBTSocket = null;
    private Handler mHandler;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;
    private String deviceData;
    public String BT_response;
    boolean ready=false;


    public void createList(){
        DevListAdapter adapter = new DevListAdapter(this, Device_list);
        list.setAdapter(adapter);
        Dev_amount.setText(Integer.toString(Device_list.size()));

    }
    public void updateList(mDevice newDevice){
        Device_list.add(newDevice);
        createList();
    }
    public void toast(Context context, String text, int duration) {
        Toast myToast = Toast.makeText(context, text, duration);
        myToast.show();
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    public void connectToDevice(final int position) {
        if(Device_list.get(position).getPairedStatus()!="Paired")
        {
            Toast.makeText(getBaseContext(), "Pair device first", Toast.LENGTH_SHORT).show();
        }
        else {
            final String connection_name = Device_list.get(position).getDeviceName();
            boolean fail = false;
            BluetoothDevice connection_device = mBluetoothAdapter.getRemoteDevice(Device_list.get(position).getMacAddress());

            try {
                mBTSocket = createBluetoothSocket(connection_device);
            } catch (IOException e) {
                fail = true;
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                mBTSocket.connect();
            } catch (IOException e) {
                try {
                    fail = true;
                    mBTSocket.close();
                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                            .sendToTarget();
                } catch (IOException e2) {
                    //insert code to deal with this
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
            }
            if (fail == false) {
                mConnectedThread = new ConnectedThread(mBTSocket);
                mConnectedThread.start();

                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, connection_name)
                        .sendToTarget();

            }
        }
    }
    public void get_range(){
            mConnectedThread.write("0001");
            new CountDownTimer(500, 100) {
                public void onFinish() {
                    SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("range", Float.parseFloat(BT_response));
                    editor.apply();
                }
                public void onTick(long millisUntilFinished) {
                }
            }.start();

    }
    public void get_battery_voltage(){
            mConnectedThread.write("0002");
            new CountDownTimer(500, 100) {
                public void onFinish() {
                    SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("battery_voltage", Float.parseFloat(BT_response));
                    editor.apply();
                }
                public void onTick(long millisUntilFinished) {
                }
            }.start();
    }
    public void get_current_speed(){
        mConnectedThread.write("0003");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("current_speed", Float.parseFloat(BT_response));
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
    public void get_current_power(){
        mConnectedThread.write("0004");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("current_power", Float.parseFloat(BT_response));
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
    public void set_deadtime(){

        String[] deadtime_list = new String[]{"010.0", "050.0", "100.0"};
        String[] sink_list = new String[]{"1000.0", "1500.0", "2000.0"};
        String[] source_list = new String[]{"0500.0", "1000.0", "1500.0"};
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        float temp=sharedPreferences.getFloat("set_deadtime", 0);
        if(temp==10) mConnectedThread.write("1101");
        else if(temp==50) mConnectedThread.write("1102");
        else if(temp==100) mConnectedThread.write("1103");
        else{};
    }
    public void set_sourcecurrent(){
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        float temp=sharedPreferences.getFloat("set_source_current", 0);
        if(temp==500) mConnectedThread.write("1201");
        else if(temp==1000) mConnectedThread.write("1202");
        else if(temp==1500) mConnectedThread.write("1203");
        else{};
    }
    public void set_sinkcurrent(){
        SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
        float temp=sharedPreferences.getFloat("set_sink_current", 0);
        if(temp==500) mConnectedThread.write("1301");
        else if(temp==1000) mConnectedThread.write("1302");
        else if(temp==1500) mConnectedThread.write("1303");
        else{};
    }
    public void get_deadtime(){
        mConnectedThread.write("0005");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_deadtime", Float.parseFloat(BT_response));
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
    public void get_sourcecurrent(){
        mConnectedThread.write("0006");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_source_current", Float.parseFloat(BT_response));
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
    public void get_sinkcurrent(){
        mConnectedThread.write("0007");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("set_sink_current", Float.parseFloat(BT_response));
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }
    public void get_distance(){
        mConnectedThread.write("0008");
        new CountDownTimer(500, 100) {
            public void onFinish() {
                SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Float tmp= sharedPreferences.getFloat("distance_covered", 0);
                tmp=tmp+Float.parseFloat(BT_response);
                editor.putFloat("distance_covered", tmp);
                editor.apply();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        list = (ListView) findViewById(R.id.Paired_devices);
        BTButton = (Button) findViewById(R.id.turnON);
        SearchButton = (Button) findViewById(R.id.search);
        SynchroButton = (Button) findViewById(R.id.synchro);
        Dev_amount =(TextView) findViewById(R.id.Dev_amount);
        range = (TextView) findViewById(R.id.range);


        Dev_amount.setText("0");
        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        Intent previousIntent = getIntent();
        deviceData = previousIntent.getStringExtra("deviceData");



        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = Device_list.get(position);
                connectToDevice(position);
                if(mConnectedThread != null) //First check to make sure thread created
                    mConnectedThread.write("1");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mReceiver);
    }
    public void clickBTButton(View view) {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            toast(this, "BT turned on", Toast.LENGTH_SHORT);
        }
        else{
            toast(this, "BT already turned on", Toast.LENGTH_SHORT);
        }

    }
    public void clickBackButton(View view) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("deviceData", deviceData);
        startActivity(myIntent);
    }
    public void clickSearchButton(View view) {
        if(mBluetoothAdapter.isEnabled())
        {
            toast(this, "Searching...", Toast.LENGTH_SHORT);
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            Device_list.clear();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    mDevice newDevice = new mDevice(deviceName, deviceHardwareAddress, "Paired");
                    Device_list.add(newDevice);
                }
            }
            createList();
           mBluetoothAdapter.startDiscovery();
           IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
           registerReceiver(mReceiver, filter);

        }

        else
        {
            toast(this, "Turn BT on first", Toast.LENGTH_SHORT);
        }

    }
    public void clickSynchroButton(View view) {
        if (mBTSocket != null) {
            toast(this, "Synchronizing...", Toast.LENGTH_SHORT);
            mReadBuffer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0) {
                        BT_response = s.toString();
                    }
                    s = null;
                }
            });
            ready = true;
            get_range();
            new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    get_battery_voltage();
                    new CountDownTimer(1000, 100) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            get_current_speed();
                            new CountDownTimer(1000, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    get_current_power();
                                    new CountDownTimer(1000, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                        }

                                        public void onFinish() {
                                        set_deadtime();
                                            new CountDownTimer(1000, 100) {
                                                @Override
                                                public void onTick(long millisUntilFinished) {
                                                }

                                                public void onFinish() {
                                                get_deadtime();
                                                    new CountDownTimer(1000, 100) {
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {
                                                        }

                                                        public void onFinish() {
                                                        set_sourcecurrent();
                                                            new CountDownTimer(1000, 100) {
                                                                @Override
                                                                public void onTick(long millisUntilFinished) {
                                                                }

                                                                public void onFinish() {
                                                                get_sourcecurrent();
                                                                    new CountDownTimer(1000, 100) {
                                                                        @Override
                                                                        public void onTick(long millisUntilFinished) {
                                                                        }

                                                                        public void onFinish() {
                                                                        set_sinkcurrent();
                                                                            new CountDownTimer(1000, 100) {
                                                                                @Override
                                                                                public void onTick(long millisUntilFinished) {
                                                                                }

                                                                                public void onFinish() {
                                                                                get_sinkcurrent();
                                                                                    new CountDownTimer(1000, 100) {
                                                                                        @Override
                                                                                        public void onTick(long millisUntilFinished) {
                                                                                        }

                                                                                        public void onFinish() {
                                                                                            get_distance();
                                                                                            SharedPreferences sharedPreferences = getSharedPreferences(deviceData, MODE_PRIVATE);
                                                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                                            editor.putString("connection_status", "Synchronized");
                                                                                            editor.apply();
                                                                                            toast(getApplicationContext(), "Synchronized \n You can go back now", Toast.LENGTH_SHORT);
                                                                                        }
                                                                                    }.start();
                                                                                }
                                                                            }.start();
                                                                        }
                                                                    }.start();
                                                                }
                                                            }.start();
                                                        }
                                                    }.start();
                                                }
                                            }.start();
                                        }
                                    }.start();
                                }
                            }.start();
                        }
                    }.start();
                }
            }.start();

        }
        else{
            toast(getApplicationContext(), "Not connected", Toast.LENGTH_SHORT);
        }

    }
    private class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        public OutputStream mmOutStream;



        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
    class DevListAdapter extends ArrayAdapter<mDevice>{
        public DevListAdapter(Context context, ArrayList<mDevice> devices) {
            super(context, 0, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            mDevice device = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_view, parent, false);
            }
            // Lookup view for data population
            TextView device_name = (TextView) convertView.findViewById(R.id.device_name);
            TextView mac_address = (TextView) convertView.findViewById(R.id.mac_address);
            TextView paired_status = (TextView) convertView.findViewById(R.id.paired_status);
            // Populate the data into the template view using the data object
            device_name.setText(device.getDeviceName());
            mac_address.setText(device.getMacAddress());
            paired_status.setText(device.getPairedStatus());
            // Return the completed view to render on screen
            return convertView;
        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                mDevice newDev = new mDevice(deviceName, deviceHardwareAddress,"New");
                updateList(newDev);
            }
        }
    };

}