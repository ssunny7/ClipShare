package com.clipshare.csclientservice;

import com.clipshare.common.Constants;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class CSClientService extends Service {
	
	private static CSClientService thisService = null;

	private String ipAddress = null;
	private Messenger serverMessenger = null;
	private ConnCreator connCreator = null;

    private final Messenger commandMessenger = new Messenger(new ServiceCommandHandler());
	
	public CSClientService() {
		thisService = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

    private synchronized void start() {
        if(connCreator == null)
            connCreator = new ConnCreator();
        if(!connCreator.isRunning()) {
            connCreator.setIp(ipAddress);
            connCreator.setMessenger(serverMessenger);
            connCreator.startThread();
        }
    }

    //this is not called when the connection is terminated, hence connCreator is not set to null in that case. Need to fix.
    public void stop() {
        if(connCreator != null) {
            if (connCreator.isRunning())
                connCreator.stopThread();

            connCreator = null;
        }
    }
	
	@Override
	public void onDestroy() {
        stop();
		thisService = null;

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return commandMessenger.getBinder();
	}
	
	public static boolean isRunning() {
		if(thisService == null || thisService.connCreator == null)
			return false;
		
		return thisService.connCreator.isRunning();
	}

    public class ServiceCommandHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();

            switch(data.getInt(Constants.SERVICE_COMMAND_KEY)) {
                case Constants.SERVICE_COMMAND_VAL_SETIP: ipAddress = data.getString(Constants.SERVICE_COMMAND_SETIP_KEY);
                        break;
                case Constants.SERVICE_COMMAND_VAL_SETMESSENGER: serverMessenger = (Messenger)data.getParcelable(Constants.SERVICE_COMMAND_SETMESSENGER_KEY);
                        break;
                case Constants.SERVICE_COMMAND_VAL_START: start();
                        break;
                case Constants.SERVICE_COMMAND_VAL_STOP: stop();
                        break;
            }
        }
    }
}
