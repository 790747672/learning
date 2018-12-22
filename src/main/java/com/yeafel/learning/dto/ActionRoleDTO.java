package com.yeafel.learning.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by kangyifan on 2018/11/12 13:26
 */

@Data
public class ActionRoleDTO {

    private Long actionRoleId;

    /** 角色id */
    private Long roleId;

    /** 功能id */
    private Long actionId;


    /** 角色名称 .*/
    private String roleName;


    /** 功能名数组，用来装某一个用户的所有功能. */
    List<String> actionNames;


    private List<Long> actionIds;
}
