package com.adrian.servodriver.utils;

import android.util.Log;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.exception.ModbusInitException;

/**
 * Created by ranqing on 2017/10/2.
 */

public class ModbusMasterUtil {

    private static ModbusMasterUtil instance;

    private ModbusMaster master;

    private ModbusMasterUtil() {
        initModbusMaster();
    }

    public static ModbusMasterUtil getInstance() {
        if (instance == null) {
            instance = new ModbusMasterUtil();
        }
        return instance;
    }

    public void initModbusMaster() {
        SerialParameters params = new SerialParameters();
        params.setBaudRate(Constants.BAUD_RATE);
        params.setDataBits(Constants.DATA_BIT);
        params.setStopBits(Constants.STOP_BIT);
        params.setParity(Constants.PARITY_DIGIT);
        master = new ModbusFactory().createRtuMaster(params);
        master.setTimeout(2000);
        master.setRetries(0);
        try {
            master.init();
        } catch (ModbusInitException e) {
            e.printStackTrace();
        }
    }

    public void testSlaveNode(int size) {
        for (int i = 1; i < size; i++) {
            Log.e("TEST", "node" + i + ":" + master.testSlaveNode(i));
        }
    }

    public void destroy() {
        if (master != null && master.isInitialized()) {
            master.destroy();
        }
    }
}
