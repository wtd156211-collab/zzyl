package com.zzyl;

import com.zzyl.common.utils.PDFUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PDFTest {
    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream fis= new FileInputStream( "D:\\a\\Information\\阶段四_企业级智能物联网项目\\中州养老-全资料\\09. 智能评估-集成AI大模型\\资料\\体检报告样例\\体检报告-张芳-女-72岁.pdf");
        String s = PDFUtil.pdfToString(fis);
        System.out.println(s);
    }
}
