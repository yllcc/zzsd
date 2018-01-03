package cn.com.fotic.eimp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.com.fotic.eimp.repository.entity.BackCredit;

public interface CreditRepository extends JpaRepository<BackCredit, Long> {
    
	@Query(nativeQuery = true,value = "SELECT * FROM BACK_CREDIT WHERE CUST_NAME = :cust_name AND CERT_TYPE = :cert_type AND CERT_NUM = :cert_num")
    public BackCredit getOptName(@Param("cust_name") String cust_name,@Param("cert_type") String cert_type,@Param("cert_num") String cert_num);
	
	
}
