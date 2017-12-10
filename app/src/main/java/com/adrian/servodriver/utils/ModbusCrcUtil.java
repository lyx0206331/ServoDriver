package com.adrian.servodriver.utils;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by ranqing on 2017/12/9.
 */

public class ModbusCrcUtil {

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 方法一：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for (byte b : bytes) { // 使用除与取余进行转换
            if (b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }

            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }

        return new String(buf);
    }

    /**
     * 获取有空格分隔的十六进制命令字符串
     *
     * @param hexString
     * @return
     */
    public static String parse2Cmd(String hexString) {
        char[] chars = new char[hexString.length()];
        hexString.getChars(0, hexString.length(), chars, 0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            if ((i + 1) % 2 == 0) {
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * 读从机数据
     * @param slave_addr    从机地址
     * @param data_addr     数据地址
     * @param data_count    读取数据个数
     * @return
     */
    public static byte[] getReadCMD(int slave_addr, int data_addr, int data_count) {
        byte[] cmd_0 = new byte[6];
        cmd_0[0] = (byte) slave_addr;  //从机地址
        cmd_0[1] = 0x03;    //功能码.读
        cmd_0[2] = (byte) (data_addr >> 8);
        cmd_0[3] = (byte) (data_addr & 0x00ff);
        cmd_0[4] = (byte) (data_count >> 8);
        cmd_0[5] = (byte) (data_count & 0x00ff);
        String crc = calculateCrc16(cmd_0); //获取校验码
        byte[] c = hexStringToBytes(crc);
//        Log.e("CRC", "crc:" + crc);
        byte[] cmd = new byte[cmd_0.length + c.length];
        System.arraycopy(cmd_0, 0, cmd, 0, cmd_0.length);
        System.arraycopy(c, 0, cmd, cmd_0.length, c.length);
        return cmd;
    }

    /**
     * 写从机数据
     * @param slave_addr    从机地址
     * @param data_addr     数据地址
     * @param data          写入数据
     * @return
     */
    public static byte[] getWriteCMD(int slave_addr, int data_addr, byte[] data) {
        byte[] cmd_0 = new byte[4 + data.length];
        cmd_0[0] = (byte) slave_addr;  //从机地址
        cmd_0[1] = 0x06;    //功能码.写
        cmd_0[2] = (byte) (data_addr >> 8);
        cmd_0[3] = (byte) (data_addr & 0x00ff);
        System.arraycopy(data, 0, cmd_0, 4, data.length);
        String crc = calculateCrc16(cmd_0); //获取校验码
        byte[] c = hexStringToBytes(crc);
//        Log.e("CRC", "crc:" + crc);
        byte[] cmd = new byte[cmd_0.length + c.length];
        System.arraycopy(cmd_0, 0, cmd, 0, cmd_0.length);
        System.arraycopy(c, 0, cmd, cmd_0.length, c.length);
        return cmd;
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 计算并获得16CRC
     * @param bytes
     * @return
     */
    public static String calculateCrc16(byte[] bytes) {
        int crc = 0x0000ffff;
        for (int i = 0; i < bytes.length; i++) {
            crc ^= ((int) bytes[i] & 0x000000ff);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x00000001) != 0) {
                    crc >>= 1;
                    crc ^= 0x0000a001;
                } else {
                    crc >>= 1;
                }
            }
        }
        //高低位互换，输出符合相关工具对Modbus CRC16的运算
        crc = ((crc & 0xff00) >> 8) | ((crc & 0x00ff) << 8);
        return String.format("%04X", crc);
    }

}
