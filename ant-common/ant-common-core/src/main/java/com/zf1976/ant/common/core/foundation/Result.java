package com.zf1976.ant.common.core.foundation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.power.common.util.DateTimeUtil;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

/**
 * 请求响应对象
 *
 * @author ant
 * @since 2020/5/19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("rawtypes")
public class Result<T> {

    /**
     * 响应是否成功
     */
    private Boolean success;

    /**
     * 响应码
     */
    private Integer status;

    /**
     * 错误代码
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int errCode;

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 错误详情
     */
    private String errDetail;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 处理时间
     */
    private String timestamp;

    /**
     * 请求路径
     */
    private String path;


    /**
     * 响应带数据的成功消息
     *
     * @param data 数据
     * @param <E>  E
     * @return 响应对象
     */
    public static <E> Result<E> success(E data) {
        Result<E> vo = new Result<>();
        if (!ObjectUtils.isEmpty(data)) {
            vo.setData(data);
        }
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 响应成功消息
     *
     * @param <E> E
     * @param sign param
     * @return 响应对象
     */
    public static <E> Result<E> success(Void sign) {
        Result<E> vo = new Result<>();
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 响应成功消息
     *
     * @param <E> E
     * @return 响应对象
     */
    public static <E> Result<E> success() {
        Result<E> vo = new Result<>();
        vo.setSuccess(true);
        vo.setStatus(200);
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    /**
     * 返回失败消息
     * @param errMsg 失败消息
     * @return 响应对象
     */
    public static Result fail(String errMsg) {
        return fail(500, errMsg);
    }

    /**
     * 返回失败消息
     * @param errMsg 错误消息
     * @param errCode 错误码
     * @return 响应对象
     */
    public static Result fail(int errCode, String errMsg) {
        Result vo = new Result();
        vo.setSuccess(false);
        vo.setErrCode(errCode);
        vo.setErrMsg(errMsg);
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }

    public static Result fail(HttpStatus httpStatus) {
        Result vo = new Result();
        vo.setSuccess(false);
        vo.setErrCode(httpStatus.value());
        vo.setErrMsg(httpStatus.getReasonPhrase());
        vo.setTimestamp(DateTimeUtil.nowStrTime());
        return vo;
    }
}
