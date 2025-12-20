package com.tenant.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.account.CustomerDto;
import com.tenant.api.dto.comment.AuthorInfoDto;
import com.tenant.api.dto.comment.CommentDto;
import com.tenant.api.dto.reaction.VoteDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.ChangeStatusForm;
import com.tenant.api.form.comment.CreateCommentForm;
import com.tenant.api.form.comment.PinnedCommentForm;
import com.tenant.api.form.comment.UpdateCommentForm;
import com.tenant.api.form.reaction.CreateReactionForm;
import com.tenant.api.mapper.AccountMapper;
import com.tenant.api.mapper.CommentMapper;
import com.tenant.api.service.MovieService;
import com.tenant.api.service.feign.FeignCustomerAuthService;
import com.tenant.api.storage.tenant.criteria.CommentCriteria;
import com.tenant.api.storage.tenant.model.*;
import com.tenant.api.storage.tenant.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/comment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CommentController extends ABasicController {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovieItemRepository movieItemRepository;

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private FeignCustomerAuthService feignCustomerAuthService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieService movieService;

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_C')")
    public ApiMessageDto<CommentDto> create(@Valid @RequestBody CreateCommentForm form) throws JsonProcessingException {
        AuthorInfoDto authorInfoDto;
        Long authorId;
        if (isShop()) {
            CustomerDto customerDto = feignCustomerAuthService.get(getCurrentUser()).getData();
            authorId = customerDto.getId();
            authorInfoDto = accountMapper.fromCustomerDtoToAuthorInfoDto(customerDto);
        } else {
            Account account = accountRepository.findById(getCurrentUser())
                    .orElseThrow(() -> new NotFoundException("[Account] not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
            authorId = account.getId();
            authorInfoDto = accountMapper.entityToAuthorInfoDto(account);
            if (isUser()) {
                User user = userRepository.findByIdAndStatus(account.getId(), BaseConstant.STATUS_ACTIVE)
                        .orElseThrow(() -> new NotFoundException("[User] not found", ErrorCode.USER_ERROR_NOT_FOUND));
                authorInfoDto.setGender(user.getGender());
            }
        }

        Comment comment = commentMapper.fromCreateCommentFormToEntity(form);
        comment.setAuthorId(authorId);
        comment.setAuthorInfo(objectMapper.writeValueAsString(authorInfoDto));

        if (form.getMovieItemId() != null) {
            MovieItem movieItem = movieItemRepository.findById(form.getMovieItemId())
                    .orElseThrow(() -> new NotFoundException("[MovieItem] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));
            comment.setMovieItem(movieItem);
            comment.setMovieId(movieItem.getMovie().getId());
        } else {
            Movie movie = movieRepository.findById(form.getMovieId())
                    .orElseThrow(() -> new NotFoundException("[Movie] not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
            comment.setMovieId(movie.getId());
        }

        if (form.getParentId() != null) {
            if (form.getReplyToId() == null || form.getReplyToKind() == null) {
                throw new BadRequestException("[Comment] reply invalid", ErrorCode.COMMENT_ERROR_REPLY_INVALID);
            }
            Comment parent = commentRepository.findById(form.getParentId())
                    .orElseThrow(() -> new NotFoundException("[Comment] not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new BadRequestException("[Comment] parent invalid", ErrorCode.COMMENT_ERROR_PARENT_INVALID);
            }
            Long replyToId;
            AuthorInfoDto replyToInfo;
            if (Objects.equals(form.getReplyToKind(), BaseConstant.USER_KIND_MANAGER)) {
                CustomerDto customerDto = feignCustomerAuthService.get(form.getReplyToId()).getData();
                if (customerDto == null) {
                    throw new NotFoundException("[Customer] not found", ErrorCode.COMMENT_ERROR_REPLY_NOT_FOUND);
                }
                replyToId = customerDto.getId();
                replyToInfo = accountMapper.fromCustomerDtoToAuthorInfoDto(customerDto);
            } else {
                Account account = accountRepository.findById(form.getReplyToId())
                        .orElseThrow(() -> new NotFoundException("[Account] not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
                replyToId = account.getId();
                replyToInfo = accountMapper.entityToAuthorInfoDto(account);
            }
            commentRepository.increaseTotalChild(parent.getId());
            comment.setReplyToId(replyToId);
            comment.setReplyToInfo(objectMapper.writeValueAsString(replyToInfo));
            comment.setParent(parent);
        }

        commentRepository.save(comment);
        movieService.calculateComment(comment.getMovieId(), BaseConstant.ACTION_ADD);
        return makeSuccessResponse(commentMapper.entityToCommentDto(comment), "Create comment success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_V')")
    public ApiMessageDto<CommentDto> get(@PathVariable("id") Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));
        return makeSuccessResponse(commentMapper.entityToCommentDto(comment), "Get comment success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<CommentDto>>> list(CommentCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdDate")));
//        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        criteria.setIsParent(criteria.getParentId() == null);
        Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(comments, commentMapper::fromEntityToCommentDtoList), "Get list comment success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_L')")
    public ApiMessageDto<ResponseListDto<List<CommentDto>>> listAdmin(CommentCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdDate")));
        criteria.setIsParent(criteria.getParentId() == null);
        Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(comments, commentMapper::fromEntityToCommentDtoList), "Get list comment success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateCommentForm form) {
        Comment comment = commentRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));

        if (comment.getAuthorId() != getCurrentUser()) {
            throw new UnauthorizationException("Not allow");
        }

        comment.setContent(form.getContent());
        commentRepository.save(comment);
        return makeSuccessResponse("Update comment success");
    }

    @PutMapping(value = "/pin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_PIN')")
    public ApiMessageDto<Void> pin(@Valid @RequestBody PinnedCommentForm form) {
        if (isUser()) {
            throw new UnauthorizationException("Not allow");
        }

        Comment comment = commentRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));

        comment.setIsPinned(form.getIsPinned());
        commentRepository.save(comment);
        return makeSuccessResponse("Pin comment success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/vote", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_VOTE')")
    public ApiMessageDto<Void> vote(@Valid @RequestBody CreateReactionForm form) {
        Long userId = getCurrentUser();
        Comment comment = commentRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));

        Reaction reaction = reactionRepository.findFirstByCommentIdAndUserId(comment.getId(), userId).orElse(null);

        if (reaction == null) {
            reaction = new Reaction();
            reaction.setCommentId(comment.getId());
            reaction.setUserId(userId);
            reaction.setType(form.getType());
            reactionRepository.save(reaction);
            increaseCounter(comment.getId(), form.getType());
        } else if (reaction.getType().equals(form.getType())) {
            reactionRepository.delete(reaction);
            decreaseCounter(comment.getId(), form.getType());
        } else {
            increaseCounter(comment.getId(), form.getType());
            decreaseCounter(comment.getId(), reaction.getType());
            reaction.setType(form.getType());
            reactionRepository.save(reaction);
        }
        return makeSuccessResponse("Vote success");
    }

    @GetMapping(value = "/vote-list/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<VoteDto>> voteList(@PathVariable("movieId") Long movieId) {
        return makeSuccessResponse(commentRepository.findVotesByMovieIdAndUserId(movieId, getCurrentUser()), "Get list vote success");
    }

    private void increaseCounter(Long commentId, Integer type) {
        if (Objects.equals(type, BaseConstant.REACTION_TYPE_LIKE)) {
            commentRepository.increaseTotalLike(commentId);
        } else {
            commentRepository.increaseTotalDislike(commentId);
        }
    }

    private void decreaseCounter(Long commentId, Integer type) {
        if (Objects.equals(type, BaseConstant.REACTION_TYPE_LIKE)) {
            commentRepository.decreaseTotalLike(commentId);
        } else {
            commentRepository.decreaseTotalDislike(commentId);
        }
    }

    @Transactional("tenantTransactionManager")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));

        if (isUser() && comment.getAuthorId() != getCurrentUser()) {
            throw new UnauthorizationException("Not allow");
        }

        if (comment.getParent() != null) {
            commentRepository.decreaseTotalChild(comment.getParent().getId());
        } else {
            commentRepository.deleteByParentId(comment.getId());
        }
        movieService.calculateComment(comment.getMovieId(), BaseConstant.ACTION_DELETE);
        reactionRepository.deleteByCommentId(comment.getId());
        commentRepository.delete(comment);
        return makeSuccessResponse("Delete comment success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/change-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_D')")
    public ApiMessageDto<Void> changeStatus(@Valid @RequestBody ChangeStatusForm form) {
        Comment comment = commentRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));
        if (comment.getParent() == null) {
            commentRepository.updateStatusByParentId(comment.getId(), form.getStatus());
        }
        comment.setStatus(form.getStatus());
        commentRepository.save(comment);
        return makeSuccessResponse("Change status success");
    }
}
