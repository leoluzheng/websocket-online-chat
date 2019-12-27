package com.wdg.chatonlinewebsocket.utils.penum;

/**
 * ClassName: ChatEnum
 * Description:
 * date: 2019/12/27 18:09
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */
public enum ChatEnum implements BaseEnum{

    /**
     * 类型 1：文字 2：音频 3：文件 4：图片 5：表情
     */

    CHARACTER(1,"文字"),
    FILE(2,"文件"),
    PICTURE(3,"图片"),
    EMOJI(4,"表情"),
    AUDIO(5,"音频"),

    /**
     * 本条记录是否有效 0 已读，1过期  2 未读
     */

    MARKREAD(0,"已读"),
    EXPIRE(1,"过期"),
    UNREAD(2,"未读");

    private Integer value;
    private String label;



    ChatEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getLabel() {
        return this.label;
    }}
