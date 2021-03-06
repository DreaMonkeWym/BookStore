package com.wym.mapper;

import com.wym.model.BookType;
import com.wym.model.BookTypeExample;
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

public interface BookTypeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=BookTypeSqlProvider.class, method="countByExample")
    long countByExample(BookTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @DeleteProvider(type=BookTypeSqlProvider.class, method="deleteByExample")
    int deleteByExample(BookTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Delete({
        "delete from booktype",
        "where typeid = #{typeid,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String typeid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Insert({
        "insert into booktype (typeid, typename)",
        "values (#{typeid,jdbcType=VARCHAR}, #{typename,jdbcType=VARCHAR})"
    })
    int insert(BookType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @InsertProvider(type=BookTypeSqlProvider.class, method="insertSelective")
    int insertSelective(BookType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=BookTypeSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="typename", property="typename", jdbcType=JdbcType.VARCHAR)
    })
    List<BookType> selectByExample(BookTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Select({
        "select",
        "typeid, typename",
        "from booktype",
        "where typeid = #{typeid,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="typename", property="typename", jdbcType=JdbcType.VARCHAR)
    })
    BookType selectByPrimaryKey(String typeid);

    @Select({
            "select",
            "typeid",
            "from booktype",
            "where typename = #{typename,jdbcType=VARCHAR}"
    })
    @Results({
            @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR, id=true)
    })
    BookType selectByPrimaryName(String typeName);

    @Select({
            "select",
            "typeid, typename",
            "from booktype"
    })
    @Results({
            @Result(column="typeid", property="typeid", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="typename", property="typename", jdbcType=JdbcType.VARCHAR)
    })
    List<BookType> queryBookType();
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=BookTypeSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") BookType record, @Param("example") BookTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=BookTypeSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") BookType record, @Param("example") BookTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=BookTypeSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(BookType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table booktype
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Update({
        "update booktype",
        "set typename = #{typename,jdbcType=VARCHAR}",
        "where typeid = #{typeid,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(BookType record);
}