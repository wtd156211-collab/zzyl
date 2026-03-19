package com.zzyl;

import com.zzyl.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Filter;

public class VelocityDemoTest {
    public static void main(String[] args) throws IOException {
        //1.初始化引擎
        VelocityInitializer.initVelocity();

        //2.创建上下文对象
        VelocityContext context = new VelocityContext();
        context.put("message", "加油欣欣~~~");

        //3.获取模板文件
        Template template = Velocity.getTemplate("vms/index.html.vm", "UTF-8");

        //4.输出文件
        FileWriter writer = new FileWriter("zzyl-generator\\src\\main\\resources\\index.html");

        //5.合并上下文对象和模板文件
        template.merge(context, writer);

        //6.关闭流
        writer.close();
    }
}
