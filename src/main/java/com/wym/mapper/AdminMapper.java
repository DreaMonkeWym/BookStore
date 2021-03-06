package com.wym.mapper;

import com.wym.model.Admin;
import com.wym.model.AdminExample;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;

public interface AdminMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=AdminSqlProvider.class, method="countByExample")
    long countByExample(AdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @DeleteProvider(type=AdminSqlProvider.class, method="deleteByExample")
    int deleteByExample(AdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Delete({
        "delete from admin",
        "where adminname = #{adminname,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String adminname);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Insert({
        "insert into admin (adminname, adminpassword, ",
        "avatar)",
        "values (#{adminname,jdbcType=VARCHAR}, #{adminpassword,jdbcType=VARCHAR}, ",
        "#{avatar,jdbcType=VARCHAR})"
    })
    int insert(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @InsertProvider(type=AdminSqlProvider.class, method="insertSelective")
    int insertSelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=AdminSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="adminname", property="adminname", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="adminpassword", property="adminpassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="avatar", property="avatar", jdbcType=JdbcType.VARCHAR)
    })
    List<Admin> selectByExample(AdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Select({
        "select",
        "adminname, adminpassword, avatar",
        "from admin",
        "where adminname = #{adminname,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="adminname", property="adminname", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="adminpassword", property="adminpassword", jdbcType=JdbcType.VARCHAR),
        @Result(column="avatar", property="avatar", jdbcType=JdbcType.VARCHAR)
    })
    Admin selectByPrimaryKey(String adminname);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=AdminSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") Admin record, @Param("example") AdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=AdminSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") Admin record, @Param("example") AdminExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=AdminSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Update({
        "update admin",
        "set adminpassword = #{adminpassword,jdbcType=VARCHAR},",
          "avatar = #{avatar,jdbcType=VARCHAR}",
        "where adminname = #{adminname,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(Admin record);
}