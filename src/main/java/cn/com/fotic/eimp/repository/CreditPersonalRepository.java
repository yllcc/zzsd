package cn.com.fotic.eimp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.com.fotic.eimp.repository.entity.CreditFraudDic;
import cn.com.fotic.eimp.repository.entity.CreditPersonalDic;

public interface CreditPersonalRepository extends JpaRepository<CreditPersonalDic, Long> {

}
