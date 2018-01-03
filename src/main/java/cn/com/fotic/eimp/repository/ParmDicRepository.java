package cn.com.fotic.eimp.repository;

import cn.com.fotic.eimp.repository.entity.ParmDic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParmDicRepository extends JpaRepository<ParmDic, Long> {
    
    @Query(nativeQuery = true,value = "select opt_name from parm_dic where key_name = :key_name and opt_code = :opt_code and opt_sts = '01' ")
    public String getOptName(@Param("key_name") String key_name,@Param("opt_code") String opt_code);

}

