package cn.com.fotic.eimp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cn.com.fotic.eimp.repository.entity.BankCredit;

public interface BankCreditRepository extends JpaRepository<BankCredit, Long> {
	@Query(nativeQuery = true,value = " select * from BANK_CREDIT where id = :id")
    public BankCredit getOptName(@Param("id") Integer id);
}
