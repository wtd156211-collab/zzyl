package com.zzyl.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author ruoyi
 */
public class CacheConstants {
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
    /**
     * 护理等级列表
     */
    public static final String NURSING_LEVEL_LIST = "nursingLevel:getAll";
    /**
     * 护理计划列表
     */
    public static final String CACHE_NURSING_PLAN_KEY = "nursingPlan:getAll";
    /**
     * 护理项目列表
     */
    public static final Object NURSING_PROJECT_CACHE = "nursingProject:getAll";
    /**
     * 患者列表
     */
    public static final String HEALTH_REPORT_KEY = "healthReport";

}
