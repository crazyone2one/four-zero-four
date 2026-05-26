package cn.master.system;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
public class Codegen {


    public static GlobalConfig createGlobalConfigUseStyle1( String[] tables) {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.getJavadocConfig().setAuthor("11's papa");
        //设置根包
        globalConfig.setBasePackage("cn.master.system");
        globalConfig.setSourceDir(System.getProperty("user.dir") + "/backend/services/system-service/src/main/java");
        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("tb_");
        globalConfig.setGenerateTable(tables);

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(25);
        globalConfig.getEntityConfig().setWithSwagger(true).setSwaggerVersion(EntityConfig.SwaggerVersion.DOC);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);
        globalConfig.enableController();
        globalConfig.setServiceGenerateEnable(true);
        globalConfig.setServiceImplGenerateEnable(true);
        for (String tableName : tables) {
            ColumnConfig columnConfig = new ColumnConfig();
            columnConfig.setColumnName("create_time");
            columnConfig.setOnInsertValue("now()");
            globalConfig.setColumnConfig(tableName, columnConfig);

            ColumnConfig columnConfig2 = new ColumnConfig();
            columnConfig2.setColumnName("update_time");
            columnConfig2.setOnInsertValue("now()");
            columnConfig2.setOnUpdateValue("now()");
            globalConfig.setColumnConfig(tableName, columnConfig2);
        }

        return globalConfig;
    }
    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/malo?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle1(new String[]{"schedule"});
        //GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
        System.out.println(System.getProperty("user.dir"));
    }


}
