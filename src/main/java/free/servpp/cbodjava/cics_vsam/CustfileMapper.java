package free.servpp.cbodjava.cics_vsam;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CustfileMapper {

    @Insert("""
            INSERT INTO CUSTFILE (CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_PHONE, CUSTOMER_ADDRESS)
            VALUES (#{customerId}, #{customerName}, #{customerPhone}, #{customerAddress})
            """)
    int insert(CustfileRow row);

    @Select("""
            SELECT CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_PHONE, CUSTOMER_ADDRESS
            FROM CUSTFILE
            WHERE CUSTOMER_ID = #{customerId}
            """)
    @Results(id = "custfileRowMap", value = {
            @Result(property = "customerId", column = "CUSTOMER_ID"),
            @Result(property = "customerName", column = "CUSTOMER_NAME"),
            @Result(property = "customerPhone", column = "CUSTOMER_PHONE"),
            @Result(property = "customerAddress", column = "CUSTOMER_ADDRESS")
    })
    CustfileRow selectById(@Param("customerId") String customerId);

    @Update("""
            UPDATE CUSTFILE
            SET CUSTOMER_NAME = #{customerName},
                CUSTOMER_PHONE = #{customerPhone},
                CUSTOMER_ADDRESS = #{customerAddress}
            WHERE CUSTOMER_ID = #{customerId}
            """)
    int update(CustfileRow row);

    @Delete("""
            DELETE FROM CUSTFILE
            WHERE CUSTOMER_ID = #{customerId}
            """)
    int deleteById(@Param("customerId") String customerId);
}
