package com.atguigu.eduService.excel;


import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {
    public static void main(String[] args) {
        /**
         * 写操作
         */
        // String fileName = "F:\\01.xlsx";
        // EasyExcel.write(fileName,DemoData.class).sheet("学生列表").doWrite(getData());
        /**
         * 读操作
         */
        String fileName = "F:\\01.xlsx";
        EasyExcel.read(fileName,DemoData.class,new ExcelListener()).sheet().doRead();
    }

    private static List<DemoData> getData() {
        List<DemoData> list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            DemoData demoData = new DemoData();
            demoData.setSno(i);
            demoData.setSname("lucy"+i);
            list.add(demoData);
        }
        return list;
    }
}
