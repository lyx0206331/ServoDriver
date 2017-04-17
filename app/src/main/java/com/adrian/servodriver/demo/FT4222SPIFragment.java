package com.adrian.servodriver.demo;

import com.adrian.servodriver.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.*;
import com.ftdi.j2xx.ft4222.FT_4222_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Spi_Master;
import com.ftdi.j2xx.ft4222.FT_4222_Spi_Slave;
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


public class FT4222SPIFragment extends Fragment {
	
	static Context 	SPIContext;
	D2xxManager 	ftdid2xx;
	FT_Device 		ftDevice_1		  = null;
	FT_Device 		ftDevice_2		  = null;
	
	FT_4222_Device 	ft42Device_1 	  = null;
	FT_4222_Device 	ft42Device_2 	  = null;
	
	SpiMaster 		ftSpiMaster 	  = null;
	SpiSlave 		ftSpiSlave 		  = null;
	
	int 			DevCount		  = -1;
	
	// handler event	
	final int UPDATE_MASTER_READ_DATA = 0;
	final int UPDATE_SLAVE_READ_DATA  = 1;
	final int UPDATE_MASTER_WRITE_DATA= 2;
	final int UPDATE_SLAVE_WRITE_DATA = 3;
	
	StringBuffer 	readSB_1 		  = new StringBuffer();
	StringBuffer 	readSB_2 		  = new StringBuffer();
	
	int 			intRadix 		  = 10;
	
    /*graphical objects*/
    EditText 		readText;
    EditText 		writeText;
    //EditText addrText;
    EditText 		numBytesText;
    EditText 		statusText, writeStatusText;
    
    Button 			readButton, writeButton;
    Button 			configButton;
    
    Spinner 		clockPhaseSpinner;
    Spinner 		clockDividerSpinner;
    Spinner 		clockFreqSpinner;
    
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
    byte 			clockPhaseMode;
    byte 			CPOL;
    byte 			CPHA;
    byte 			clockFrequence;
    byte 			CFreq;
    byte 			clockDivider;
    byte 			CDivi;

    
	// Empty Constructor
	public FT4222SPIFragment()
	{
	}

	
	/* Constructor */
	public FT4222SPIFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		SPIContext 	= parentContext;
		ftdid2xx 	= ftdid2xxContext;
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
        
        View view 		= inflater.inflate(R.layout.device_ft4222_spi, container, false);
        
        /*create master - editable text objects*/         
        readText 	 	= (EditText)view.findViewById(R.id.ReadValues);
        writeText 	 	= (EditText)view.findViewById(R.id.WriteValues);
         
        numBytesText 	= (EditText)view.findViewById(R.id.NumberOfBytesValue);
          
        statusText 	 	= (EditText)view.findViewById(R.id.StatusValues);
        statusText.setInputType(0);
        writeStatusText = (EditText)view.findViewById(R.id.WriteStatusValues);
        writeStatusText.setInputType(0);
         
        readButton 	 	= (Button)view.findViewById(R.id.MasterReadButton);
        writeButton  	= (Button)view.findViewById(R.id.MasterWriteButton);
        configButton 	= (Button)view.findViewById(R.id.ConfigButton);      
        
        /*create slave - editable text objects*/         
        readText_2 	 	= (EditText)view.findViewById(R.id.ReadValues_2);
        writeText_2		= (EditText)view.findViewById(R.id.WriteValues_2);
                   
        numBytesText_2  = (EditText)view.findViewById(R.id.NumberOfBytesValue_2);   
       
        statusText_2	= (EditText)view.findViewById(R.id.StatusValues_2);
        statusText_2.setInputType(0);
        writeStatusText_2 = (EditText)view.findViewById(R.id.WriteStatusValues_2);
        writeStatusText_2.setInputType(0);
         
        readButton_2 	= (Button)view.findViewById(R.id.SlaveReadButton);
        writeButton_2 	= (Button)view.findViewById(R.id.SlaveWriteButton);           
        
        /*allocate buffer*/
        readWriteBuffer = new byte[64];
        realSize 		= new int[1];

        /*default mode is set to mode 0*/
        clockPhaseMode 	= 0;
        clockPhaseSpinner = (Spinner)view.findViewById(R.id.ClockPhaseValue);
        ArrayAdapter<CharSequence> clockPhaseAdapter = ArrayAdapter.createFromResource(SPIContext,R.array.clock_phase,        																	android.R.layout.simple_spinner_item);
        
        clockPhaseSpinner.setAdapter(clockPhaseAdapter);
        clockPhaseSpinner.setSelection(clockPhaseMode);        
        
        /*clock frequence*/
        clockFrequence 	= 0;
        clockFreqSpinner= (Spinner)view.findViewById(R.id.ClockFreqValue);
        ArrayAdapter<CharSequence> clockFreqAdapter = ArrayAdapter.createFromResource(SPIContext,R.array.SPI_freq, 
        																	android.R.layout.simple_spinner_item);
        clockFreqSpinner.setAdapter(clockFreqAdapter);
        clockFreqSpinner.setSelection(clockFrequence);      

        /*clock divider*/
        clockDivider 	= 0;
        clockDividerSpinner = (Spinner)view.findViewById(R.id.ClockDividerValue);
        ArrayAdapter<CharSequence> clockDividerAdapter = ArrayAdapter.createFromResource(SPIContext,R.array.clock_divier,       																	android.R.layout.simple_spinner_item);
        
        clockDividerSpinner.setAdapter(clockDividerAdapter);
        clockDividerSpinner.setSelection(clockDivider);
        
        /*config section*/ 
        configButton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
	    
				clockPhaseMode = (byte)clockPhaseSpinner.getSelectedItemPosition();
				clockFrequence = (byte)clockFreqSpinner.getSelectedItemPosition();
				clockDivider   = (byte)clockDividerSpinner.getSelectedItemPosition();
				
				switch(clockPhaseMode)
				{
				case 1:
					CPOL = 0;
					CPHA = 1;
					break;
				case 2:
					CPOL = 1;
					CPHA = 0;
					break;
				case 3:
					CPOL = 1;
					CPHA = 1;
					break;
				case 0:
				default:					
					CPOL = 0;
					CPHA = 0;
					break;
				}
				
				switch(clockFrequence)
				{

				case 1:
					CFreq = 2;
					break;
				case 2:
					CFreq = 0;
					break;
				case 3:
					CFreq = 3;
					break;
				case 0:
				default:					
					CFreq = 1;
					break;
				}
				
				CDivi = (byte)((int)clockDivider + 1);
				
				if(null == ftDevice_1 || null == ftDevice_2){
					ConnectFunction();
				}

				if((null != ftDevice_1 && null != ftDevice_2)
				    && (true == ftDevice_1.isOpen() && true == ftDevice_2.isOpen()))
				{
					SetConfig(CFreq, CDivi, CPOL, CPHA);
				}				
			}
		});
        
        
        //  ------------  master  ------------  //
        readButton.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				int numBytes;
				
				if(ftSpiMaster == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
								
				int intRadix = 10;			
								
				if(numBytesText.length() != 0)
				{
					try{
						numBytes = (byte) Integer.parseInt(numBytesText.getText().toString(), intRadix);						
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
				if(ftSpiMaster == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
				
				if(writeText.length() != 0)
				{
					MasterWriteThread mw = new MasterWriteThread(handler);
					mw.start();
				}				
			}
		});
                   
        
        //  ------------  slave  ------------  //
        readButton_2.setOnClickListener(new View.OnClickListener() {
			
			//@Override
			public void onClick(View v) {
				int numBytes;
				
				if(ftSpiSlave == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
								
				int intRadix = 10;
				
				if(numBytesText_2.length() != 0)
				{
					try{
						numBytes = (byte) Integer.parseInt(numBytesText_2.getText().toString(), intRadix);						
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
				if(ftSpiSlave == null)
				{
					msgToast("Please set config before read/wirte data." ,Toast.LENGTH_SHORT);
					return;
				}
				
				if(writeText_2.length() != 0)
				{
					SlaveWriteThread mw = new SlaveWriteThread(handler);
					mw.start();
				}				
			}
		});
		
		
		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        SPIContext.getApplicationContext().registerReceiver(mUsbPlugEvents, filter);
               
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
			statusText.setText(Integer.toString(displayActualNumBytes, intRadix));
		} else{
			statusText_2.setText(Integer.toString(displayActualNumBytes, intRadix));
		}
    }
    
    
    public void updateWriteData(int displayActualNumBytes, boolean isMaster)
    {
    	if(true == isMaster)
    		writeStatusText.setText(Integer.toString(displayActualNumBytes, intRadix));
    	else
    		writeStatusText_2.setText(Integer.toString(displayActualNumBytes, intRadix));
    }
    
    
	public void ConnectFunction() {
		DevCount = ftdid2xx.createDeviceInfoList(SPIContext);
		Log.e("j2xx", "DevCount : " + DevCount);						
		
		if (DevCount == 4) {
			ftDevice_1 = ftdid2xx.openByIndex(SPIContext, 2);
			ftDevice_2 = ftdid2xx.openByIndex(SPIContext, 0);

			if(ftDevice_1 == null || ftDevice_2 == null)
			{
				Toast.makeText(SPIContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice_1.isOpen() && true == ftDevice_2.isOpen())
			{	
				ft42Device_1 = new FT_4222_Device(ftDevice_1);
				ftSpiMaster  = ft42Device_1.getSpiMasterDevice();
				
				ft42Device_2 = new FT_4222_Device(ftDevice_2);
				ftSpiSlave 	 = ft42Device_2.getSpiSlaveDevice();
				
				Toast.makeText(SPIContext,
						"devCount:" + DevCount,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(SPIContext, "Need to get permission!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("j2xx", "Need two ft4222 devices(mode 0).");
			Toast.makeText(SPIContext, "Need two ft4222 devices(mode 0).",
					Toast.LENGTH_SHORT).show();
		}
    }
	
	
	public void SetConfig(int cFreq, int cDivider, int cPol, int cPha)
	{
		int status = 0;
		if(ftDevice_1 != null)                                                                                                                        
		{                                                                                                                                             
			ft42Device_1 = new FT_4222_Device(ftDevice_1);
			status = ft42Device_1.init();
			if(status != 0){
				msgToast("SPI init NG", Toast.LENGTH_LONG);
				return;	
			}
			ft42Device_1.setClock((byte)cFreq);
			ftSpiMaster  = ft42Device_1.getSpiMasterDevice();          
			ftSpiMaster.init(FT4222_SPIMode.SPI_IO_SINGLE, cDivider, cPol, cPha, (byte)1);   			 
			((FT_4222_Spi_Master)ftSpiMaster).setDrivingStrength(SPI_DrivingStrength.DS_12MA, SPI_DrivingStrength.DS_16MA, SPI_DrivingStrength.DS_16MA);
		}
		
		if(ftDevice_2 != null)                                                                                                                        
		{                                                                                                                                             
			ft42Device_2 = new FT_4222_Device(ftDevice_2);
			ft42Device_2.init();
			status = ft42Device_1.init();
			if(status != 0){
				msgToast("SPI init NG",Toast.LENGTH_LONG);
				return;	
			}
			ft42Device_2.setClock((byte)cFreq);
			ftSpiSlave  = ft42Device_2.getSpiSlaveDevice();                                                                                    
			ftSpiSlave.init();
			((FT_4222_Spi_Slave)ftSpiSlave).setDrivingStrength(SPI_DrivingStrength.DS_12MA, SPI_DrivingStrength.DS_16MA, SPI_DrivingStrength.DS_16MA);
		}   
	}
    
    
    public void writeData(boolean isMaster)
    {
    	EditText writeText;
    	
    	if(true == isMaster){
    		writeText = this.writeText;
    		
    	} else{
    		writeText = this.writeText_2;
    	}
    	
    	String srcStr = writeText.getText().toString();
    	
		int numBytes = (byte)srcStr.length();

		int ret = -1;

		for (int i = 0; i < numBytes; i++) {
			readWriteBuffer[i] = (byte)srcStr.charAt(i);
		}	
		
    	if(true == isMaster){
    		ret = ftSpiMaster.singleWrite(readWriteBuffer, numBytes, realSize, true);
    		
    		if(0 != ret){
    			msgToast("master swrite NG!  ret:" + ret ,Toast.LENGTH_LONG);
    		}
    		
    	} else{
    		ret = ftSpiSlave.write(readWriteBuffer, numBytes, realSize);
    		
    		if(0 != ret){
    			msgToast("slave swrite NG!  ret:" + ret ,Toast.LENGTH_LONG);
    		}
    	}
    }
	
	
    void msgToast(String str, int showTime)
    {
    	Toast.makeText(SPIContext, str, showTime).show();
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
			case UPDATE_MASTER_WRITE_DATA:
				updateWriteData(realSize[0], true);
				break;
			case UPDATE_SLAVE_WRITE_DATA:
				updateWriteData(realSize[0], false);
				break;
    		}
    	}
    };
    
    
    class MasterReadThread extends Thread
    {
		Handler mHandler;
		int 	numBytes;
		byte[]	WriteBuffer;

		MasterReadThread(Handler h, int n){
			mHandler = h;
			numBytes = n;
			WriteBuffer = new byte[64];
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		public void run()
		{
			if(FT4222_STATUS.FT4222_OK == ftSpiMaster.singleReadWrite(readWriteBuffer, WriteBuffer, numBytes, realSize, false)){
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
			int [] rxStatus = new int[1];		
			long startTime = System.currentTimeMillis();
			
			ftSpiSlave.getRxStatus(rxStatus);			 
			while(rxStatus[0] < numBytes && (System.currentTimeMillis() -startTime < 5000)){
				ftSpiSlave.getRxStatus(rxStatus);
			} 
					
			if(FT4222_STATUS.FT4222_OK == ftSpiSlave.read(readWriteBuffer, numBytes, realSize)){
				Message msg = mHandler.obtainMessage(UPDATE_SLAVE_READ_DATA);
				mHandler.sendMessage(msg);
			}
			else{
				Log.e("","Slave read data NG");	
			}
		}
    }
    
    
    class MasterWriteThread extends Thread
    {
		Handler mHandler;
		int numBytes;

		MasterWriteThread(Handler h){
			mHandler = h;
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		public void run()
		{		
			writeData(true);
			Message msg = mHandler.obtainMessage(UPDATE_MASTER_WRITE_DATA);
			mHandler.sendMessage(msg);
		}
    }
    
    
    class SlaveWriteThread extends Thread
    {
		Handler mHandler;

		SlaveWriteThread(Handler h){
			mHandler = h;
			this.setPriority(Thread.MAX_PRIORITY);
		}
		
		public void run()
		{
			writeData(false);
			Message msg = mHandler.obtainMessage(UPDATE_SLAVE_WRITE_DATA);
			mHandler.sendMessage(msg);
		}
    }
    
    public void onDestroyView(){
		super.onDestroyView();
		
		SPIContext.getApplicationContext().unregisterReceiver(mUsbPlugEvents);
		
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
		    		
		    		ftSpiMaster 	  = null;
		    		ftSpiSlave 		  = null;	

	        } 
	    }
	};
    
}