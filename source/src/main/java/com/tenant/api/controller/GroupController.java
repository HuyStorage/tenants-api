package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.group.GroupDto;
import com.tenant.api.dto.groupPermission.GroupPermissionDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.group.CreateGroupForm;
import com.tenant.api.form.group.UpdateGroupForm;
import com.tenant.api.mapper.GroupMapper;
import com.tenant.api.service.feign.FeignPermissionAuthService;
import com.tenant.api.storage.tenant.criteria.GroupCriteria;
import com.tenant.api.storage.tenant.model.Group;
import com.tenant.api.storage.tenant.model.GroupPermission;
import com.tenant.api.storage.tenant.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/group")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class GroupController extends ABasicController {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private FeignPermissionAuthService feignPermissionAuthService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateGroupForm createGroupForm) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed create.");
        }

        if (groupRepository.existsByName(createGroupForm.getName())) {
            throw new BadRequestException("[Group] Group name is existed", ErrorCode.GROUP_ERROR_NAME_EXISTED);
        }

        Group group = groupMapper.fromCreateGroupFormToEntity(createGroupForm);

        List<GroupPermission> permissions = new ArrayList<>();
        if (!createGroupForm.getPermissions().isEmpty()) {
            ApiMessageDto<List<GroupPermissionDto>> groupPermissionList = feignPermissionAuthService.getPermissionByIds(createGroupForm.getPermissions());
            if (groupPermissionList.getResult() && !groupPermissionList.getData().isEmpty()) {
                for (GroupPermissionDto groupPermissionDto : groupPermissionList.getData()) {
                    GroupPermission groupPermission = new GroupPermission();
                    groupPermission.setGroup(group);
                    groupPermission.setPermissionId(groupPermissionDto.getId());
                    groupPermission.setPermissionCode(groupPermissionDto.getPermissionCode());
                    permissions.add(groupPermission);
                }
            }
        }
        group.setPermissions(permissions);

        int kind = createGroupForm.getKind() != null ? createGroupForm.getKind() : groupRepository.getNextKind();
        group.setKind(kind);
        groupRepository.save(group);
        return makeSuccessResponse("Create group success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateGroupForm updateGroupForm) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed update.");
        }

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(updateGroupForm.getId())
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

        // Check if the new name already exists
        if (!Objects.equals(updateGroupForm.getName(), group.getName())
                && groupRepository.existsByName(updateGroupForm.getName())) {
            throw new BadRequestException("[Group] Name existed", ErrorCode.GROUP_ERROR_NAME_EXISTED);
        }

        List<GroupPermission> permissions = new ArrayList<>();
        if (!updateGroupForm.getPermissions().isEmpty()) {
            ApiMessageDto<List<GroupPermissionDto>> groupPermissionList = feignPermissionAuthService.getPermissionByIds(updateGroupForm.getPermissions());
            if (groupPermissionList.getResult() && !groupPermissionList.getData().isEmpty()) {
                for (GroupPermissionDto groupPermissionDto : groupPermissionList.getData()) {
                    GroupPermission groupPermission = new GroupPermission();
                    groupPermission.setGroup(group);
                    groupPermission.setPermissionId(groupPermissionDto.getId());
                    groupPermission.setPermissionCode(groupPermissionDto.getPermissionCode());
                    permissions.add(groupPermission);
                }
            }
        }
        group.getPermissions().clear();
        group.getPermissions().addAll(permissions);

        groupMapper.fromUpdateGroupFormToEntity(updateGroupForm, group);
        groupRepository.save(group);

        apiMessageDto.setMessage("Update group success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_V')")
    public ApiMessageDto<GroupDto> get(@PathVariable("id") Long id) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed to get.");
        }

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found!", ErrorCode.GROUP_ERROR_NOT_FOUND));
        return makeSuccessResponse(groupMapper.fromEntityToGroupDto(group), "Get group success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_L')")
    public ApiMessageDto<ResponseListDto<List<GroupDto>>> list(GroupCriteria groupCriteria, Pageable pageable) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed to get.");
        }

        List<Integer> excludeKinds = new ArrayList<>();
        excludeKinds.add(BaseConstant.USER_KIND_USER);
        excludeKinds.add(BaseConstant.USER_KIND_USER_VIP);
        groupCriteria.setExcludeKinds(excludeKinds);

        Page<Group> groups = groupRepository
                .findAll(groupCriteria.getSpecification(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        return makeSuccessResponse(makeResponseListDto(groups, groupMapper::fromEntityToGroupDtoList), "List group success.");
    }
}
