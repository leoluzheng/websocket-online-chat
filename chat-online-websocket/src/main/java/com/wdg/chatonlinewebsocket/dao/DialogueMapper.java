package com.wdg.chatonlinewebsocket.dao;

import com.wdg.chatonlinewebsocket.bean.Dialogue;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


//@Component
@Mapper
@SuppressWarnings("all")
public interface DialogueMapper {
	@Insert("insert into dialogue(USER_IP,MESSAGE_TYPE,SENDER_ID,MESSAGE_CONTENT,STATUS,CREATE_TIME,UPDATE_TIME,CHANNEL_ID,DIALOGUE_DETAIL_ID) values" +
			"(#{userIp},#{messageType},#{senderId},#{messageContent},#{status},#{createTime},#{updateTime},#{channelId},#{dialogueDetailId})")
	int add(Dialogue dialogue);

	@Update("update dialogue set MESSAGE_TYPE=#{messageType},MESSAGE_CONTENT=#{messageContent},SENDER_ID=#{senderId},STATUS=#{status},CREATE_TIME=#{createTime}," +
			"UPDATE_TIME=#{updateTime},CHANNEL_ID=#{channelId},DIALOGUE_DETAIL_ID=#{dialogueDetailId} where id=#{id}")
    int update(Dialogue dialogue);

	@Delete("delete from dialogue where ID=#{id}")
    int deleteById(Integer id);

	@Select("select * from dialogue where id=#{id}")
	@Results(id = "dialogue",value= {
		 @Result(property = "messageType", column = "MESSAGE_TYPE", javaType = Integer.class),
         @Result(property = "senderId", column = "SENDER_ID", javaType = Integer.class),
         @Result(property = "status", column = "STATUS", javaType = Integer.class),
         @Result(property = "messageContent", column = "MESSAGE_CONTENT", javaType = byte[].class),
         @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class),
         @Result(property = "updateTime", column = "UPDATE_TIME", javaType = Date.class),
         @Result(property = "channelId", column = "CHANNEL_ID", javaType = Integer.class),
         @Result(property = "dialogueDetailId", column = "DIALOGUE_DETAIL_ID", javaType = Integer.class)
	})
	Dialogue queryDialogueById(Integer id);

	List<Dialogue> selectByDialogue(Dialogue record);
}
