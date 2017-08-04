package com.bluetooth.plugin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Set;

/**
 * Created by DELL on 8/1/2017.
 */

public class BluetoothMonitorPlugin  extends CordovaPlugin{

    private BluetoothAdapter mBluetoothAdapter;

    private Context context;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // gets context
        this.context = webView.getContext();

        // needs to start Bluetooth service


    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
                super.execute(action, args, callbackContext);

        if("getBluetoothDeviceList".equals(action)) {
            getBluetoothDeviceList(callbackContext);
            return true;
        }
        return false;
    }

    /**
     * This method will return the devices either paired or connected (Bonded)
     */
    public void getBluetoothDeviceList(CallbackContext callbackContext) {

        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        JSONArray array = new JSONArray();

        for(BluetoothDevice device:devices) {

            String msg = "";
            switch(device.getBondState()) {

                case BluetoothDevice.BOND_BONDED:
                msg = device.getName() + ":" + device.getAddress() + ": connected";
                array.put(msg);
                break;

                default:
                break;
            }
        }

        //fire the callback
        callbackContext.success(array);
    }

    /**
     * Starts a service to monitor Bluetooth connection
     */
    private void startBluetoothService() {
    }

}
