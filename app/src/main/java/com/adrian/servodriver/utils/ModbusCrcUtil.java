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
     * 读从机数据
     *
     * @param slave_addr 从机地址
     * @param data_addr  数据地址
     * @param data_count 读取数据个数
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
     *
     * @param slave_addr 从机地址
     * @param data_addr  数据地址
     * @param data       写入数据
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
        Log.e("CRC", "crc:" + crc);
        byte[] cmd = new byte[cmd_0.length + c.length];
        System.arraycopy(cmd_0, 0, cmd, 0, cmd_0.length);
        System.arraycopy(c, 0, cmd, cmd_0.length, c.length);
        return cmd;
    }

    /**
     * Convert hex string to byte[]
     *
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
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 计算并获得16CRC
     *
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
//        return String.format("%04X", crc);
        return Integer.toHexString(crc);
    }

    /**
     * 计算CRC16校验码
     * 同getCRC
     *
     * @param arr_buff 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCrc16(byte[] arr_buff) {
        int len = arr_buff.length;

        //预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            //把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                //把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    //如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else
                    //如果移出位为 0,再次右移一位
                    crc = crc >> 1;
            }
        }
        return Integer.toHexString(crc);
    }

    /**
     * 计算CRC16校验码
     * 同getCrc16
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    /**
     * 将16进制单精度浮点型转换为10进制浮点型
     *
     * @return float
     * @since 1.0
     */
    private float parseHex2Float(String hexStr) {
        BigInteger bigInteger = new BigInteger(hexStr, 16);
        return Float.intBitsToFloat(bigInteger.intValue());
    }

    /**
     * 将十进制浮点型转换为十六进制浮点型
     *
     * @return String
     * @since 1.0
     */
    private String parseFloat2Hex(float data) {
        return Integer.toHexString(Float.floatToIntBits(data));
    }
}
