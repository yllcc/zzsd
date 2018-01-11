package cn.com.fotic.eimp.config;

import org.springframework.beans.factory.annotation.Qualifier;  
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;  
import org.springframework.boot.context.properties.ConfigurationProperties;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;  
import org.springframework.context.annotation.Primary;  
  
import javax.sql.DataSource;  
  
/** 
 * Created by Administrator on 2017/8/11. 
 * @author liugj
 * 数据源的配置 
 * 
 */  
@Configuration  
public class DataSourceConfig {  
  
  
    @Bean(name = "primaryDataSource")  
    @Qualifier("primaryDataSource")  
    @ConfigurationProperties(prefix="spring.datasource")  
    public DataSource primaryDataSource() {  
        return DataSourceBuilder.create().build();  
    }  
    @Bean(name = "secondaryDataSource")  
    @Qualifier("secondaryDataSource")  
    @Primary  
    @ConfigurationProperties(prefix="custom.datasource")  
    public DataSource secondaryDataSource() {  
        return DataSourceBuilder.create().build();  
    }  
  
  
  
}  
