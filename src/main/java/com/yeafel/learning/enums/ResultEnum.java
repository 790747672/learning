package com.yeafel.learning.enums;

import lombok.Getter;

/**
 * Created by kangyifan on 2018/9/17 15:11
 */
@Getter
public enum ResultEnum {

    SUCCESS(0,"�ɹ�"),

    PARAM_ERROR(1,"��������ȷ"),

    LOGIN_FAIL(10,"��¼ʧ��,��¼��Ϣ����ȷ"),

    USER_NOT_EXIST(11,"�û�������"),

    PASSWORD_ERROR(12,"�������"),

    ROLE_REJECT(13,"�㲻ӵ�д˽�ɫ"),

    ROLE_NOT_EXIST(14,"��ɫ������"),

    UPDATE_ERROR(15,"����ʧ��"),

    CREATE_ERROR(16,"����ʧ��"),

    COURSE_NOT_EXIST(17,"�γ̲�����"),

    ACTIONROLE_NOT_EXIST(18,"��ɫȨ�޲���"),

    COURSEWARE_NOT_EXIST(19,"�μ�������"),

    RECORD_NOT_EXIST(20,"��¼������"),

    ;

    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
