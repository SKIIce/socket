package com.test.sp;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncTime extends Thread{
    private static int sleepMinutes = 0;
    private static final long EPOCH_OFFSET_MILLIS;
    private static final String[] hostName = {"ntp.sjtu.edu.cn"};

    static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // Java使用的参照标准是1970年，而时间服务器返回的秒是相当1900年的，算一下偏移
        calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
        EPOCH_OFFSET_MILLIS = Math.abs(calendar.getTime().getTime());
    }

    public static void main(String[] args) {
        new SyncTime().start();
    }

    @Override
    public void run(){
        while(true){
            try{
                for(int i = 0 ; i < 3; i++) {
                    GetWebTime();
                    Thread.sleep(8000);
                }
                Thread.sleep(1000 * 60 * 8 - 3 * 8000) ;
            }catch(Exception e){

            }
        }
    }

    private static Date getNetDate(String hostName) {
        Date date = null;
        String webUrl="http://www.baidu.com"; //百度时间
        try {
            URL url=new URL(webUrl);
            URLConnection conn=url.openConnection();
            conn.connect();
            long dateL=conn.getDate();
            date=new Date(dateL);

            SimpleDateFormat dateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            System.out.println("当前网络时间：" + dateFormat.format(date));
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return date;
    }



    /**
     * 通过ping命令判断是否离线
     * @return
     */
    public static boolean offLine() {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("ping www.hao123.com");
            InputStream s = process.getInputStream();
            byte[] bytes = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int len;
            while((len = s.read(bytes)) != -1){
                String str = new String(bytes,0,len,"GBK");
                sb.append(str);
            }
            String result = sb.toString();
            //System.out.println(result);
            if(result.contains("0% 丢失")){
                return false;
            }
            process.waitFor();

        } catch (IOException ex) {
            Logger.getLogger(SyncTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SyncTime.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }


    /**
     * 通过调用本地命令date和time修改计算机时间
     * @param date
     */
    private static void setComputeDate(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND) + 1;

        c.setTime(new Date());
        int year_c = c.get(Calendar.YEAR);
        int month_c = c.get(Calendar.MONTH) + 1;
        int day_c = c.get(Calendar.DAY_OF_MONTH);
        int hour_c = c.get(Calendar.HOUR_OF_DAY);
        int minute_c = c.get(Calendar.MINUTE);

        String ymd = year + "-" + month + "-" + day;
        String time = hour + ":" + minute + ":" + second;

        try {

            // 日期不一致就修改一下日期
            if (year != year_c || month != month_c || day != day_c) {
                String cmd = "cmd /c date " + ymd;
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            }

            // 时间不一致就修改一下时间
            if (hour != hour_c || minute != minute_c) {
                String cmd = "cmd  /c  time " + time;
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            }

        } catch (IOException ex) {
            Logger.getLogger(SyncTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SyncTime.class.getName()).log(Level.SEVERE, null, ex);

        }

    }



    public static void GetWebTime()
    {
        // 从网络上获取时间
        Date date = null;
        for (int i = 0; i < hostName.length; i++) {
            date = getNetDate(hostName[i]);
            if (date != null) {
                break;
            }
        }

        // 修改本机时间
        if (date != null) {
            System.out.println("原子钟时间："+date);
            setComputeDate(date);
        }
    }

}
