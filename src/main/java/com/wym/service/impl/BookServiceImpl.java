package com.wym.service.impl;

import com.wym.mapper.*;
import com.wym.model.*;
import com.wym.model.po.*;
import com.wym.service.BookService;
import com.wym.utils.ApiResult;
import com.wym.utils.RedisConfig;
import com.wym.utils.StaticConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wym on 2019-03-22 18:12
 */
@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Resource
    private BookTypeMapper bookTypeMapper;
    @Resource
    private BookDetailMapper bookDetailMapper;
    @Resource
    private StaticConfig staticConfig;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    private Mono<BookDetail> selectBookDetail(String bookId){
        return Mono.fromSupplier(() -> {
            if (bookDetailMapper.selectByPrimaryKey(bookId) != null){
                return bookDetailMapper.selectByPrimaryKey(bookId);
            }
            return new BookDetail();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectBookDetail is error!~~,bookId == {}", bookId, t))
                .onErrorReturn(new BookDetail());
    }

    private Mono<List<Comment>> selectByBookId(String bookId){
        return Mono.fromSupplier(() -> {
            if (commentMapper.selectByBookId(bookId) != null){
                return commentMapper.selectByBookId(bookId);
            }
            return new ArrayList<Comment>();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectByBookId is error!~~,bookId == {}", bookId, t))
                .onErrorReturn(new ArrayList<>());
    }

    private Mono<BookDetail> selectBookISBN(String isbn){
        return Mono.fromSupplier(() -> {
            if (bookDetailMapper.selectBookISBN(isbn) != null){
                return bookDetailMapper.selectBookISBN(isbn);
            }
            return new BookDetail();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectBookISBN is error!~~,isbn == {}", isbn, t))
                .onErrorReturn(new BookDetail());
    }

    @Override
    public Mono addBookDetail(String typeName, String bookName, String descri, String isbn, String price,
                                                 String quantity, String publicationTime, String author, MultipartFile file) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            String bookId = String.valueOf(System.currentTimeMillis());
            Mono<BookDetail> bookDetailMono = selectBookISBN(isbn);
            BookType bookType = bookTypeMapper.selectByPrimaryName(typeName);
            return bookDetailMono.flatMap(bookDetail -> {
                if(StringUtils.isEmpty(bookDetail.getBookid())){
                    String fileName = "";
                    String filePath = staticConfig.getFilePath();
                    if(!file.isEmpty()){
                        fileName = file.getOriginalFilename();
                        String suffixName = fileName.substring(fileName.lastIndexOf("."));
                        fileName = bookId + suffixName;
                    }
                    BookDetail bookDetails = new BookDetail();
                    bookDetails.setBookid(bookId);
                    bookDetails.setTypeid(bookType.getTypeid());
                    bookDetails.setBookname(bookName);
                    bookDetails.setDescri(descri);
                    bookDetails.setIsbn(isbn);
                    bookDetails.setPrice(price);
                    bookDetails.setQuantity(quantity);
                    bookDetails.setPublicationtime(publicationTime);
                    bookDetails.setAuthor(author);
                    bookDetails.setAvatar(fileName);
                    bookDetails.setGlance("0");
                    bookDetails.setSoldout("0");
                    if (bookDetailMapper.insert(bookDetails) > 0){
                        try {
                            file.transferTo(new File(filePath + File.separator + fileName));
                        } catch (IOException e) {
                            log.info("图片上传失败！ ~~", e);
                        }
                        bookByTypeList(bookType.getTypeid());
                        valueOperations.getOperations().delete("queryByFavorNull");
                        return Mono.just(ApiResult.getApiResult(200, "add book success"));
                    }
                }
                return Mono.just(ApiResult.getApiResult(-1, "add book fail, maybe it already exists."));
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("addBookDetail is error!~~,bookId == {}", bookId, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "add book fail"));
        });
    }

    @Override
    public Mono<ApiResult<? extends List<BookType>>> queryBookType() {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, List<BookType>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey("queryBookType")) {
                List<BookType> bookTypeList = valueOperations.get("queryBookType");
                return ApiResult.getApiResult(bookTypeList);
            }
            List<BookType> bookTypeList = bookTypeMapper.queryBookType();
            if (!CollectionUtils.isEmpty(bookTypeList) && bookTypeList.size() > 0){
                valueOperations.set("queryBookType", bookTypeList);
                return ApiResult.getApiResult(bookTypeList);
            }
            return ApiResult.getApiResult(new ArrayList<BookType>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryBookType is error!~~", t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    @Override
    public Mono<ApiResult<? extends List<BookByType>>> queryBookByType(String typeId) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, BookByType> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            if (redisConfig.getRedisTemplate().hasKey(typeId)) {
                List<BookByType> bookByTypeList = hashOperations.values(typeId);
                return ApiResult.getApiResult(bookByTypeList);
            }
            List<BookByType> bookByTypeList = bookByTypeList(typeId);
            if (!bookByTypeList.isEmpty()) {
                return ApiResult.getApiResult(bookByTypeList);
            }
            return ApiResult.getApiResult(new ArrayList<BookByType>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryBookByType is error!~~ typeId = {}", typeId, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    private List<BookByType> bookByTypeList(String typeId) {
        RedisConfig redisConfig = new RedisConfig(redisTemplate);
        HashOperations<String, String, BookByType> hashOperations = redisConfig.getRedisTemplate().opsForHash();
        List<BookDetail> bookDetailList = bookDetailMapper.queryBookByType(typeId);
        List<BookByType> bookByTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bookDetailList) && bookDetailList.size() > 0){
            bookDetailList.forEach(bookDetail -> {
                BookByType bookByType = setBookBytype(bookDetail);
                hashOperations.put(typeId, bookDetail.getBookid(), bookByType);
                bookByTypeList.add(bookByType);
            });
        }
        return bookByTypeList;
    }

    private BookByType setBookBytype(BookDetail bookDetail) {
        BookByType bookByType = new BookByType();
        bookByType.setBookid(bookDetail.getBookid());
        bookByType.setAuthor(bookDetail.getAuthor());
        bookByType.setAvatar(bookDetail.getAvatar());
        bookByType.setBookname(bookDetail.getBookname());
        bookByType.setPrice(bookDetail.getPrice());
        bookByType.setGlance(bookDetail.getGlance());
        bookByType.setPublicationtime(bookDetail.getPublicationtime());
        return bookByType;
    }

    @Override
    public Mono evaluateBook(String bookId, String content, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, ResBookDetail> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            Comment comment = new Comment();
            comment.setCommentid(System.currentTimeMillis() + username);
            comment.setBookid(bookId);
            comment.setContent(content);
            comment.setUsername(username);
            if (commentMapper.insert(comment) > 0){
                if (redisConfig.getRedisTemplate().hasKey(bookId)) {
                    ResBookDetail resBookDetail = valueOperations.get(bookId);
                    ResComment resComment = new ResComment();
                    User user = userMapper.selectByPrimaryKey(username);
                    user.setPassword("**********");
                    resComment.setContent(comment.getContent());
                    resComment.setUser(user);
                    resBookDetail.getCommentList().add(resComment);
                    valueOperations.set(bookId, resBookDetail);
                }
                return ApiResult.getApiResult(200, "evalate book successfully");
            }
            return ApiResult.getApiResult(-1, "evalate book failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("evaluateBook is error!~~ bookId = {}, content = {}, username = {}", bookId, content, username, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "evalate book failly"));
    }

    @Override
    public Mono<ApiResult<? extends List<BookRecommend>>> queryOrderSold() {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey("queryOrderSold")) {
                List<BookRecommend> bookRecommendList = valueOperations.get("queryOrderSold");
                return ApiResult.getApiResult(bookRecommendList);
            }
            List<BookRecommend> bookRecommendList = new ArrayList<>();
            BookRecommendList(bookRecommendList);
            if (bookRecommendList.size() > 0) {
                return ApiResult.getApiResult(bookRecommendList);
            }
            return ApiResult.getApiResult(new ArrayList<BookRecommend>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryOrderSold is error!~~", t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    private void BookRecommendList(List<BookRecommend> bookRecommendList) {
        RedisConfig redisConfig = new RedisConfig(redisTemplate);
        ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
        List<BookDetail> bookDetailList = bookDetailMapper.queryOrderSold();
        if (!CollectionUtils.isEmpty(bookDetailList) && bookDetailList.size() > 0){
            bookDetailList.forEach(bookDetail -> {
                BookRecommend bookRecommend = setBookRecommend(bookDetail);
                bookRecommendList.add(bookRecommend);
            });
            valueOperations.set("queryOrderSold", bookRecommendList);
        }
    }

    private BookRecommend setBookRecommend(BookDetail bookDetail){
        BookRecommend bookRecommend = new BookRecommend();
        bookRecommend.setBookid(bookDetail.getBookid());
        bookRecommend.setAvatar(bookDetail.getAvatar());
        bookRecommend.setBookname(bookDetail.getBookname());
        return bookRecommend;
    }

    @Override
    public Mono queryBookDetail(String bookId, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, ResBookDetail> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey(bookId)) {
                ResBookDetail resBookDetail = valueOperations.get(bookId);
                String glance = new BigInteger(resBookDetail.getGlance()).add(new BigInteger("1")).toString();
                if (bookDetailMapper.updateGlance(bookId, glance) > 0){
                    resBookDetail.setGlance(glance);
                    valueOperations.set(bookId, resBookDetail);
                    bookByTypeList(resBookDetail.getTypeId());
                }
                setRecentView(resBookDetail.getTypeId(), username);
                return ApiResult.getApiResult(resBookDetail);
            }
            Mono<BookDetail> bookDetailMono = selectBookDetail(bookId);
            Mono<List<Comment>> commentMono = selectByBookId(bookId);
            return Mono.zip(bookDetailMono, commentMono).map(tuple -> {
                ResBookDetail resBookDetail = new ResBookDetail();
                List<ResComment> resCommentList = new ArrayList<>();
                if (!tuple.getT2().isEmpty() && tuple.getT2().size() > 0){
                    tuple.getT2().forEach(comment -> {
                        ResComment resComment = new ResComment();
                        User user = userMapper.selectByPrimaryKey(comment.getUsername());
                        user.setPassword("**********");
                        resComment.setContent(comment.getContent());
                        resComment.setUser(user);
                        resCommentList.add(resComment);
                    });
                }
                if (!StringUtils.isEmpty(tuple.getT1().getBookid())){
                    resBookDetail.setAuthor(tuple.getT1().getAuthor());
                    resBookDetail.setAvatar(tuple.getT1().getAvatar());
                    resBookDetail.setBookname(tuple.getT1().getBookname());
                    resBookDetail.setDescri(tuple.getT1().getDescri());
                    resBookDetail.setPrice(tuple.getT1().getPrice());
                    resBookDetail.setQuantity(tuple.getT1().getQuantity());
                    resBookDetail.setPublicationtime(tuple.getT1().getPublicationtime());
                    resBookDetail.setTypeId(tuple.getT1().getTypeid());
                    String glance = new BigInteger(tuple.getT1().getGlance()).add(new BigInteger("1")).toString();
                    if (bookDetailMapper.updateGlance(bookId, glance) > 0){
                        resBookDetail.setGlance(glance);
                    }else {
                        resBookDetail.setGlance(tuple.getT1().getGlance());
                    }
                    resBookDetail.setCommentList(resCommentList);
                }
                setRecentView(tuple.getT1().getTypeid(), username);
                bookByTypeList(tuple.getT1().getTypeid());
                valueOperations.set(bookId, resBookDetail);
                return ApiResult.getApiResult(resBookDetail);
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("queryBookDetail zip is error!~~ bookId = {} ", bookId, t))
                    .onErrorReturn(ApiResult.getApiResult(new ResBookDetail()));
        });
    }

    private void setRecentView(String typeId, String username) {
        RedisConfig redisConfig = new RedisConfig(redisTemplate);
        ValueOperations<String, String> value = redisConfig.getRedisTemplate().opsForValue();
        if (!"".equals(username) && username != null) {
            value.set("recentView" + username, typeId);
        }
    }

    private List<BookByType> bookByNameList(String bookName) {
        List<BookDetail> bookDetailList = bookDetailMapper.queryBookByName(bookName);
        List<BookByType> bookByTypeList = new ArrayList<>();
        if (!bookDetailList.isEmpty() && bookDetailList.size() > 0) {
            bookDetailList.forEach(bookDetail -> {
                BookByType bookByType = setBookBytype(bookDetail);
                bookByTypeList.add(bookByType);
            });
        }
        return bookByTypeList;
    }
    @Override
    public Mono<ApiResult<? extends List<BookByType>>> queryBookByName(String bookName) {
        return Mono.fromSupplier(() -> {
            List<BookByType> bookByTypeList = bookByNameList(bookName);
            if (!bookByTypeList.isEmpty()) {
                return ApiResult.getApiResult(bookByTypeList);
            }
            return ApiResult.getApiResult("Do not find this book", new ArrayList<BookByType>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryBookByName is error!~~  bookName = {}",bookName, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    @Override
    public Mono<ApiResult<? extends List<BookRecommend>>> queryByFavor(String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            ValueOperations<String, User> userValueOperations = redisConfig.getRedisTemplate().opsForValue();
            HashOperations<String, String, BookByType> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            if (username == null || "".equals(username)){
                if (redisConfig.getRedisTemplate().hasKey("queryByFavorNull")) {
                    List<BookRecommend> bookRecommendList = valueOperations.get("queryByFavorNull");
                    userValueOperations.getOperations().delete("recentUser");
                    return ApiResult.getApiResult(bookRecommendList);
                }
                List<BookDetail> bookDetailList = bookDetailMapper.queryBookByType(staticConfig.getTypeId());
                if (!bookDetailList.isEmpty() && bookDetailList.size() > 0){
                    if (bookDetailList.size() > 10){
                        bookDetailList = bookDetailList.subList(0, 10);
                    }
                    List<BookRecommend> bookRecommendList = new ArrayList<>();
                    bookDetailList.forEach(bookDetail -> {
                        BookRecommend bookRecommend = setBookRecommend(bookDetail);
                        bookRecommendList.add(bookRecommend);
                    });
                    valueOperations.set("queryByFavorNull", bookRecommendList);
                    return ApiResult.getApiResult(bookRecommendList);
                }
                return ApiResult.getApiResult(new ArrayList<BookRecommend>());
            } else {
                User user = userMapper.selectByPrimaryKey(username);
                String typeId;
                if (redisConfig.getRedisTemplate().hasKey("recentView" + user.getUsername())) {
                    ValueOperations<String, String> value = redisConfig.getRedisTemplate().opsForValue();
                    typeId = value.get("recentView" + user.getUsername());
                } else {
                    typeId = bookTypeMapper.selectByPrimaryName(user.getFavor()).getTypeid();
                }
                if (redisConfig.getRedisTemplate().hasKey(typeId)) {
                    List<BookByType> bookByTypeList = hashOperations.values(typeId);
                    if (bookByTypeList.size() > 8){
                        bookByTypeList = bookByTypeList.subList(0, 8);
                    }
                    List<BookRecommend> bookRecommendList = new ArrayList<>();
                    bookByTypeList.forEach(bookByType -> {
                        BookRecommend bookRecommend = new BookRecommend();
                        bookRecommend.setBookid(bookByType.getBookid());
                        bookRecommend.setBookname(bookByType.getBookname());
                        bookRecommend.setAvatar(bookByType.getAvatar());
                        bookRecommendList.add(bookRecommend);
                    });
                    return ApiResult.getApiResult(bookRecommendList);
                }
                List<BookDetail> bookDetailList = bookDetailMapper.queryBookByType(typeId);
                if (!bookDetailList.isEmpty() && bookDetailList.size() > 0){
                    if (bookDetailList.size() > 8){
                        bookDetailList = bookDetailList.subList(0, 8);
                    }
                    List<BookRecommend> bookRecommendList = new ArrayList<>();
                    bookDetailList.forEach(bookDetail -> {
                        BookRecommend bookRecommend = setBookRecommend(bookDetail);
                        bookRecommendList.add(bookRecommend);
                    });
                    bookByTypeList(typeId);
                    return ApiResult.getApiResult(bookRecommendList);
                }
            }
            return ApiResult.getApiResult(new ArrayList<BookRecommend>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryByFavor is error!~~ username = {}", username, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    @Override
    public Mono delBook(String bookId) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, ResBookDetail> bookDetailValueOperations = redisConfig.getRedisTemplate().opsForValue();
            HashOperations<String, String, BookByType> bookByTypeHashOperations = redisConfig.getRedisTemplate().opsForHash();
            ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            Mono<BookDetail> bookDetailMono = selectBookDetail(bookId);
            return bookDetailMono.flatMap(bookDetail -> {
                String fileName = staticConfig.getFilePath() + File.separator + bookDetail.getAvatar();
                if (bookDetailMapper.deleteByPrimaryKey(bookId) > 0 && deleteAvatar(fileName)){
                    commentMapper.deleteByBookId(bookId);
                    bookDetailValueOperations.getOperations().delete(bookId);
                    bookByTypeHashOperations.delete(bookDetail.getTypeid(), bookId);
                    valueOperations.getOperations().delete("queryOrderSold");
                    valueOperations.getOperations().delete("queryByFavorNull");
                    return Mono.just(ApiResult.getApiResult(200, "del book successfully"));
                }
                return Mono.just(ApiResult.getApiResult(-1, "del book failly"));
            });
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delBook is error!~~ bookId = {}", bookId, t))
                .onErrorReturn(Mono.just(ApiResult.getApiResult(-1, "del book failly")));
    }

    /**
     * 删除头像
     * @param fileName
     * @return
     */
    private boolean deleteAvatar(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Mono updateBook(String bookId, String price, String quantity) {
        return Mono.fromSupplier(() -> {
            if (bookDetailMapper.updateBook(bookId, price, quantity) > 0){
                RedisConfig redisConfig = new RedisConfig(redisTemplate);
                ValueOperations<String, ResBookDetail> valueOperations = redisConfig.getRedisTemplate().opsForValue();
                HashOperations<String, String, BookByType> bookByTypeHashOperations = redisConfig.getRedisTemplate().opsForHash();
                if (redisConfig.getRedisTemplate().hasKey(bookId)) {
                    ResBookDetail resBookDetail = valueOperations.get(bookId);
                    resBookDetail.setQuantity(quantity);
                    resBookDetail.setPrice(price);
                    valueOperations.set(bookId, resBookDetail);
                }
                BookDetail bookDetail = bookDetailMapper.selectByPrimaryKey(bookId);
                bookByTypeList(bookDetail.getTypeid());
                return ApiResult.getApiResult(200, "update book successfully");
            }
            return ApiResult.getApiResult(-1, "update book failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("updateBook is error!~~ bookId = {}", bookId, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "update book failly"));
    }

    @Override
    public Mono<ApiResult<? extends List<AdminQuery>>> adminManageBook() {
        // 只走一个IO, 不使用redis
        return Mono.fromSupplier(() -> {
            List<BookType> bookTypeList = bookTypeMapper.queryBookType();
            List<AdminQuery> adminQueryList = new ArrayList<>();
            if (!bookTypeList.isEmpty() && bookTypeList.size() > 0){
                bookTypeList.forEach(bookType -> {
                    List<BookDetail> bookDetailList = bookDetailMapper.queryBookByType(bookType.getTypeid());
                    List<AdminBook> bookList = new ArrayList<>();
                    if (!bookDetailList.isEmpty() && bookDetailList.size() > 0){
                        bookDetailList.forEach(bookDetail -> {
                            AdminBook adminBook = new AdminBook();
                            adminBook.setBookid(bookDetail.getBookid());
                            adminBook.setAvatar(bookDetail.getAvatar());
                            adminBook.setBookname(bookDetail.getBookname());
                            adminBook.setPrice(bookDetail.getPrice());
                            adminBook.setQuantity(bookDetail.getQuantity());
                            bookList.add(adminBook);
                        });
                    }
                    AdminQuery adminQuery = new AdminQuery();
                    adminQuery.setTypeName(bookType.getTypename());
                    adminQuery.setAdminBookList(bookList);
                    adminQueryList.add(adminQuery);
                });
                return ApiResult.getApiResult(adminQueryList);
            }
            return ApiResult.getApiResult(new ArrayList<AdminQuery>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("adminManageBook is error!~~ ", t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }
}
