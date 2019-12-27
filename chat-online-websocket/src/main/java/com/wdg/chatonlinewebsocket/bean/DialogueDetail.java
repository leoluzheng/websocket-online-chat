package com.wdg.chatonlinewebsocket.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * Description:
 * @author: WuDG
 * 1490727316@qq.com
 * https://github.com/WuDg
 */
@SuppressWarnings("all")
@Data
public class DialogueDetail implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;


    /**
     * 
     */
    private Date createTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 渠道
     */
    private Integer channelId;

    private static final long serialVersionUID = 1L;

}