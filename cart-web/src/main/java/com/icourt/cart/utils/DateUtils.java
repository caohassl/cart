package com.icourt.cart.utils;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils extends org.apache.commons.lang.time.DateUtils{

	private static final String[] CN_MM = { "", "\u4e00\u6708", "\u4e8c\u6708",
			"\u4e09\u6708", "\u56db\u6708", "\u4e94\u6708", "\u516d\u6708",
			"\u4e03\u6708", "\u516b\u6708", "\u4e5d\u6708", "\u5341\u6708",
			"\u5341\u4e00\u6708", "\u5341\u4e8c\u6708" };

	private static final String[] PATTERNS = new String[]{"yyyy年MM月dd日", "yyyy年MM月", "yyyy-MM-dd", "yyyy-MM", "yyyyMM", "yyyy/MM",
			"yyyyMMdd", "yyyy/MM/dd", "yyyyMMddHHmmss",
			"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"};

	public static String parseDateYYYY_MM_dd(String sDate) {
		try {
			Date date = parseDate(sDate, PATTERNS);
			return getFormatDate(date);
		} catch (Exception e) {
			log.error("日期转化失败，失败原因", e);
		}
		return null;
	}
	/**
	 * 得到月份的中文名
	 * 
	 * @param myDate
	 * @return
	 */
	public static String formatDateCN(Date myDate) {
		String strDate = CN_MM[getMonth(myDate)];
		return strDate;
	}

	/**
	 * 得到日期对应的月份数
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		return cld.get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到本月对应的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static String getMonthFristDay() {
		Date date = new Date();
		Calendar cld = Calendar.getInstance();
		cld.setTime(date);
		int day = cld.get(Calendar.DAY_OF_MONTH) - 1;
		return getPriorDayDateStr(day);
	}

	/**
	 * 获取当前月份的日期
	 * 
	 * @return
	 */
	public static String getCurMonthDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		return simpleDateFormat.format(new Date());
	}

	/**
	 * 获取格式化今天的日期字符串
	 * 
	 * @return
	 */
	public static String getCurDayDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(new Date());
	}

	public static String getCurDaySimple() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.format(new Date());
	}
	
	public static String getCurTime() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return simpleDateFormat.format(new Date());
	}

	public static String getTimeStr(Date d) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		return simpleDateFormat.format(d);
	}

	/**
	 * 得到今天的长整型时间since January 1, 1970, 00:00:00 GMT represented by this Date
	 * object.
	 * 
	 * @return
	 */
	public static long getTodayTime() {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar todaycld = Calendar.getInstance();
		todaycld.set(year, month, day, 0, 0, 0);
		return todaycld.getTime().getTime();
	}

	/**
	 * 判断是否今天
	 * 
	 * @param atime
	 * @return
	 */
	public static boolean isTodayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar todaycld = Calendar.getInstance();
		todaycld.set(year, month, day, 0, 0, 0);
		if (atime + 1000l >= todaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否昨天
	 * 
	 * @param atime
	 * @return
	 */
	public static boolean isLastdayTime(long atime) {
		Calendar cld = Calendar.getInstance();
		// cld.setTime(new Date());
		cld.add(Calendar.DAY_OF_MONTH, -1);
		int year = cld.get(Calendar.YEAR);
		int month = cld.get(Calendar.MONTH);
		int day = cld.get(Calendar.DAY_OF_MONTH);
		Calendar lastdaycld = Calendar.getInstance();
		lastdaycld.set(year, month, day, 0, 0, 0);
		if (atime >= lastdaycld.getTime().getTime()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转换字符串为日期
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static Date getFormatDateOnDay(String s) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.parse(s);
	}

	/*
	 * 转换为中文日期
	 */
	public static String getFormatZHDay(String s) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sf = new SimpleDateFormat();
		sf.applyPattern("yyyy\u5E74MM\u6708dd\u65E5");
		return sf.format(date);
	}

	/**
	 * 转换字符串为日期和时间
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	public static Date getFormatDateOnDayAndTime(String s) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			return simpleDateFormat.parse(s);
		} catch (Exception e) {
			System.out.println("date format is wrong.");
		}
		return null;
	}

	/**
	 * 转换时间字符串为日期和时间
	 * 
	 * @param s
	 * @return
	 */
	public static Date getFormatTime(String s) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			return simpleDateFormat.parse(s);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取前几天的日期字符串
	 * 
	 * @param num
	 * @return
	 * @throws Exception
	 */
	public static String getPriorDayDateStr(int num) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) - num);
		String beforeDate = dft.format(date.getTime());
		return beforeDate;
	}

	/**
	 * 获取前几天的日期
	 * 
	 * @param num
	 * @return
	 */
	public static Date getPriorDayDate(int num) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) - num);
		String beforeDate = dft.format(date.getTime()) + " 00:00:00";
		Date fdate = null;
		try {
			fdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(beforeDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fdate;
	}

	public static Date getPriorDayNineAM(Date st,int num) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.setTime(st);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - num);
		String beforeDate = dft.format(date.getTime()) + " 09:00:00";
		Date fdate = null;
		try {
			fdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(beforeDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fdate;
	}
	
	
	public static Date getPriorDayLastTime(int num) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) - num);
		String beforeDate = dft.format(date.getTime()) + " 23:59:59";
		Date fdate = null;
		try {
			fdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(beforeDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fdate;
	}

	/**
	 * 获取后几天的日期
	 * 
	 * @param num
	 * @return
	 */
	public static Date getNextDayDate(int num) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) + num);
		String nextDate = dft.format(date.getTime()) + " 00:00:00";
		Date ndate = null;
		try {
			ndate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(nextDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ndate;
	}

	/**
	 * 获取今年第一天的日期
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Date getCurYearFristDate() throws Exception {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Calendar date = Calendar.getInstance();
		String beforeDate = date.get(Calendar.YEAR) + "-01-01 00:00:00";
		return dft.parse(beforeDate);
	}

	/**
	 * 时间转换
	 * 
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long Time2Long(int year, int month, int date, int hour,
			int minute, int second) {
		Calendar cld = Calendar.getInstance();
		month = month - 1;
		cld.set(year, month, date, hour, minute, second);
		return cld.getTime().getTime();
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String getFormatDate(Date date) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getFormatDate(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	/**
	 * 返回两个日期相差的天数
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static int getDistDates(Date startDate, Date endDate) {
		long totalDate = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		long timestart = calendar.getTimeInMillis();
		calendar.setTime(endDate);
		long timeend = calendar.getTimeInMillis();
		totalDate = Math.abs((timeend - timestart)) / (1000 * 60 * 60 * 24);
		return (int) totalDate;
	}

	public static int getDistDates(String startDate, String endDate) {
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		Date eDate = null;
		try {
			sDate = dft.parse(startDate);
			eDate = dft.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long num = (eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60 * 24);
		return (int) num;
	}
	/**
	 * 
	 * 获取当前时间到今天凌晨所剩毫秒数
	 * 
	 * @return
	 */
	public static int getTimeMillisLeftToday(){
		long currentTimeMillis = System.currentTimeMillis();
		long todayLeftTimeMillis = 1000*60*60*24-(currentTimeMillis%(1000*60*60*24) + 1000*60*60*8);
		return (int)todayLeftTimeMillis;
	}

	public static int getTimeSecondsLeftToday(){
		return 60*60*24- new DateTime().getSecondOfDay();
	}
	
	public static int getDistHours(Date startTime, Date endTime) {
		long num = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60);
		return (int) num;
	}


	/**
	 * 获取指定日期后几天的日期
	 *
	 * @param num
	 * @return
	 */
	public static Date getNextDayDate(Date date,int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)+num);
		return calendar.getTime();
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getTimeSecondsLeftToday());
//		Date d =DateUtils.getNextDayDate(2);
//		Date p = DateUtils.getPriorDayNineAM(d,1);
//		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		System.out.println(dft.format(d));
//		System.out.println(dft.format(p));
		
//		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date sDate = dft.parse("2010-06-31 16:00:00");
//		System.out.println(getFormatDate(sDate,"yyyy-MM-dd hh:mm"));
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm:ss");
//		System.out.println(simpleDateFormat.format(new Date()));
	}

}
