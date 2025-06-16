package com.tenant.api.controller;

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
import org.springframework.validation.BindingResult;
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
    GroupRepository groupRepository;
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    FeignPermissionAuthService feignPermissionAuthService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateGroupForm createGroupForm, BindingResult bindingResult) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed create.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findFirstByName(createGroupForm.getName());
        if (group != null) {
            throw new BadRequestException("[Group] Group name is existed", ErrorCode.GROUP_ERROR_NAME_EXISTED);
        }
        group = groupMapper.fromCreateGroupFormToEntity(createGroupForm);
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
        group.setKind(groupRepository.getNextKind());
        groupRepository.save(group);
        apiMessageDto.setMessage("Create group success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateGroupForm updateGroupForm, BindingResult bindingResult) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed update.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(updateGroupForm.getId())
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));
        // Check if the new name already exists
        Group otherGroup = groupRepository.findFirstByName(updateGroupForm.getName());
        if (otherGroup != null && !Objects.equals(updateGroupForm.getId(), otherGroup.getId())) {
            throw new BadRequestException("[Group] Cant update this group name because it is exist!", ErrorCode.GROUP_ERROR_NAME_EXISTED);
        }
        group.setName(updateGroupForm.getName());
        group.setDescription(updateGroupForm.getDescription());

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
        ApiMessageDto<GroupDto> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found!", ErrorCode.GROUP_ERROR_NOT_FOUND));
        apiMessageDto.setData(groupMapper.fromEntityToGroupDto(group));
        apiMessageDto.setMessage("Get group success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_L')")
    public ApiMessageDto<ResponseListDto<List<GroupDto>>> list(GroupCriteria groupCriteria, Pageable pageable) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed to get.");
        }
        Page<Group> groups = groupRepository
                .findAll(groupCriteria.getSpecification(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        ResponseListDto<List<GroupDto>> responseListDto = makeResponseListDto(groups, groupMapper::fromEntityToGroupDtoList);
        return makeSuccessResponse(responseListDto, "List group success.");
    }
}
