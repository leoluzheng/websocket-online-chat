package com.wdg.chatonlinewebsocket.bean;

import com.wdg.chatonlinewebsocket.utils.penum.ChatEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * @author: WuDG
 * 1490727316@qq.com
 * https://github.com/WuDg
 */
@Data
@SuppressWarnings("all")
public class Dialogue implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 类型 1：文字 2：音频 3：文件 4：图片 5：表情
     */
    private Integer messageType;

    /**
     * 发送方ID
     */
    private Integer senderId;

    /**
     * 接收方ID
     */
    private Integer receiveId;

    /**
     * 本条记录是否有效 0 已读，1过期  2 未读
     */
    private Integer status;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
    /**
     * 用户IP
     */
    private String userIp;
    /**
     * 聊天内容，包括图片和文本内容
     */
    private byte[] messageContent;

    /**
     * 频道ID
     * @return
     */

    private Integer channelId;

    private static final long serialVersionUID = 1L;

}