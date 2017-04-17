package com.adrian.servodriver.demo;

import com.adrian.servodriver.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.FT4222_STATUS;
import com.ftdi.j2xx.interfaces.*;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.*;


public class FT4222I2CFragment extends Fragment {
	
	static Context 	I2CMasterContext;
	D2xxManager 	ftdid2xx;
	FT_Device 		ftDevice_1 		  = null;
	FT_Device 		ftDevice_2 		  = null;
	
	FT_4222_Device 	ft42Device_1 	  = null;
	FT_4222_Device 	ft42Device_2 	  = null;	
	
	I2cMaster 		ftI2cMaster 	  = null;
	I2cSlave 		ftI2cSlave 		  = null;
	
	int 			DevCount 		  = -1;
	
	// handler event
	final int UPDATE_MASTER_READ_DATA = 0;
	final int UPDATE_SLAVE_READ_DATA  = 1;
		
	StringBuffer 	readSB_1 		  = new StringBuffer();
	StringBuffer 	readSB_2 		  = new StringBuffer();

    /*graphical objects*/
    EditText 		readText;
    EditText 		writeText;
    EditText 		addrText;
    EditText 		numBytesText;
    EditText 		statusText, writeStatusText;
    
    Button 			readButton, writeButton;
    Button 			configButton;
    
    Spinner 		i2cFreqSpinner;
    
    /* slave */
    EditText 		readText_2;
    EditText 		writeText_2;
    
    EditText 		numBytesText_2;
    EditText 		statusText_2, writeStatusText_2;
    
    Button 			readButton_2, writeButton_2;    
    
    /*local variables*/
    byte[] 			readWriteBuffer;
    int[] 			realSize;

    byte 			status;
    byte 			i2cFrequence;
    byte 			deviceAddress;

    
	// Empty Constructor
	public FT4222I2CFragment()
	{
	}

	
	/* Constructor */
	public FT4222I2CFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		I2CMasterContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}
	
	
    public int getShownIndex() {
        return getArguments().getInt("index", 12);
    }

	
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        
        View view 	  	= inflater.inflate(R.layout.device_ft4222_i2c, container, false);
        
        /*create master - editable text objects*/         
        readText	  	= (EditText)view.findViewById(R.id.ReadValues);
        writeText 	  	= (EditText)view.findViewById(R.id.WriteValues);
        addrText 	  	= (EditText)view.findViewById(R.id.DeviceAddressValue);
                    
        numBytesText  	= (EditText)view.findViewById(R.id.NumberOfBytesValue);
              
        statusText	  	= (EditText)view.findViewById(R.id.StatusValues);
        statusText.setInputType(0);
        writeStatusText = (EditText)view.findViewById(R.id.WriteStatusValues);
        writeStatusText.setInputType(0);
         
        readButton    	= (Button)view.findViewById(R.id.MasterReadButton);
        writeButton   	= (Button)view.findViewById(R.id.MasterWriteButton);
        configButton  	= (Button)view.findViewById(R.id.ConfigButton);        
        
        /*create slave - editable text objects*/         
        readText_2    	= (EditText)view.findViewById(R.id.ReadValues_2);
        writeText_2   	= (EditText)view.findViewById(R.id.WriteValues_2);
                     
        numBytesText_2	= (EditText)view.findViewById(R.id.NumberOfBytesValue_2);       
       
        statusText_2  	= (EditText)view.findViewById(R.id.StatusValues_2);
        statusText_2.setInputType(0);
        writeStatusText_2 = (EditText)view.findViewById(R.id.WriteStatusValues_2);
        writeStatusText_2.setInputType(0);
         
        readButton_2  	= (Button)view.findViewById(R.id.SlaveReadButton);
        writeButton_2 	= (Button)view.findViewById(R.id.WriteButton_2);
                       
        /*allocate buffer*/
        readWriteBuffer = new byte[64];
        realSize 	  	= new int[1];

        /*clock frequence*/
        i2cFrequence  = 0;
        i2cFreqSpinner= (Spinner)view.findViewById(R.id.ClockFreqValue);
        ArrayAdapter<CharSequence> clockFreqAdapter = ArrayAdapter.createFromResource(I2CMasterContext,R.array.I2C_freq, 
        																	android.R.layout.simple_spinner_item);
        i2cFreqSpinner.setAdapter(clockFreqAdapter);
        i2cFreqSpinner.setSelection(i2cFrequence);

        
        //  ------------  Master  ------------  //
        readButton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				int numBytes;
				
				if(ftI2cMaster == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
												
				if(addrText.length() != 0)
				{
					try{
						deviceAddress = (byte) Integer.parseInt(addrText.getText().toString(), 16);						
					}
					catch(NumberFormatException ex){
						msgToast("Invalid input for device address",Toast.LENGTH_SHORT);
						return;						
					}
				}
				else
				{
					msgToast("Please set device address",Toast.LENGTH_SHORT);
					return;					
				}
								
				if(numBytesText.length() != 0)
				{
					try{
						numBytes = (byte) Integer.parseInt(numBytesText.getText().toString(), 10);						
					}
					catch(NumberFormatException ex){
						msgToast("Invalid input for Read Number of Bytes",Toast.LENGTH_SHORT);
						return;						
					}
				
					for(int i=0; i < numBytes; i++){
						readWriteBuffer[i]=(byte)0xff;
					}

					MasterReadThread mr = new MasterReadThread(handler, numBytes);
					mr.start();
				}	
				
			}
		});
        
        
        /*handle write click*/
		writeButton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) 
			{
				if(ftI2cMaster == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
				
				if(writeText.length() != 0)
				{
					writeData(true);
				}				
			}
		});
        
		
       /*config section*/ 
        configButton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {

			    int kbps;
			    int status = 0;
			    
				i2cFrequence = (byte)i2cFreqSpinner.getSelectedItemPosition();
				
				switch(i2cFrequence)
				{
				case 1:
					kbps = 100;
					break;
				case 2:
					kbps = 400;
					break;
				case 3:
					kbps = 1000;
					break;
				case 4:
					kbps = 3400;
					break;					
				case 0:
				default:					
					kbps = 60;
					break;
				}
				
				if(null == ftDevice_1 || null == ftDevice_2){
					ConnectFunction();
				}

				if((null != ftDevice_1 && null != ftDevice_2)
				    && (true == ftDevice_1.isOpen() && true == ftDevice_2.isOpen()))
				{	
					if(addrText.length() != 0)
					{
						try{
							deviceAddress = (byte) Integer.parseInt(addrText.getText().toString(), 16);						
						}
						catch(NumberFormatException ex){
							msgToast("Invalid input for device address",Toast.LENGTH_SHORT);
							return;						
						}
					}
					else
					{
						msgToast("Please set device address",Toast.LENGTH_SHORT);
						return;					
					}
							

					if(ftI2cMaster != null){
						status = ftI2cMaster.init(kbps);
						Log.e("i2cA", "i2c - master init status:"+status);
						
						if(status != 0){
							msgToast("I2C Master init NG",Toast.LENGTH_LONG);
							return;	
						}
					}
					
					if(ftI2cSlave != null){
						status = ftI2cSlave.init();
						Log.e("i2cA", "i2c - slave init status:"+status);
						
						if(status != 0){
							msgToast("I2C Slave init NG",Toast.LENGTH_LONG);
							return;	
						}
						
						status = ftI2cSlave.setAddress(deviceAddress);
						Log.e("i2cA", "i2c - slave setAddress:"+deviceAddress+" status:"+status);
						
						int[] getdevAddr = new int[1];
						status = ftI2cSlave.getAddress(getdevAddr);
						Log.e("i2cA", "i2c - slave getAddress status:"+status+" addr:"+getdevAddr[0]);
					}
				}				
			}
		});
       
        
        //  ------------  slave  ------------  //
        readButton_2.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				int numBytes;
				
				if(ftI2cSlave == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}							
								
				if(numBytesText_2.length() != 0)
				{
					try{
						numBytes = (byte) Integer.parseInt(numBytesText_2.getText().toString(), 10);						
					}
					catch(NumberFormatException ex){
						msgToast("Invalid input for Read Number of Bytes",Toast.LENGTH_SHORT);
						return;						
					}
				
					for(int i=0; i < numBytes; i++){
						readWriteBuffer[i]=(byte)0xff;
					}

					SlaveReadThread sr = new SlaveReadThread(handler, numBytes);
					sr.start();
				}
			}
		});
        
        
        /*handle write click*/
		writeButton_2.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) 
			{
				if(ftI2cSlave == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
				
				if(writeText_2.length() != 0)
				{
					writeData(false);
				}				
			}
		});
              
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        I2CMasterContext.getApplicationContext().registerReceiver(mUsbPlugEvents, filter);
        
        return view;
    }
    
    
    public void updateReadData(int displayActualNumBytes, boolean isMaster)
    {
		char[] displayReadbuffer = new char[64];		

		for(int i = 0; i < displayActualNumBytes ; i++)
		{
			displayReadbuffer[i] = (char)readWriteBuffer[i];
		}		
		Log.e("handler","displayActualNumBytes:"+displayActualNumBytes);
		
		appendData(displayReadbuffer, displayActualNumBytes, isMaster);
		
		if(true == isMaster){
			statusText.setText(Integer.toString(displayActualNumBytes, 10));
		} else{
			statusText_2.setText(Integer.toString(displayActualNumBytes, 10));
		}
    }
    
    
	public void ConnectFunction() {
		DevCount = ftdid2xx.createDeviceInfoList(I2CMasterContext);
		Log.e("j2xx", "DevCount : " + DevCount);

		if (DevCount == 4) {
			ftDevice_1 = ftdid2xx.openByIndex(I2CMasterContext, 0);
			ftDevice_2 = ftdid2xx.openByIndex(I2CMasterContext, 2);

			if(ftDevice_1 == null || ftDevice_2 == null)
			{
				Toast.makeText(I2CMasterContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice_1.isOpen()  && true == ftDevice_2.isOpen())
			{	
				ft42Device_1 = new FT_4222_Device(ftDevice_1);
				ftI2cMaster = ft42Device_1.getI2cMasterDevice();
				
				ft42Device_2 = new FT_4222_Device(ftDevice_2);
				ftI2cSlave = ft42Device_2.getI2cSlaveDevice();
				
				Toast.makeText(I2CMasterContext,
						"devCount:" + DevCount,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(I2CMasterContext, "Need to get permission!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("j2xx", "Need two ft4222 devices(mode 0).");
			Toast.makeText(I2CMasterContext, "Need two ft4222 devices(mode 0).",
					Toast.LENGTH_SHORT).show();
		}
    }
   
    
    public void writeData(boolean isMaster)
    {    	
    	EditText writeText , writeStatusText;
    	
    	if(true == isMaster){
    		writeText = this.writeText;
    		writeStatusText = this.writeStatusText;
    		
    	} else{
    		writeText = this.writeText_2;
    		writeStatusText = this.writeStatusText_2;
    	}
    	
		if(addrText.length() != 0)
		{
			try{
				deviceAddress = (byte) Integer.parseInt(addrText.getText().toString(), 16);						
			}
			catch(NumberFormatException ex){
				msgToast("Invalid input for device address",Toast.LENGTH_SHORT);
				return;					
			}
		}
		else
		{
			msgToast("Please set device address",Toast.LENGTH_SHORT);
			return;					
		}
		
    	String srcStr = writeText.getText().toString();
    	
		int numBytes = (byte)srcStr.length();

		int ret = -1;
		
		for (int i = 0; i < numBytes; i++) {
			readWriteBuffer[i] = (byte)srcStr.charAt(i);
		}
		
		int[] realSize = new int[1];		

    	if(true == isMaster){
    		ret = ftI2cMaster.write(deviceAddress, readWriteBuffer, numBytes, realSize);
    		
    		if(0 != ret){
    			msgToast("master swrite NG!  ret:" + ret ,Toast.LENGTH_LONG);
    		}
    	} else{
    		ret = ftI2cSlave.write(readWriteBuffer, numBytes, realSize);
    		
    		if(0 != ret){
    			msgToast("slave write NG!  ret:" + ret ,Toast.LENGTH_LONG);
    		}
    	}		
		
		writeStatusText.setText(Integer.toString(numBytes, 10));
    }

	
    void msgToast(String str, int showTime)
    {
    	Toast.makeText(I2CMasterContext, str, showTime).show();
    }
    
    
    public void appendData(char[] data, int len, boolean isMaster)
    { 	
    	EditText readText;
    	StringBuffer readSB;
    	
    	if(true == isMaster){
    		readText = this.readText;
    		readSB =  readSB_1;
    	} else{
    		readText = this.readText_2;
    		readSB =  readSB_2;    		
    	}
    	   	  	
    	if(len >= 1)    		
    		readSB.append(String.copyValueOf(data, 0, len));
    	
    	readText.setText(readSB);
    }
    
    
	final Handler handler =  new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		switch(msg.what)
			{
			case UPDATE_MASTER_READ_DATA:
				updateReadData(realSize[0], true);
				break;
			case UPDATE_SLAVE_READ_DATA:
				updateReadData(realSize[0], false);
				break;
    		}
    	}
    };
    
    
    class MasterReadThread extends Thread
    {
		Handler mHandler;
		int numBytes;

		MasterReadThread(Handler h, int n){
			mHandler = h;
			numBytes = n;
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		public void run()
		{			
			if(FT4222_STATUS.FT4222_OK == ftI2cMaster.read(deviceAddress, readWriteBuffer, numBytes, realSize)){
				Message msg = mHandler.obtainMessage(UPDATE_MASTER_READ_DATA);
				mHandler.sendMessage(msg);
			}
			else{
				Log.e("","Master read data NG");
			}
		}
    }
    
    
    class SlaveReadThread extends Thread
    {
		Handler mHandler;
		int numBytes;
		SlaveReadThread(Handler h, int n){
			mHandler = h;
			numBytes = n;
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		public void run()
		{
			if(FT4222_STATUS.FT4222_OK == ftI2cSlave.read(readWriteBuffer, numBytes, realSize)){
				Message msg = mHandler.obtainMessage(UPDATE_SLAVE_READ_DATA);
				mHandler.sendMessage(msg);
			}
			else{
				Log.e("","Slave read data NG");	
			}		
		}
    }
    
	public void onDestroyView(){
		super.onDestroyView();
		
		I2CMasterContext.getApplicationContext().unregisterReceiver(mUsbPlugEvents);
		
	}
	
	private BroadcastReceiver mUsbPlugEvents = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

		    		if(ftDevice_1 != null){
		    			ftDevice_1.close();
		    		}
		    		
		    		if(ftDevice_2 != null){
		    			ftDevice_2.close();
		    		}
		    		
		    		ftDevice_1 		  = null;
		    		ftDevice_2 		  = null;
		    		
		    		ft42Device_1 	  = null;
		    		ft42Device_2 	  = null;	
		    		
		    		ftI2cMaster 	  = null;
		    		ftI2cSlave 		  = null;	

	        } 
	    }
	};
}