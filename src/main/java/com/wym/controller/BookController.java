package com.wym.controller;

import com.wym.model.BookType;
import com.wym.model.po.AdminQuery;
import com.wym.model.po.BookByType;
import com.wym.model.po.BookRecommend;
import com.wym.model.po.ResBookDetail;
import com.wym.service.BookService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wym on 2019-03-22 18:00
 */
@RestController
@CrossOrigin
@RequestMapping("/book")
@Slf4j
public class BookController {

    @Resource
    private BookService bookService;

    /**
     * 添加图书
     * @param typeName
     * @param bookName
     * @param descri
     * @param isbn
     * @param price
     * @param quantity
     * @param publicationTime
     * @param author
     * @param file
     * @return
     */
    @PostMapping("/addBook")
    public Mono<ApiResult<Object>> addBookDetail(@RequestParam String typeName,
                                                 @RequestParam String bookName,
                                                 @RequestParam String descri,
                                                 @RequestParam String isbn,
                                                 @RequestParam String price,
                                                 @RequestParam String quantity,
                                                 @RequestParam String publicationTime,
                                                 @RequestParam String author,
                                                 @RequestParam MultipartFile file) {
        log.info("addBookDetail ~~ typeName = {}, bookName = {}, descri = {}, isbn = {}, price = {}, quantity = {}, publicationTime = {}, author = {}",
                                                                   typeName, bookName, descri, isbn, price, quantity, publicationTime, author);
        return bookService.addBookDetail(typeName, bookName, descri, isbn, price, quantity, publicationTime, author, file);
    }

    /**
     * 查询图书种类
     * @return
     */
    @GetMapping("/querybooktype")
    public Mono<ApiResult<? extends List<BookType>>> queryBookType(){
        log.info("queryBookType ~~");
        return bookService.queryBookType();
    }

    /**
     * 根据类型查找书籍
     * @param typeId
     * @return
     */
    @GetMapping("/querybookbytype")
    public Mono<ApiResult<? extends List<BookByType>>> queryBookByType(@RequestParam String typeId){
        log.info("queryBookByType ~~ typeId = {}", typeId);
        return bookService.queryBookByType(typeId);
    }

    /**
     * 查看书籍详情
     * @param bookId
     * @return
     */
    @PostMapping("/querybookdetail")
    public Mono<ApiResult<ResBookDetail>> queryBookDetail(@RequestParam String bookId){
        log.info("queryBookDetail ~~ bookId = {}", bookId);
        return bookService.queryBookDetail(bookId);
    }

    /**
     * 书籍评价
     * @param bookId
     * @param content
     * @param username
     * @return
     */
    @PostMapping("/evaluatebook")
    public Mono<ApiResult<Object>> evaluateBook(@RequestParam String bookId,
                                                @RequestParam String content,
                                                @RequestParam String username){
        log.info("evaluateBook ~~ bookId = {}, content = {}, username = {}", bookId, content, username);
        return bookService.evaluateBook(bookId, content, username);
    }

    /**
     * 根据书名查找书籍
     * @param bookName
     * @return
     */
    @PostMapping("/querybookbyname")
    public Mono<ApiResult<? extends List<BookByType>>> queryBookByName(@RequestParam String bookName){
        log.info("queryBookByName ~~ bookName = {}", bookName);
        return bookService.queryBookByName(bookName);
    }

    /**
     * 已售榜单查询
     * @return
     */
    @GetMapping("/queryordersold")
    public Mono<ApiResult<? extends List<BookRecommend>>> queryOrderSold(){
        log.info("queryOrderSold ~~ ");
        return bookService.queryOrderSold();
    }

    /**
     * 根据用户爱好查询
     * @param username
     * @return
     */
    @PostMapping("/querybyfavor")
    public Mono<ApiResult<? extends List<BookRecommend>>> queryByFavor(@RequestParam(required = false)  String username) {
        log.info(" queryByFavor ~~ username = {}", username);
        return bookService.queryByFavor(username);
    }

    /**
     * 删除书籍
     * @param bookId
     * @return
     */
    @DeleteMapping("/delbook")
    public Mono<ApiResult<Object>> delBook(@RequestParam String bookId){
        log.info(" delBook ~~ bookId = {}", bookId);
        return bookService.delBook(bookId);
    }

    /**
     * 修改书籍价格、数量
     * @param bookId
     * @param price
     * @param quantity
     * @return
     */
    @PutMapping("/updatebook")
    public Mono<ApiResult<Object>> updateBook(@RequestParam String bookId,
                                              @RequestParam String price,
                                              @RequestParam String quantity){
        log.info(" updateBook ~~ bookId = {}, price = {}, quantity = {}", bookId, price, quantity);
        return bookService.updateBook(bookId, price, quantity);
    }

    /**
     * 管理员管理书籍的查询页面
     * @return
     */
    @GetMapping("/adminmangebook")
    public Mono<ApiResult<? extends List<AdminQuery>>> adminMangeBook(){
        log.info("adminMangeBook ~~");
        return bookService.adminManageBook();
    }
}
