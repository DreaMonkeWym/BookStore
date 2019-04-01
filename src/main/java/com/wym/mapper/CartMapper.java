package com.wym.mapper;

import com.wym.model.Cart;
import com.wym.model.CartExample;
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

public interface CartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=CartSqlProvider.class, method="countByExample")
    long countByExample(CartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @DeleteProvider(type=CartSqlProvider.class, method="deleteByExample")
    int deleteByExample(CartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Delete({
        "delete from cart",
        "where cartid = #{cartid,jdbcType=VARCHAR}"
    })
    int deleteByPrimaryKey(String cartid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Insert({
        "insert into cart (cartid, username, ",
        "bookid, quantity, ",
        "payment)",
        "values (#{cartid,jdbcType=VARCHAR}, #{username,jdbcType=VARCHAR}, ",
        "#{bookid,jdbcType=VARCHAR}, #{quantity,jdbcType=VARCHAR}, ",
        "#{payment,jdbcType=BIT})"
    })
    int insert(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @InsertProvider(type=CartSqlProvider.class, method="insertSelective")
    int insertSelective(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @SelectProvider(type=CartSqlProvider.class, method="selectByExample")
    @Results({
        @Result(column="cartid", property="cartid", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="bookid", property="bookid", jdbcType=JdbcType.VARCHAR),
        @Result(column="quantity", property="quantity", jdbcType=JdbcType.VARCHAR),
        @Result(column="payment", property="payment", jdbcType=JdbcType.BIT)
    })
    List<Cart> selectByExample(CartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Select({
        "select",
        "cartid, username, bookid, quantity, payment",
        "from cart",
        "where cartid = #{cartid,jdbcType=VARCHAR}"
    })
    @Results({
        @Result(column="cartid", property="cartid", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
        @Result(column="bookid", property="bookid", jdbcType=JdbcType.VARCHAR),
        @Result(column="quantity", property="quantity", jdbcType=JdbcType.VARCHAR),
        @Result(column="payment", property="payment", jdbcType=JdbcType.BIT)
    })
    Cart selectByPrimaryKey(String cartid);

    @Select({
            "select",
            "cartid, username, bookid, quantity, payment",
            "from cart",
            "where username = #{username,jdbcType=VARCHAR} and bookid = #{bookid,jdbcType=VARCHAR} and payment = #{payment,jdbcType=BIT}"
    })
    @Results({
            @Result(column="cartid", property="cartid", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
            @Result(column="bookid", property="bookid", jdbcType=JdbcType.VARCHAR),
            @Result(column="quantity", property="quantity", jdbcType=JdbcType.VARCHAR),
            @Result(column="payment", property="payment", jdbcType=JdbcType.BIT)
    })
    Cart selectBookExist(String username, String bookid, Boolean payment);

    @Select({
            "select",
            "cartid, username, bookid, quantity",
            "from cart",
            "where username = #{username,jdbcType=VARCHAR} and payment = #{payment,jdbcType=BIT} ORDER BY cartid desc"
    })
    @Results({
            @Result(column="cartid", property="cartid", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="username", property="username", jdbcType=JdbcType.VARCHAR),
            @Result(column="bookid", property="bookid", jdbcType=JdbcType.VARCHAR),
            @Result(column="quantity", property="quantity", jdbcType=JdbcType.VARCHAR)
    })
    List<Cart> queryCart(String username, Boolean payment);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=CartSqlProvider.class, method="updateByExampleSelective")
    int updateByExampleSelective(@Param("record") Cart record, @Param("example") CartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=CartSqlProvider.class, method="updateByExample")
    int updateByExample(@Param("record") Cart record, @Param("example") CartExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @UpdateProvider(type=CartSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    @Update({
        "update cart",
        "set username = #{username,jdbcType=VARCHAR},",
          "bookid = #{bookid,jdbcType=VARCHAR},",
          "quantity = #{quantity,jdbcType=VARCHAR},",
          "payment = #{payment,jdbcType=BIT}",
        "where cartid = #{cartid,jdbcType=VARCHAR}"
    })
    int updateByPrimaryKey(Cart record);

    @Update({
            "update cart",
            "set quantity = #{quantity,jdbcType=VARCHAR}",
            "where cartid = #{cartid,jdbcType=VARCHAR}"
    })
    int updateQuantity(String cartid, String quantity);

    @Update({
            "update cart",
            "set payment = #{payment,jdbcType=BIT}",
            "where cartid = #{cartid,jdbcType=VARCHAR}"
    })
    int updatePayment(String cartid, Boolean payment);
}