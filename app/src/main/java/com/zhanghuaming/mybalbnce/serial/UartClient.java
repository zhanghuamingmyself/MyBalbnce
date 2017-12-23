package com.zhanghuaming.mybalbnce.serial;

import android.util.Log;


import com.zhanghuaming.mybalbnce.utils.SerialBack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/12/8.
 */
public class UartClient {
    private String TAG = UartClient.class.getSimpleName();
    static UartClient mUartClient;
    boolean isRunning = false;
    private int num = 0;
    private OutputStream os;
    private InputStream is;

    static SerialBack mSerialBack;
    public static UartClient getInstance(SerialBack serialBack) {

        if (mUartClient == null) {
            mUartClient = new UartClient(serialBack);
        }else {
            mSerialBack = serialBack;
        }
        return mUartClient;
    }

    private UartClient(SerialBack serialBack) {
        mSerialBack = serialBack;
    }


    public boolean sendMsg(byte[] buf) {
        if (os != null) {
            try {
                os.write(buf);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    class UartThread extends Thread {

        public UartThread() {
        }

        @Override
        public void run() {
            Log.d(TAG, "线程开了");
            try {
                is = UartUtils.getInstance().getInputStream();
                os = UartUtils.getInstance().getOutputStream();
                if (is == null) {
                    isRunning = false;
                    Log.d(TAG, "is == null");
                    return;
                }
                isRunning = true;
                byte[] cache = null;
                byte[] buf = new byte[100];
                int index = 0;
                Log.d(TAG, "进入while前");
                while (isRunning) {
                    num++;
                    Log.d(TAG, "num=" + num);
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ret=");
                    int ret = is.read(buf);
                    Log.d(TAG, "ret=" + ret);
                    Log.i(TAG, "HEX:" + BufToHex(buf, index, ret));
                    if (ret > 0) {
                        if (cache != null) {
                            byte[] temp = new byte[cache.length + ret];
                            System.arraycopy(cache, 0, temp, 0, cache.length);
                            System.arraycopy(buf, 0, temp, cache.length, ret);
                            cache = temp;
                            Log.i(TAG, "全部数据为---" + BufToHex(cache, 0, cache.length));
                        } else {
                            cache = new byte[ret];
                            System.arraycopy(buf, 0, cache, 0, ret);
                        }

                        if (cache != null && cache.length > 0) {
                            for (int i = 0; i < cache.length; i++) {
                                byte b = cache[i];
                                if (IntToHex(b).equals("0xFF")) {
                                    byte temp[] = new byte[cache.length - i];
                                    System.arraycopy(cache, i, temp, 0, cache.length - i);
                                    cache = temp;
                                    Log.i(TAG, "全部数据为---" + BufToHex(cache, 0, cache.length));
                                    break;
                                } else if (i == cache.length - 1) {
                                    cache = null;
                                }
                            }
                        }
                        cache = caseWeight(cache);
                        cache = caseNoPeople(cache);
                        cache = caseAferSettingCloseTime(cache);
                    } else {
                        //不会执行，read会一直阻塞
                        Log.d(TAG, "read from uart empty");
                    }

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isRunning = false;
                Log.d(TAG, e.getMessage());

            }
            Log.i(TAG, "UartKeyBoardClient run end");
        }
    }

    public byte[] caseWeight(byte[] cache){
        double result = -1;
        if (cache != null && cache.length >= 2 && IntToHex(cache[0]).equals("0xFF") && IntToHex(cache[1]).equals("0x0")) {
            if (cache.length == 8) {
                //逻辑操作
                Log.i(TAG, "解析中");
                result = analysisWeight(cache);
                cache = null;
            } else if (cache.length > 8) {
                result = analysisWeight(cache);
                Log.i(TAG, "解析后有剩余" + (cache.length - 8));
                byte[] temp = new byte[cache.length - 8];
                System.arraycopy(cache, 8, temp, 0, cache.length - 8);
                cache = temp;
            } else {
                Log.i(TAG, "解析其他");
                cache = null;
            }

            if(result == -1){
                Log.i(TAG,"体重信息有错");
            }else if(result ==0){
                mSerialBack.sHavePeople();
            }else {
                mSerialBack.sHaveweight(result);
            }
        }
        return cache;
    }
    public double analysisWeight(byte[] buf) {//解析,需要判断是否为-1
        String resultString = "";
        double result;

        resultString += IntToHexNoHead(buf[2]);
        resultString += IntToHexNoHead(buf[3]);
        resultString += IntToHexNoHead(buf[4]);
        resultString += '.';
        resultString += IntToHexNoHead(buf[5]);
        resultString += IntToHexNoHead(buf[6]);

        result = Double.parseDouble(resultString);
        if( IntToHexNoHead(buf[7]).equals("FE")){
            Log.i(TAG, "解析的结果为：" + result);
            return result;
        }
        Log.i(TAG, "解析的结果有错");
        return -1;
    }

    public byte[] caseNoPeople(byte[] cache){
        double result = -1;
        if (cache != null && cache.length >= 2 && IntToHex(cache[0]).equals("0xFF") && IntToHex(cache[1]).equals("0x2")) {
            if (cache.length == 8) {
                //逻辑操作
                Log.i(TAG, "解析中");
                if(IntToHex(cache[7]).equals("0xFE")){
                    mSerialBack.sNoPeople();
                }
                cache = null;
            } else if (cache.length > 8) {
                if(IntToHex(cache[7]).equals("0xFE")){
                    mSerialBack.sNoPeople();
                }
                Log.i(TAG, "解析后有剩余" + (cache.length - 8));
                byte[] temp = new byte[cache.length - 8];
                System.arraycopy(cache, 8, temp, 0, cache.length - 8);
                cache = temp;
            } else {
                Log.i(TAG, "解析其他");
                cache = null;
            }
        }
        return cache;
    }

    public byte[] caseAferSettingCloseTime(byte[] cache){
        double result = -1;
        if (cache != null && cache.length >= 2 && IntToHex(cache[0]).equals("0xFF") && IntToHex(cache[1]).equals("0x6")) {
            if (cache.length == 8) {
                //逻辑操作
                Log.i(TAG, "解析中");
                if(IntToHex(cache[7]).equals("0xFE")){
                    mSerialBack.sAfterSettingCloseTime();
                }
                cache = null;
            } else if (cache.length > 8) {
                if(IntToHex(cache[7]).equals("0xFE")){
                    mSerialBack.sAfterSettingCloseTime();
                }
                Log.i(TAG, "解析后有剩余" + (cache.length - 8));
                byte[] temp = new byte[cache.length - 8];
                System.arraycopy(cache, 8, temp, 0, cache.length - 8);
                cache = temp;
            } else {
                Log.i(TAG, "解析其他");
                cache = null;
            }
        }
        return cache;
    }
    public void start() {
        Log.i(TAG, "UartKeyBoardClient start()");
        if (!isRunning) {
            new UartThread().start();
        }
    }

    public static String BufToHex(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHex(buf[i]) + " ";
            }
        }
        return str;
    }

    public static String BufToHexString(byte[] buf) {
        String str = "";
        if (buf != null) {
            for (int i = 0; i < buf.length; i++) {
                str += IntToHex2(buf[i]);
            }
        }
        return str;
    }

    public static String BufToHexString(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHex2(buf[i]);
            }
        }
        return str;
    }

    public static String BufToHexNoHead(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHexNoHead(buf[i]) + " ";
            }
        }
        return str;
    }

    public static String IntToHexNoHead(int n) {
        if (n < 0) {
            n += 256;
        }
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    public static String IntToHex(int n) {
        if (n < 0) {
            n += 256;
        }
        char[] ch = new char[20];
        int nIndex = 0;
        int m;
        int k;
        while (true) {
            m = n / 16;
            k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        String strHex = new String("0x");
        strHex += sb.toString();
        return strHex;
    }

    public static String IntToHex2(int n) {
        if (n < 0) {
            n += 256;
        }
        int m = n / 16;
        int k = n % 16;

        String strHex = "" + byteHex(m) + byteHex(k);

        return strHex;
    }

    static char byteHex(int k) {
        char ch;
        if (k == 15)
            ch = 'F';
        else if (k == 14)
            ch = 'E';
        else if (k == 13)
            ch = 'D';
        else if (k == 12)
            ch = 'C';
        else if (k == 11)
            ch = 'B';
        else if (k == 10)
            ch = 'A';
        else
            ch = (char) ('0' + k);
        return ch;
    }

}
