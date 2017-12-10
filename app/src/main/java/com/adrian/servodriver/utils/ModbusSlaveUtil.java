package com.adrian.servodriver.utils;

import com.serotonin.io.serial.SerialParameters;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ModbusSlaveSet;
import com.serotonin.modbus4j.base.ModbusUtils;
import com.serotonin.modbus4j.exception.ModbusInitException;

import java.math.BigInteger;

/**
 * Created by ranqing on 2017/10/2.
 */

public class ModbusSlaveUtil {
    private ModbusSlaveSet slaveSet;

    public void initModbusSlave() {
        SerialParameters params = new SerialParameters();
        params.setBaudRate(Constants.BAUD_RATE);
        params.setDataBits(Constants.DATA_BIT);
        params.setStopBits(Constants.STOP_BIT);
        params.setParity(Constants.PARITY_DIGIT);
        slaveSet = new ModbusFactory().createRtuSlave(params);
        ModbusUtils utils = new ModbusUtils();
    }

    public void start() {
        try {
            if (slaveSet != null) {
                slaveSet.start();
            }
        } catch (ModbusInitException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (slaveSet != null) {
            slaveSet.stop();
        }
    }


}
