package com.bluetooth.plugin;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.bluetooth.monitor.MyBluetoothService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Set;

/**
 * Created by DELL on 8/1/2017.
 */

public class BluetoothMonitorPlugin  extends CordovaPlugin{


    private Context context;

    private CallbackContext tmpCallbackContext;

    private CallbackContext  onBluetoothCallbackContext;

    private CordovaWebView webView;

    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mDiscoveryReceiver;
    private MyBluetoothService myBluetoothService;

    private Handler mHandler;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mBluetoothStatus;

    private static final long SCAN_PERIOD = 15000;

    private boolean mIsBound = false;

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
                if (device.getName() == null) {
                Log.d("LE device", "Unknown");
            } else {
                Log.d("LE device", device.getName());
            }
            Log.d("LE address", device.getAddress());
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("LESCANNER", "fail:" + errorCode);
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void refresh() {
        if (mBluetoothAdapter.isEnabled()) {
            myBluetoothService.closeGatt();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                    if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE || device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    myBluetoothService.connectGatt(context, device);
                }
            }

            Set<BluetoothDevice> connectedDevices = myBluetoothService.getConnectedDevices();
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.stopScan(mLeScanCallback);
                    Log.d("LESCANNER", "stop");
                }
            }, SCAN_PERIOD);

            mBluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            mBluetoothLeScanner.stopScan(mLeScanCallback);
            Log.d("LESCANNER", "stop");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBluetoothService = ((MyBluetoothService.MyServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBluetoothService = null;
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


        // gets context
        this.context = webView.getContext();

        this.webView = webView;
        // needs to start Bluetooth service
        mDiscoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();
                    // TDDO: Needs to add plugin call back
                    JSONArray attay = new JSONArray();
                    attay.put("ACTION_DISCOVERY_STARTED");
                    updateStatus(attay);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Toast.makeText(context, "Scan finished", Toast.LENGTH_SHORT).show();
                    // TDDO: Needs to add plugin call back
                    JSONArray attay = new JSONArray();
                    attay.put("ACTION_DISCOVERY_FINISHED");
                    updateStatus(attay);
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Toast.makeText(context, "Device found", Toast.LENGTH_SHORT).show();
                    // TDDO: Needs to add plugin call back
                    JSONArray attay = new JSONArray();
                    attay.put("DEVICE_FOUND");
                    updateStatus(attay);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                } else if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                    Toast.makeText(context, "Device connection changed..", Toast.LENGTH_SHORT).show();
                    // TDDO: Needs to add plugin call back
                    JSONArray attay = new JSONArray();
                    attay.put("DEVICE_CONNECTION_STATE_CHANGED");
                    updateStatus(attay);

                    refresh();
                } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    Toast.makeText(context, "Device connection state changed..", Toast.LENGTH_SHORT).show();
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    // TDDO: Needs to add plugin call back

                    JSONArray attay = new JSONArray();
                    attay.put("DEVICE_STATE_CHANGED");
                    updateStatus(attay);

                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        this.context.registerReceiver(mDiscoveryReceiver, filter);

        this.context.startService(new Intent(this.context, MyBluetoothService.class));
        doBindService();

    }

    void doBindService() {
        this.context.bindService(new Intent(this.context, MyBluetoothService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            this.context.unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
                super.execute(action, args, callbackContext);

        if("getBluetoothDeviceList".equals(action)) {
            getBluetoothDeviceList(callbackContext);
            return true;
        }else if("onBluetoothEventFired".equals(action)) {
            onBluetoothCallbackContext = callbackContext;
            onBluetoothEventFired(callbackContext);
            return true;
        }else if("connect".equals(action)) {
            connect(callbackContext, args);
            return  true;
        }else if("disconnect".equals(action)) {
            disconnect(callbackContext,args);
            return true;
        }

        return false;
    }

    /**
     * This method will return the devices either paired or connected (Bonded)
     */
    public void getBluetoothDeviceList(final CallbackContext callbackContext) {

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String msg = "No Data";

//        Toast.makeText(
//                webView.getContext(),
//                "Inside getBluetooth plug in",
//                Toast.LENGTH_LONG
//        ).show();

        JSONArray array = new JSONArray();
        if(devices != null) {
                for (BluetoothDevice device : devices) {
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDED:
                            msg = device.getName() + ":" + device.getAddress() + ": connected";
                            array.put(msg);
                            break;

                        case BluetoothDevice.BOND_BONDING:
                            msg = device.getName() + ":" + device.getAddress() + ": connecting";
                            array.put(msg);
                            break;

                        case BluetoothDevice.BOND_NONE:
                            msg = device.getName() + ":" + device.getAddress() + ": not connected";
                            array.put(msg);
                            break;

                        default:
                            break;
                    }
                }

            try {
                msg = array.getString(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // sends connected bluetooth devices
        callbackContext.success(array);
    }

    private void onBluetoothEventFired(final CallbackContext callbackContext) {
        JSONArray array = new JSONArray();
        array.put("FINE");
        updateStatus(array);
    }

    /**
     * This method updates the status of bluetooth connection and disconnection
     */
    private void updateStatus(JSONArray data) {
        if(onBluetoothCallbackContext != null) {
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.OK, data);
            pluginResult.setKeepCallback(true);
            onBluetoothCallbackContext.sendPluginResult(pluginResult);
        }
    }


    /**
     * This method connects to Bluetooth device
     *
     * @param array
     */
    private void connect(CallbackContext context, final JSONArray array) throws JSONException {
        String address = array.getString(0); // gets device address

        if(address == null) {
            context.error("device address null");
            return;
        }

        if (mBluetoothAdapter.isEnabled()) {
            myBluetoothService.closeGatt();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getType() == BluetoothDevice.DEVICE_TYPE_LE || device.getType() == BluetoothDevice.DEVICE_TYPE_DUAL && device.getAddress().equals(address)) {
                    myBluetoothService.connectGatt(webView.getContext(), device);
                    context.success("DeviceConnected");
                    break;
                }
            }
            Set<BluetoothDevice> connectedDevices = myBluetoothService.getConnectedDevices();
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Disconnect a device
     *
     * @param context
     * @param array
     */
    private void disconnect(CallbackContext context, final JSONArray array) {
        myBluetoothService.disconnect();
        context.success("DeviceDisconnected");
    }

}
