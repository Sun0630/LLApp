package com.heaton.liulei.utils.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * @param requestMap
     * @return
     */
    public static HashMap<String, Object> toJson(Map<String, Object> requestMap) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String key : requestMap.keySet()) {
            sb.append("\"").append(key).append("\":\"").append(requestMap.get(key)).append("\",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("}");
        map.put("Json", sb.toString());
        return map;
    }

    public static String Double2String(double d) {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        return fnum.format(d);
    }

    /**
     * 将int型R.sting.  转成string
     * @param id
     * @return
     */
    public static String getStrFromRes(int id) {
        return LiuleiUtils.mContext.getResources().getString(id);
    }

    /**
     * 将long型的内存大小转成String
     * @param size
     * @return
     */
    public static String long2Str(long size) {
        if (size / 1000 >= 0) {
            if (size / 1000000 > 0) {
                return (size / 10000000) + "M";
            } else {
                return (size / 1000) + "K";
            }
        } else {
            return size + "B";
        }
    }

    /**
     * 判断电话号码是否合法
     * @param phoneNumber
     * @return
     */
    public static boolean isMObileNO(String phoneNumber) {
        boolean isValid = false;

        String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
        String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);
        if (matcher.matches() || matcher2.matches()) {
            isValid = true;
        }
        return !isValid;
    }

    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }

   /* public static String getTodayStartStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        return sdf.format(d);
    }

    public static String getStrFronDate(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(d);
    }

    public static Date getDateFronStr(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * 格式化时间，带小时
     * @param duration 秒值
     * @return hh:MM:ss
     */
    public static String formatTimeHasHour(long duration) {
        String hh = duration / 3600 > 9 ? duration / 3600 + "" : "0" + duration / 3600;
        String mm = (duration % 3600) / 60 > 9 ? (duration % 3600) / 60 + "" : "0" + (duration % 3600) / 60;
        String ss = (duration % 3600) % 60 > 9 ? (duration % 3600) % 60 + "" : "0" + (duration % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    /**
     * 格式化时间，时间没到小时就不带
     * @param duration 毫秒值
     * @return hh:MM:ss
     */
    public static String formatTime(long duration) {
        long hours = (duration % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;
        long milliSecond = duration % 1000;
        if (hours < 1) {
            return (minutes > 9 ? minutes : "0" + minutes) + "\"" + (seconds > 9 ? seconds : "0" + seconds)/* + "\"" +
                    (milliSecond > 99 ? milliSecond : (milliSecond > 9 ? "0" + milliSecond : "00" + milliSecond))*/;
        }
        return (hours > 9 ? hours : "0" + hours) + "\"" + (minutes > 9 ? minutes : "0" + minutes) + "\"" +
                (seconds > 9 ? seconds : "0" + seconds)/* + "\"" + (milliSecond > 99 ? milliSecond : (milliSecond > 9 ? "0" + milliSecond : "00" + milliSecond))*/;
    }

    /**
     * *****对String进行加密解密*****
     */
    public static String EncodeBase64Str(String str) {
        // 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
        String strBase64 = new String(Base64.encode(str.getBytes(), Base64.NO_WRAP));
        return strBase64;
    }

    public static String DecodeBase64Str(String strBase64) {
        String str = new String(Base64.decode(strBase64.getBytes(), Base64.NO_WRAP));
        return str;
    }

    public static String EncodeURLStr(String str) {
        // 在这里使用的是encode方式，返回的是byte类型加密数据，可使用new String转为String类型
        String strurl = "";
        try {
            strurl = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strurl;
    }

    public static String DecodeURLStr(String strBase64) {
        String strurl = "";
        try {
            strurl = URLDecoder.decode(strBase64, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strurl;
    }

   /* public static SpannableString setFaceToTextView(Context c, String str, TextView tv) {
        SpannableString spannableString = null;
        try {
            spannableString = ExpressionUtil.getExpressionString(c, str, ConstantValue.PATTER_FACE, tv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableString;
    }*/

    /**
     *
     * @param string  需要md5加密的字符串
     * @return  加密后的字符串
     */
   public static String md5(String string) {
       byte[] hash;
       try {
           hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
       } catch (NoSuchAlgorithmException e) {
           throw new RuntimeException("Huh, MD5 should be supported?", e);
       } catch (UnsupportedEncodingException e) {
           throw new RuntimeException("Huh, UTF-8 should be supported?", e);
       }

       StringBuilder hex = new StringBuilder(hash.length * 2);
       for (byte b : hash) {
           if ((b & 0xFF) < 0x10) hex.append("0");
           hex.append(Integer.toHexString(b & 0xFF));
       }
       return hex.toString();
   }

}
