package com.zs.project.domain.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZhuangShuo
 * @date 2024/10/14
 * @description
 */

@Data
public class UserLogoutRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

}
