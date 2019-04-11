package com.wym.mapper;

import com.wym.model.RecentView;
import com.wym.model.RecentViewExample;
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

public interface RecentViewMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=RecentViewSqlProvider.class, method="countByExample")
    long countByExample(RecentViewExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @DeleteProvider(type=RecentViewSqlProvider.class, method="deleteByExample")
    int deleteByExample(RecentViewExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Delete({
        "delete from recentview",
        "where username = #{username,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String username);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Insert({
        "insert into recentview (username, typeid)",
        "values (#{username,jdbcType=VARCHAR}, #{typeid,jdbcType=VARCHAR})"
    })
    int insert(RecentView record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @InsertProvider(type=RecentViewSqlProvider.class, method="insertSelective")
    int insertSelective(RecentView record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=RecentViewSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR)
    })
    List<RecentView> selectByExample(RecentViewExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Select({
        "select",
        "username",
        "from recentview",
        "where username = #{username,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR, id=true),
//        @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR)
    })
    String selectByPrimaryKey(String username);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=RecentViewSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") RecentView record, @Param("example") RecentViewExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=RecentViewSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") RecentView record, @Param("example") RecentViewExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=RecentViewSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(RecentView record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recentview
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Update({
        "update recentview",
        "set typeid = #{typeid,jdbcType=VARCHAR}",
        "where username = #{username,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(String username, String typeid);
}