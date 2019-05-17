package com.wym.service;

import com.wym.model.BookType;
import com.wym.model.po.AdminQuery;
import com.wym.model.po.BookByType;
import com.wym.model.po.BookRecommend;
import com.wym.model.po.ResBookDetail;
import com.wym.utils.ApiResult;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by wym on 2019-03-22 18:00
 */
public interface BookService {

    /**
     * 查看书籍详情
     * @param bookId
     * @return
     */
    Mono<ApiResult<ResBookDetail>> queryBookDetail(String bookId, String username);

    /**
     *  添加书籍
     * @param typeName
     * @param descri
     * @param price
     * @param quantity
     * @param publicationTime
     * @param author
     * @param file
     * @return
     */
    Mono<ApiResult<Object>> addBookDetail(String typeName,String bookName, String descri, String isbn, String price, String quantity, String publicationTime, String author, MultipartFile file);

    /**
     * 查询图书种类
     * @return
     */
    Mono<ApiResult<? extends List<BookType>>> queryBookType();

    /**
     * 根据类型查找书籍
     * @param typeId
     * @return
     */
    Mono<ApiResult<? extends List<BookByType>>> queryBookByType(String typeId);

    /**
     * 根据书名查找书籍
     * @param bookName
     * @return
     */
    Mono<ApiResult<? extends List<BookByType>>> queryBookByName(String bookName);

    /**
     * 书籍评价
     * @param bookId
     * @param content
     * @param username
     * @return
     */
    Mono<ApiResult<Object>> evaluateBook(String bookId, String content, String username);

    /**
     * 已售榜单查询
     * @return
     */
    Mono<ApiResult<? extends List<BookRecommend>>> queryOrderSold();

    /**
     * 根据用户爱好查询
     * @param username
     * @return
     */
    Mono<ApiResult<? extends List<BookRecommend>>> queryByFavor(String username);

    /**
     * 删除书籍
     * @param bookId
     * @return
     */
    Mono<ApiResult<Object>> delBook(String bookId);

    /**
     * 修改书籍价格、数量
     * @param bookId
     * @param price
     * @param quantity
     * @return
     */
    Mono<ApiResult<Object>> updateBook(String bookId, String price, String quantity);

    /**
     * 管理员管理书籍的查询页面
     * @return
     */
    Mono<ApiResult<? extends List<AdminQuery>>> adminManageBook();

}
