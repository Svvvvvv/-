package com.atguigu.eduservice.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectData {
    @ExcelProperty(index = 0) // Excel文件第一列
    private String oneSubjectName;
    @ExcelProperty(index = 1) // Excel文件第二列
    private String twoSubjectName;
}
