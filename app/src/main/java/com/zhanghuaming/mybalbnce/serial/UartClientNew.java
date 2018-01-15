package com.zhanghuaming.mybalbnce.serial;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/12/8.
 */
public class UartClientNew {
    private String TAG = UartClientNew.class.getSimpleName();
    static UartClientNew mUartClient;
    boolean isRunning = false;
    private int num = 0;
    private OutputStream os;
    private InputStream is;

    static SerialBack mSerialBack;

    public static UartClientNew getInstance(SerialBack serialBack) {

        if (mUartClient == null) {
            mUartClient = new UartClientNew(serialBack);
        } else {
            mSerialBack = serialBack;
        }
        return mUartClient;
    }

    private UartClientNew(SerialBack serialBack) {
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
                byte[] buf = new byte[1000];
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
                                if (IntToHex(b).equals("0xAC")) {
                                    byte temp[] = new byte[cache.length - i];
                                    System.arraycopy(cache, i, temp, 0, cache.length - i);
                                    cache = temp;


//
//                                    String comp = BufToHex(cache, 0, cache.length);
//                                    Log.i(TAG, "全部数据为---" + comp);
//                                    if (-1 != comp.indexOf("0xCA")) {
//                                        if (mSerialBack != null) {
//                                            mSerialBack.sHaveweight(22);
//                                        }else {
//                                            Log.i(TAG,"0xCA mSerialBack = null");
//                                        }
//                                        cache = null;
//                                    }

                                    break;
                                } else if (i == cache.length - 1) {
                                    cache = null;
                                    break;
                                }
                            }

                            while (cache != null && cache.length >= 8) {
                                int beforeSize = cache.length;
                                if (cache != null && cache.length >= 2 && IntToHex(cache[0]).equals("0xAC") && IntToHex(cache[1]).equals("0x2")) {
                                    if (IntToHex(cache[6]).equals("0xCE")) {
                                        cache = caseWeight(cache);
                                    } else if (IntToHex(cache[6]).equals("0xCA")) {
                                        cache = caseWeight(cache);
                                    } else if (IntToHex(cache[6]).equals("0xCB")&&IntToHex(cache[2]).equals("0xFD")) {
                                        cache = caseBodyfat(cache);
                                    }
                                    if (cache != null && cache.length == beforeSize) {
                                        byte[] temp = new byte[cache.length - 8];
                                        System.arraycopy(cache, 8, temp, 0, cache.length - 8);
                                        cache = temp;
                                    }
                                } else {
                                    if (cache != null) {
                                        byte[] temp = new byte[cache.length - 2];
                                        System.arraycopy(cache, 2, temp, 0, cache.length - 2);
                                        cache = temp;
                                    }
                                }
                            }
                        }
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

    public byte[] caseWeight(byte[] cache) {
        double result = -1;
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
        }

        if (result == -1) {
            Log.i(TAG, "体重信息有错");
        }

        return cache;
    }

    public double analysisWeight(byte[] buf) {//解析,需要判断是否为-1
        String resultString = "";
        double result;
        int sum = 0;
        for (int i = 2; i < 7; i++) {
            sum += buf[i];
        }
        String bb = UartClient.IntToHexNoHead(sum);
        if (IntToHexNoHead(buf[7]).equals(bb)) {//校验
            resultString += IntToHexNoHead2(buf[2]);//未处理重量高字节2
            resultString += IntToHexNoHead2(buf[3]);//重量低字节

            result = Integer.parseInt(resultString, 16) * 0.1;
            BigDecimal b = new BigDecimal(result);
            result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (IntToHexNoHead(buf[6]).equals("CE")) {
                if (mSerialBack != null) {
                    mSerialBack.sInstabilityPeople(result);
                } else {
                    Log.i(TAG, "0xCA mSerialBack = null");
                }
                Log.i(TAG, "变化重量为：" + result);
                return result;
            } else if (IntToHexNoHead(buf[6]).equals("CA")) {
                Log.i(TAG, "稳定重量为：" + result);
                if (mSerialBack != null) {
                    mSerialBack.sHaveweight(result);
                } else {
                    Log.i(TAG, "0xCA mSerialBack = null");
                }
                return result;
            }
        }
        Log.i(TAG, "解析重量的结果有错");
        return -1;
    }

    public byte[] caseBodyfat(byte[] cache) {
        double result = -1;
        if (cache.length == 8) {
            //逻辑操作
            Log.i(TAG, "解析阻抗中");
            result = analysisBodyfat(cache);
            cache = null;
        } else if (cache.length > 8) {
            result = analysisBodyfat(cache);
            Log.i(TAG, "解析阻抗后有剩余" + (cache.length - 8));
            byte[] temp = new byte[cache.length - 8];
            System.arraycopy(cache, 8, temp, 0, cache.length - 8);
            cache = temp;
        }

        if (result == -1) {
            Log.i(TAG, "阻抗信息有错");
        }

        return cache;
    }

    public double analysisBodyfat(byte[] buf) {//解析,需要判断是否为-1
        String resultString = "";
        double result;
        int sum = 0;
        for (int i = 2; i < 7; i++) {
            sum += buf[i];
        }
        String bb = UartClient.IntToHexNoHead(sum);
        if (IntToHexNoHead(buf[7]).equals(bb)) {//校验
            if (IntToHex(buf[3]).equals("0x0")) {
                resultString += IntToHexNoHead2(buf[4]);
                resultString += IntToHexNoHead2(buf[5]);
                result = Integer.parseInt(resultString, 16);
                BigDecimal b = new BigDecimal(result);
                result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Log.i(TAG, "阻抗测量中");
                if (mSerialBack != null) {
                    mSerialBack.sBeginBodyfat(result);
                } else {
                    Log.i(TAG, "0xCA mSerialBack = null");
                }
                return 0;
            } else if (IntToHex(buf[3]).equals("0x1")) {
                Log.i(TAG, "阻抗测量完成");
                resultString += IntToHexNoHead2(buf[4]);
                resultString += IntToHexNoHead2(buf[5]);
                result = Integer.parseInt(resultString, 16);
                BigDecimal b = new BigDecimal(result);
                result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                Log.i(TAG, "稳定阻抗的结果为：" + result);
                if (mSerialBack != null) {
                    mSerialBack.sHaveBodyfat(result);
                } else {
                    Log.i(TAG, "0xCA mSerialBack = null");
                }
                return result;
            } else if (IntToHex(buf[3]).equals("0xFF")) {
                if (mSerialBack != null) {
                    mSerialBack.sBodyfatError();
                } else {
                    Log.i(TAG, "0xCA mSerialBack = null");
                }
            }
        }
        Log.i(TAG, "解析的结果有错");
        return -1;
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

    public static String IntToHexNoHead2(int n) {
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
        String r = sb.toString();
        if (r.length() == 1) {
            return '0' + r;
        } else {
            return r;
        }
    }

}
