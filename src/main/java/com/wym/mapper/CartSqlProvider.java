package com.wym.mapper;

import com.wym.model.Cart;
import com.wym.model.CartExample.Criteria;
import com.wym.model.CartExample.Criterion;
import com.wym.model.CartExample;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.jdbc.SQL;

public class CartSqlProvider {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String countByExample(CartExample example) {
        SQL sql = new SQL();
        sql.SELECT("count(*)").FROM("cart");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String deleteByExample(CartExample example) {
        SQL sql = new SQL();
        sql.DELETE_FROM("cart");
        applyWhere(sql, example, false);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String insertSelective(Cart record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("cart");
        
        if (record.getCartid() != null) {
            sql.VALUES("cartid", "#{cartid,jdbcType=VARCHAR}");
        }
        
        if (record.getUsername() != null) {
            sql.VALUES("username", "#{username,jdbcType=VARCHAR}");
        }
        
        if (record.getBookid() != null) {
            sql.VALUES("bookid", "#{bookid,jdbcType=VARCHAR}");
        }
        
        if (record.getQuantity() != null) {
            sql.VALUES("quantity", "#{quantity,jdbcType=VARCHAR}");
        }
        
        if (record.getPayment() != null) {
            sql.VALUES("payment", "#{payment,jdbcType=BIT}");
        }
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String selectByExample(CartExample example) {
        SQL sql = new SQL();
        if (example != null && example.isDistinct()) {
            sql.SELECT_DISTINCT("cartid");
        } else {
            sql.SELECT("cartid");
        }
        sql.SELECT("username");
        sql.SELECT("bookid");
        sql.SELECT("quantity");
        sql.SELECT("payment");
        sql.FROM("cart");
        applyWhere(sql, example, false);
        
        if (example != null && example.getOrderByClause() != null) {
            sql.ORDER_BY(example.getOrderByClause());
        }
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String updateByExampleSelective(Map<String, Object> parameter) {
        Cart record = (Cart) parameter.get("record");
        CartExample example = (CartExample) parameter.get("example");
        
        SQL sql = new SQL();
        sql.UPDATE("cart");
        
        if (record.getCartid() != null) {
            sql.SET("cartid = #{record.cartid,jdbcType=VARCHAR}");
        }
        
        if (record.getUsername() != null) {
            sql.SET("username = #{record.username,jdbcType=VARCHAR}");
        }
        
        if (record.getBookid() != null) {
            sql.SET("bookid = #{record.bookid,jdbcType=VARCHAR}");
        }
        
        if (record.getQuantity() != null) {
            sql.SET("quantity = #{record.quantity,jdbcType=VARCHAR}");
        }
        
        if (record.getPayment() != null) {
            sql.SET("payment = #{record.payment,jdbcType=BIT}");
        }
        
        applyWhere(sql, example, true);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String updateByExample(Map<String, Object> parameter) {
        SQL sql = new SQL();
        sql.UPDATE("cart");
        
        sql.SET("cartid = #{record.cartid,jdbcType=VARCHAR}");
        sql.SET("username = #{record.username,jdbcType=VARCHAR}");
        sql.SET("bookid = #{record.bookid,jdbcType=VARCHAR}");
        sql.SET("quantity = #{record.quantity,jdbcType=VARCHAR}");
        sql.SET("payment = #{record.payment,jdbcType=BIT}");
        
        CartExample example = (CartExample) parameter.get("example");
        applyWhere(sql, example, true);
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    public String updateByPrimaryKeySelective(Cart record) {
        SQL sql = new SQL();
        sql.UPDATE("cart");
        
        if (record.getUsername() != null) {
            sql.SET("username = #{username,jdbcType=VARCHAR}");
        }
        
        if (record.getBookid() != null) {
            sql.SET("bookid = #{bookid,jdbcType=VARCHAR}");
        }
        
        if (record.getQuantity() != null) {
            sql.SET("quantity = #{quantity,jdbcType=VARCHAR}");
        }
        
        if (record.getPayment() != null) {
            sql.SET("payment = #{payment,jdbcType=BIT}");
        }
        
        sql.WHERE("cartid = #{cartid,jdbcType=VARCHAR}");
        
        return sql.toString();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated Tue Mar 19 12:15:33 CST 2019
     */
    protected void applyWhere(SQL sql, CartExample example, boolean includeExamplePhrase) {
        if (example == null) {
            return;
        }
        
        String parmPhrase1;
        String parmPhrase1_th;
        String parmPhrase2;
        String parmPhrase2_th;
        String parmPhrase3;
        String parmPhrase3_th;
        if (includeExamplePhrase) {
            parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        } else {
            parmPhrase1 = "%s #{oredCriteria[%d].allCriteria[%d].value}";
            parmPhrase1_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
            parmPhrase2 = "%s #{oredCriteria[%d].allCriteria[%d].value} and #{oredCriteria[%d].criteria[%d].secondValue}";
            parmPhrase2_th = "%s #{oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
            parmPhrase3 = "#{oredCriteria[%d].allCriteria[%d].value[%d]}";
            parmPhrase3_th = "#{oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
        }
        
        StringBuilder sb = new StringBuilder();
        List<Criteria> oredCriteria = example.getOredCriteria();
        boolean firstCriteria = true;
        for (int i = 0; i < oredCriteria.size(); i++) {
            Criteria criteria = oredCriteria.get(i);
            if (criteria.isValid()) {
                if (firstCriteria) {
                    firstCriteria = false;
                } else {
                    sb.append(" or ");
                }
                
                sb.append('(');
                List<Criterion> criterions = criteria.getAllCriteria();
                boolean firstCriterion = true;
                for (int j = 0; j < criterions.size(); j++) {
                    Criterion criterion = criterions.get(j);
                    if (firstCriterion) {
                        firstCriterion = false;
                    } else {
                        sb.append(" and ");
                    }
                    
                    if (criterion.isNoValue()) {
                        sb.append(criterion.getCondition());
                    } else if (criterion.isSingleValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase1, criterion.getCondition(), i, j));
                        } else {
                            sb.append(String.format(parmPhrase1_th, criterion.getCondition(), i, j,criterion.getTypeHandler()));
                        }
                    } else if (criterion.isBetweenValue()) {
                        if (criterion.getTypeHandler() == null) {
                            sb.append(String.format(parmPhrase2, criterion.getCondition(), i, j, i, j));
                        } else {
                            sb.append(String.format(parmPhrase2_th, criterion.getCondition(), i, j, criterion.getTypeHandler(), i, j, criterion.getTypeHandler()));
                        }
                    } else if (criterion.isListValue()) {
                        sb.append(criterion.getCondition());
                        sb.append(" (");
                        List<?> listItems = (List<?>) criterion.getValue();
                        boolean comma = false;
                        for (int k = 0; k < listItems.size(); k++) {
                            if (comma) {
                                sb.append(", ");
                            } else {
                                comma = true;
                            }
                            if (criterion.getTypeHandler() == null) {
                                sb.append(String.format(parmPhrase3, i, j, k));
                            } else {
                                sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getTypeHandler()));
                            }
                        }
                        sb.append(')');
                    }
                }
                sb.append(')');
            }
        }
        
        if (sb.length() > 0) {
            sql.WHERE(sb.toString());
        }
    }
}