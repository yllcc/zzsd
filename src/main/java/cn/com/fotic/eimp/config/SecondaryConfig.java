package cn.com.fotic.eimp.config;

/** 
 * Created by Administrator on 2017/8/11. 
 * 
 * 
 * 数据源2 
 */  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.beans.factory.annotation.Qualifier;  
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;  
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;  
import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;  
import org.springframework.orm.jpa.JpaTransactionManager;  
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;  
import org.springframework.transaction.PlatformTransactionManager;  
import org.springframework.transaction.annotation.EnableTransactionManagement;  
  
import javax.persistence.EntityManager;  
import javax.sql.DataSource;  
import java.util.Map;  
  
@Configuration  
@EnableTransactionManagement  
@EnableJpaRepositories(  
        entityManagerFactoryRef="entityManagerFactorySecondary",  
        transactionManagerRef="transactionManagerSecondary",  
        basePackages= { "cn.com.fotic.eimp.second" }) //设置Repository所在位置  
public class SecondaryConfig {  
  
    @Autowired  
    @Qualifier("secondaryDataSource")  
    private DataSource secondaryDataSource;  
    @Primary
    @Bean(name = "entityManagerSecondary")  
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {  
        return entityManagerFactorySecondary(builder).getObject().createEntityManager();  
    }  
    @Primary
    @Bean(name = "entityManagerFactorySecondary")  
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary (EntityManagerFactoryBuilder builder) {  
        return builder  
                .dataSource(secondaryDataSource)  
                .properties(getVendorProperties(secondaryDataSource))  
                .packages("cn.com.fotic.eimp.repository.entity") //设置实体类所在位置  
                .persistenceUnit("primaryPersistenceUnit")  
                .build();  
    }  
    @Autowired  
    private JpaProperties jpaProperties;  
    private Map<String, String> getVendorProperties(DataSource dataSource) {  
        return jpaProperties.getHibernateProperties(dataSource);  
    }  
    
    @Primary
    @Bean(name = "transactionManagerSecondary")  
    PlatformTransactionManager transactionManagerSecondary(EntityManagerFactoryBuilder builder) {  
        return new JpaTransactionManager(entityManagerFactorySecondary(builder).getObject());  
    }  
  
  
}  