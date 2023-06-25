package com.chatgpt.config;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.HashMap;
import java.util.Map;

public class CodeGeneator {
    public static void main(String[] args) {
        /**
         * 先配置数据源
         */
        MySqlQuery mySqlQuery = new MySqlQuery() {
            @Override
            public String[] fieldCustom() {
                return new String[]{"Default"};
            }
        };


        DataSourceConfig dsc = new DataSourceConfig
                .Builder("jdbc:mysql://localhost:3306/chatgpt?serverTimezone=UTC&useSSL=true&useUnicode=true&characterEncoding=UTF-8",
                "root",
                "200008071215xu")
                .dbQuery(mySqlQuery).
                build();


        //通过datasourceConfig创建AutoGenerator
        AutoGenerator generator = new AutoGenerator(dsc);

        /**
         * 全局配置
         */
        String projectPath = System.getProperty("user.dir"); //获取项目路径
        String filePath = projectPath + "/src/main/java";  //java下的文件路径
        GlobalConfig global = new GlobalConfig.Builder()
                .outputDir(filePath)//生成的输出路径
                .author("young")//生成的作者名字
                .dateType(DateType.TIME_PACK)//时间策略
                .commentDate("yyyy年MM月dd日")//格式化时间格式
                .disableOpenDir()//禁止打开输出目录，默认false
                .fileOverride()//覆盖生成文件
                .build();

        /**
         * 包配置
         */
        PackageConfig packages = new PackageConfig.Builder()
                .entity("entity")//实体类包名
                .parent("com.chatgpt")//父包名。如果为空，将下面子包名必须写全部， 否则就只需写子包名
                .controller("controller")//控制层包名
                .mapper("mapper")//mapper层包名
                .xml("mapper.xml")//数据访问层xml包名
                .service("service")//service层包名
                .serviceImpl("service.impl")//service实现类包名
                .other("output")//输出自定义文件时的包名
                //.pathInfo()路径配置信息,就是配置各个文件模板的路径信息
                .build();
        /**
         * 模板配置
         */
        TemplateConfig template = new TemplateConfig.Builder()
//                .disable()//禁用所有模板
//                .disable(TemplateType.ENTITY)禁用指定模板
//                .entity("/templates/entity.java")
//                .service("/templates/service.java")
//                .serviceImpl("/templates/serviceImpl.java")
//                .mapper("/templates/mapper.java")
//                .mapperXml("/templates/mapper.xml")
//                .controller("/templates/controller.java")
                .build();


        /**
         * 注入配置,自定义配置一个Map对象，可以不用配置
         */
        Map<String,Object> map = new HashMap<>();
        map.put("name","young");
        map.put("age","22");
        map.put("sex","男");
        map.put("description","深情不及黎治跃");

        InjectionConfig injectionConfig = new InjectionConfig.Builder()
                .customMap(map)
                .build();

        /**
         * 策略配置开始
         */
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                .enableCapitalMode()//开启全局大写命名
                //.likeTable()模糊表匹配
                .addInclude()//添加表匹配，指定要生成的数据表名，不写默认选定数据库所有表
                //.disableSqlFilter()禁用sql过滤:默认(不使用该方法）true
                //.enableSchema()启用schema:默认false

                .entityBuilder() //实体策略配置
                //.disableSerialVersionUID()禁用生成SerialVersionUID：默认true
                .enableChainModel()//开启链式模型
                .enableLombok()//开启lombok
                .enableRemoveIsPrefix()//开启 Boolean 类型字段移除 is 前缀
                .enableTableFieldAnnotation()//开启生成实体时生成字段注解
                //.addTableFills()添加表字段填充
                .naming(NamingStrategy.underline_to_camel)//数据表映射实体命名策略：默认下划线转驼峰underline_to_camel
                .columnNaming(NamingStrategy.underline_to_camel)//表字段映射实体属性命名规则：默认null，不指定按照naming执行
                .idType(IdType.AUTO)//添加全局逐渐类型
                .formatFileName("%s")//格式化实体名称，%s取消首字母I
                .build()

                .mapperBuilder()//mapper文件策略
                .enableMapperAnnotation()//开启mapper注解
                .enableBaseResultMap()//启用xml文件中的BaseResultMap 生成
                .enableBaseColumnList()//启用xml文件中的BaseColumnList
                //.cache(缓存类.class)设置缓存实现类
                .formatMapperFileName("%sDao")//格式化Dao类名称
                .formatXmlFileName("%sXml")//格式化xml文件名称
                .build()

                .serviceBuilder()//service文件策略
                .formatServiceFileName("%sService")//格式化 service 接口文件名称
                .formatServiceImplFileName("%sServiceImpl")//格式化 service 接口文件名称
                .build()

                .controllerBuilder()//控制层策略
                //.enableHyphenStyle()开启驼峰转连字符，默认：false
                .enableRestStyle()//开启生成@RestController
                .formatFileName("%sController")//格式化文件名称
                .build();
        /*至此，策略配置才算基本完成！*/

        /**
         * 将所有配置项整合到AutoGenerator中进行执行
         */
//        VelocityTemplateEngine engine = new VelocityTemplateEngine();


        generator.global(global)
                .template(template)
                //.injection(injectionConfig)
                .packageInfo(packages)
                .strategy(strategyConfig)
                .execute();
    }

}
