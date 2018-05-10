package com.simon.utils.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.simon.utils.utils.singtonutils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

@SuppressWarnings("all")
public class ExcelUtils {

    public static WritableCellFormat titleFormat;
    public static WritableCellFormat commonFormat;
    public static WritableCellFormat specialFormat;

    public final static String UTF8_ENCODING = "UTF-8";

    /**
     * 初始化表格
     */
    public static void initExcel(String fileName, String sheetName, String[] columnNames) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                workbook = Workbook.createWorkbook(file);
                WritableSheet sheet = workbook.createSheet(sheetName, 0);
                for (int column = 0; column < columnNames.length; column++) {
                    sheet.addCell(new Label(column, 0, columnNames[column], titleFormat));
                }
                workbook.write();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 格式化字体样式
     */
    public static void format() {
        try {
            WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 12,
                    WritableFont.BOLD);
            titleFormat = new WritableCellFormat(titleFont);
            titleFormat.setAlignment(jxl.format.Alignment.CENTRE);
            titleFormat.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            WritableFont commonFont = new WritableFont(WritableFont.ARIAL, 12);
            commonFormat = new WritableCellFormat(commonFont);
            commonFormat.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            WritableFont specialFont = new WritableFont(WritableFont.ARIAL, 12);
            specialFormat = new WritableCellFormat(commonFont);
            specialFormat.setBorder(jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN);
            specialFormat.setBackground(Colour.DARK_RED);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取Excel表格
     */
    public static Object getDataByExcel(Class cls, String fieldName) {
        Object value = null;
        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName
                .substring(0, 1).toUpperCase());
        String getMethodName = "get" + fieldName;
        try {
            Method method = cls.getMethod(getMethodName);
            value = method.invoke(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 写入数据到Excel表格
     */
    public static void writeDataToExcel(List<Object> students, String excelPath) {
        if (students != null && students.size() > 0) {
            WritableWorkbook writebook = null;
            FileInputStream fis = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);
                fis = new FileInputStream(new File(excelPath));
                Workbook workbook = Workbook.getWorkbook(fis);
                writebook = Workbook.createWorkbook(new File(excelPath),
                        workbook);
                WritableSheet sheet = writebook.getSheet(0);
                for (int i = 0; i < students.size(); i++) {
                    int col = 0;
                    int row = sheet.getRows();
//                    Student student = students.get(i);
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    String date = formatter.format(student.getDate());
//                    sheet.addCell(new Label(col++, row, date, commonFormat));
//                    sheet.addCell(new Label(col++, row, student.getName(), commonFormat));
//                    sheet.addCell(new Label(col++, row, student.getAge() + "", commonFormat));
                }
                writebook.write();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据路径打开文件
     *
     * @param context 上下文
     * @param path    文件路径
     */
    public static void openFileByPath(Context context, String path) {
        if (context == null || path == null)
            return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //文件的类型
        String type = "";
        for (int i = 0; i < MATCH_ARRAY.length; i++) {
            //判断文件的格式
            if (path.toString().contains(MATCH_ARRAY[i][0].toString())) {
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        try {
            //设置intent的data和Type属性
            intent.setDataAndType(Uri.fromFile(new File(path)), type);
            //跳转
            context.startActivity(intent);
        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            ToastUtils.getInstance().showShortToast("无法打开该格式文件!");
            e.printStackTrace();
        }
    }

    //建立一个文件类型与文件后缀名的匹配表
    private static final String[][] MATCH_ARRAY = {
            //{后缀名，    文件类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };

}
