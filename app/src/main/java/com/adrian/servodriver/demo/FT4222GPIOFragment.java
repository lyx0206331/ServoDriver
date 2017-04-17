package com.adrian.servodriver.demo;


import com.adrian.servodriver.R;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.ft4222.FT_4222_Defines.GPIO_Dir;
import com.ftdi.j2xx.ft4222.FT_4222_Device;
import com.ftdi.j2xx.interfaces.Gpio;

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


public class FT4222GPIOFragment extends Fragment {
    
    static Context  GPIOContext;
    D2xxManager     ftdid2xx;
    FT_Device       ftDevice    = null;
    
    FT_4222_Device  ft42Device  = null;
    Gpio            ftGpio      = null;
    
    int             DevCount    = -1;
    final int       iMaxPort    = 4;
    final int       iByteSize   = 8;
    String[]        sOutput     = new String[1];
    
    // handler event	
 	final int 		RADIO_0H	= 0;
 	final int 		RADIO_0L	= 1;
 	final int 		RADIO_1H	= 2;
 	final int 		RADIO_1L	= 3;
 	final int 		RADIO_2H	= 4;
 	final int 		RADIO_2L	= 5;
 	final int 		RADIO_3H	= 6;
 	final int 		RADIO_3L	= 7;

    /*graphical objects*/    
    RadioGroup 		GroupIO0	, GroupIO1	  , GroupIO2    , GroupIO3	  ;
    RadioGroup 		GroupHL0	, GroupHL1	  , GroupHL2    , GroupHL3	  ;
    RadioButton     RadioInput0 , RadioInput1 , RadioInput2 , RadioInput3 ;
    RadioButton     RadioOutput0, RadioOutput1, RadioOutput2, RadioOutput3;
    RadioButton     RadioHigh0  , RadioHigh1  , RadioHigh2  , RadioHigh3  ;
    RadioButton     RadioLow0   , RadioLow1   , RadioLow2   , RadioLow3   ;
  
    // Empty Constructor
    public FT4222GPIOFragment()
    {
    }

    
    /* Constructor */
    public FT4222GPIOFragment(Context parentContext , D2xxManager ftdid2xxContext)
    {
        GPIOContext = parentContext;
        ftdid2xx    = ftdid2xxContext;
    }
    
    
    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);           
        
        View view = inflater.inflate(R.layout.device_ft4222_gpio, container, false);
        
        /*create editable text objects*/                                  
        GroupIO0 	 = (RadioGroup)view.findViewById(R.id.GroupIO0	   );
        GroupIO1 	 = (RadioGroup)view.findViewById(R.id.GroupIO1	   );
        GroupIO2 	 = (RadioGroup)view.findViewById(R.id.GroupIO2	   );
        GroupIO3 	 = (RadioGroup)view.findViewById(R.id.GroupIO3	   );
        
        GroupHL0 	 = (RadioGroup)view.findViewById(R.id.GroupHL0	   );
        GroupHL1 	 = (RadioGroup)view.findViewById(R.id.GroupHL1	   );
        GroupHL2 	 = (RadioGroup)view.findViewById(R.id.GroupHL2	   );
        GroupHL3 	 = (RadioGroup)view.findViewById(R.id.GroupHL3	   );
        
        RadioInput0  = (RadioButton)view.findViewById(R.id.RadioInput0 );
        RadioInput1  = (RadioButton)view.findViewById(R.id.RadioInput1 );
        RadioInput2  = (RadioButton)view.findViewById(R.id.RadioInput2 );
        RadioInput3  = (RadioButton)view.findViewById(R.id.RadioInput3 );
        
        RadioOutput0 = (RadioButton)view.findViewById(R.id.RadioOutput0);
        RadioOutput1 = (RadioButton)view.findViewById(R.id.RadioOutput1);
        RadioOutput2 = (RadioButton)view.findViewById(R.id.RadioOutput2);
        RadioOutput3 = (RadioButton)view.findViewById(R.id.RadioOutput3);
        
        RadioHigh0   = (RadioButton)view.findViewById(R.id.RadioHigh0  );
        RadioHigh1   = (RadioButton)view.findViewById(R.id.RadioHigh1  );
        RadioHigh2   = (RadioButton)view.findViewById(R.id.RadioHigh2  );
        RadioHigh3   = (RadioButton)view.findViewById(R.id.RadioHigh3  );
        
        RadioLow0 	 = (RadioButton)view.findViewById(R.id.RadioLow0   );
        RadioLow1 	 = (RadioButton)view.findViewById(R.id.RadioLow1   );
        RadioLow2 	 = (RadioButton)view.findViewById(R.id.RadioLow2   );
        RadioLow3 	 = (RadioButton)view.findViewById(R.id.RadioLow3   );
        
        RadioOutput0.setChecked(true);
        RadioInput1.setChecked (true);
        RadioOutput2.setChecked(true);
        RadioInput3.setChecked (true);
        
        RadioHigh1.setEnabled (false);
        RadioLow1.setEnabled  (false);
        RadioHigh3.setEnabled (false);
        RadioLow3.setEnabled  (false);
        
        RadioHigh0.setChecked  (true);
        RadioHigh1.setChecked  (true);
        RadioHigh2.setChecked  (true);
        RadioHigh3.setChecked  (true);                              
        
        
        GroupIO0.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
            	SetIO();
            	if(RadioOutput0.isChecked()) {
            		RadioInput1.setChecked(true);
            		RadioHigh0.setEnabled(true);
            		RadioLow0.setEnabled (true);            		
            	}else{
            		RadioOutput1.setChecked(true);
            		RadioHigh0.setEnabled(false);
            		RadioLow0.setEnabled (false);          		
            	}            		
            }
        });
        
        GroupIO1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
            	SetIO();
            	if(RadioOutput1.isChecked()) {
            		RadioInput0.setChecked(true);
            		RadioHigh1.setEnabled(true);
            		RadioLow1.setEnabled (true);          		
            	}else{
            		RadioOutput0.setChecked(true);
            		RadioHigh1.setEnabled(false);
            		RadioLow1.setEnabled (false);          		
            	}            		
            }
        });
        
        GroupIO2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
            	SetIO();
            	if(RadioOutput2.isChecked()) {
            		RadioInput3.setChecked(true);
            		RadioHigh2.setEnabled(true);
            		RadioLow2.setEnabled (true);           		
            	}else{
            		RadioOutput3.setChecked(true);
            		RadioHigh2.setEnabled(false);
            		RadioLow2.setEnabled (false);            		
            	}            		
            }
        });
        
        GroupIO3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
            	SetIO();
            	if(RadioOutput3.isChecked()) {
            		RadioInput2.setChecked(true);
            		RadioHigh3.setEnabled(true);
            		RadioLow3.setEnabled (true);
            	}else{
            		RadioOutput2.setChecked(true);
            		RadioHigh3.setEnabled(false);
            		RadioLow3.setEnabled (false);
            	}            		
            }
        });
        
        
        RadioGroup GHL[] = {GroupHL0, GroupHL1, GroupHL2, GroupHL3};       
        for(int i=0; i<4; i++){
        	GHL[i].setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup arg0, int id) {
                	SetHL(); 	
                }
            });
        }
        
        ReadThread R0 = new ReadThread(handler, 0);
        ReadThread R1 = new ReadThread(handler, 1);
        ReadThread R2 = new ReadThread(handler, 2);
        ReadThread R3 = new ReadThread(handler, 3);
        R0.start();
        R1.start();
        R2.start();
        R3.start();
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        GPIOContext.getApplicationContext().registerReceiver(mUsbPlugEvents, filter);

        return view;
    }
    
    @Override
	public void onStart() {
    	super.onStart();  	
    	SetConfig();  	
    }
    
    
    public void SetConfig() {   	
    	DevCount = ftdid2xx.createDeviceInfoList(GPIOContext);
    	Log.e("j2xx", "DevCount : " + DevCount);

        if ((DevCount == 2) && (ftGpio == null)) {
        	int openIndex = 1;          // Index 1 For mode 0 hard code, Index 3 for mode 1.
            ftDevice = ftdid2xx.openByIndex(GPIOContext, openIndex);	// Open device

            if (ftDevice == null) {
            	Toast.makeText(GPIOContext,"ftDev == null",Toast.LENGTH_SHORT).show();
            	return;
            }

            if (true == ftDevice.isOpen()) {
            	ft42Device  = new FT_4222_Device(ftDevice);
            	ftGpio      = ft42Device.getGpioDevice();
            	if(ftGpio == null){
            		Toast.makeText(GPIOContext,"ftGpio == null",Toast.LENGTH_SHORT).show();
                	return;
            	}
            	Toast.makeText(GPIOContext,
            			"devCount:" + DevCount + " open index:" + openIndex,
            			Toast.LENGTH_SHORT).show();
            	SetIO();
    	    	SetHL();
            } else {
            	Toast.makeText(GPIOContext, "Need to get permission!",
            			Toast.LENGTH_SHORT).show();
            }
        } else { 
        	Log.e("j2xx", "Need ft4222 devices(mode 0).");
        	Toast.makeText(GPIOContext, "Need one ft4222 device(mode 0).",
				Toast.LENGTH_SHORT).show();
        }
    }
    
    public void SetIO() {
    	if(ftGpio == null)
    		SetConfig();
    	
    	if(ftGpio != null) {
	    	int status = 0;
	    	RadioButton[] RadioOutput = {RadioOutput0, RadioOutput1, RadioOutput2, RadioOutput3};
	    	int[] gpioDir = new int[iMaxPort];
	    	for(int iPort = 0; iPort < iMaxPort; iPort++) {			// Set GPIO direction.
	            if(RadioOutput[iPort].isChecked())
	                gpioDir[iPort] = GPIO_Dir.GPIO_OUTPUT;
	            else
	                gpioDir[iPort] = GPIO_Dir.GPIO_INPUT;
	        }
	    	status = ftGpio.init(gpioDir);
	        if(status != 0){
				msgToast("GPIO init NG, status:" + status, Toast.LENGTH_SHORT);
				return;	
			}
    	}
    }
    
    public void SetHL() {
    	if(ftGpio == null)
    		SetConfig();
    	
    	if(ftGpio != null) {
	    	RadioButton[] RadioHigh   = {RadioHigh0  , RadioHigh1  , RadioHigh2  , RadioHigh3  };
	    	for(int iPort = 0; iPort < iMaxPort; iPort++) {
	            if(RadioHigh[iPort].isChecked())
	            	ftGpio.write(iPort, true);
	            else
	            	ftGpio.write(iPort, false);
	        }
    	}
    }
       
    
    void msgToast(String str, int showTime)
    {
        Toast.makeText(GPIOContext, str, showTime).show();
    }   
    
    
    public void onDestroyView(){
		super.onDestroyView();	
		GPIOContext.getApplicationContext().unregisterReceiver(mUsbPlugEvents);		
	}
	
    
	private BroadcastReceiver mUsbPlugEvents = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

	        	if(ftDevice != null)
	        		ftDevice.close();

	        	ftDevice	= null;
	        	ft42Device 	= null;		    		
	        	ftGpio 	  	= null;
	        	DevCount 	= 0;
	        } 
	    }
	};
    
	
	final Handler handler =  new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		RadioButton[] RadioHL = {RadioHigh0, RadioLow0, RadioHigh1, RadioLow1,
    				RadioHigh2, RadioLow2, RadioHigh3, RadioLow3};
    		
    		RadioHL[msg.what].setChecked(true);
    	}
    };
	
	class ReadThread extends Thread {
		Handler mHandler;
		int		iPortNum;
		ReadThread(Handler h, int iPortNum) {
			mHandler = h;
			this.iPortNum = iPortNum;
		}
		
		public void run() {
			RadioButton[] RadioInput = {RadioInput0 , RadioInput1 , RadioInput2 , RadioInput3 };
			int[] RH = {RADIO_0H, RADIO_1H, RADIO_2H, RADIO_3H};
			int[] RL = {RADIO_0L, RADIO_1L, RADIO_2L, RADIO_3L};
			boolean[] bStatus = new boolean[1];
			while(true) {
				if((DevCount > 0) && (ftGpio != null) && RadioInput[iPortNum].isChecked()) {
					ftGpio.read(iPortNum, bStatus);
					Message msg;
					if(bStatus[0])
						msg = mHandler.obtainMessage(RH[iPortNum]);
					else
						msg = mHandler.obtainMessage(RL[iPortNum]);					
					mHandler.sendMessage(msg);
					
					try {
						Thread.sleep(100);
					}catch(Exception e){}
				}
			}
		}
    }
}