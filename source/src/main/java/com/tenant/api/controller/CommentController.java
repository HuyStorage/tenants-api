package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.comment.CommentDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.comment.CreateCommentForm;
import com.tenant.api.form.comment.PinnedCommentForm;
import com.tenant.api.form.comment.UpdateCommentForm;
import com.tenant.api.form.reaction.CreateReactionForm;
import com.tenant.api.mapper.CommentMapper;
import com.tenant.api.storage.tenant.criteria.CommentCriteria;
import com.tenant.api.storage.tenant.model.Account;
import com.tenant.api.storage.tenant.model.Comment;
import com.tenant.api.storage.tenant.model.MovieItem;
import com.tenant.api.storage.tenant.model.Reaction;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import com.tenant.api.storage.tenant.repository.CommentRepository;
import com.tenant.api.storage.tenant.repository.MovieItemRepository;
import com.tenant.api.storage.tenant.repository.ReactionRepository;
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

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateCommentForm form) {
        Account account = accountRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[Account] not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        Comment comment = commentMapper.fromCreateCommentFormToEntity(form);
        comment.setAuthor(account);

        if (form.getMovieItemId() != null) {
            MovieItem movieItem = movieItemRepository.findById(form.getMovieItemId())
                    .orElseThrow(() -> new NotFoundException("[MovieItem] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));
            comment.setMovieItem(movieItem);
            comment.setMovieId(movieItem.getMovie().getId());
        } else {
            comment.setMovieId(form.getMovieId());
        }

        if (form.getParentId() != null) {
            Comment parent = commentRepository.findById(form.getParentId())
                    .orElseThrow(() -> new NotFoundException("[Comment] not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new BadRequestException("[Comment] parent invalid", ErrorCode.COMMENT_ERROR_PARENT_INVALID);
            }
            commentRepository.increaseTotalChild(parent.getId());
            comment.setParent(parent);
        }

        commentRepository.save(comment);
        return makeSuccessResponse("Create employee success");
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
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(comments, commentMapper::fromEntityToCommentDtoList), "Get list comment success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_L')")
    public ApiMessageDto<ResponseListDto<List<CommentDto>>> listAdmin(CommentCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdDate")));
        Page<Comment> comments = commentRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(comments, commentMapper::fromEntityToCommentDtoList), "Get list comment success");
    }

    @PatchMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateCommentForm form) {
        Comment comment = commentRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Comment] Not found", ErrorCode.COMMENT_ERROR_NOT_FOUND));

        if (comment.getAuthor().getId() != getCurrentUser()) {
            throw new UnauthorizationException("Not allow");
        }

        comment.setContent(form.getContent());
        commentRepository.save(comment);
        return makeSuccessResponse("Update comment success");
    }

    @PatchMapping(value = "/pin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('CMT_U')")
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
    @PatchMapping(value = "/vote", produces = MediaType.APPLICATION_JSON_VALUE)
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

        if (comment.getParent() != null) {
            commentRepository.decreaseTotalChild(comment.getParent().getId());
        } else {
            commentRepository.deleteByParentId(comment.getId());
        }

        reactionRepository.deleteByCommentId(comment.getId());
        commentRepository.delete(comment);
        return makeSuccessResponse("Delete comment success");
    }
}
