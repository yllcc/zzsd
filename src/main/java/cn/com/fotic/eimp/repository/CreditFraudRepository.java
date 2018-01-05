package cn.com.fotic.eimp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.com.fotic.eimp.repository.entity.CreditFraudDic;

public interface CreditFraudRepository  extends JpaRepository<CreditFraudDic, Long> {

}
