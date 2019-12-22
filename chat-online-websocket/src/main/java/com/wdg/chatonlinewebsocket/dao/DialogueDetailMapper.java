package com.wdg.chatonlinewebsocket.dao;

import com.wdg.chatonlinewebsocket.bean.DialogueDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
@Mapper
@SuppressWarnings("all")
public interface DialogueDetailMapper {
	@Insert("insert into dialogue_detail(USER_ID,ADMIN_ID,CREATE_TIME,STATUS,CHANNEL_ID) values" +
			"(#{userId},#{adminId},#{createTime},#{status},#{channelId})")
	int add(DialogueDetail dialogueDetail);
	
	@Update("update dialogue_detail set USER_ID=#{userId},ADMIN_ID=#{adminId},STATUS=#{status},CREATE_TIME=#{createTime}," +
			"UPDATE_TIME=#{updateTime},CHANNEL_ID=#{channelId} where id=#{id}")
    int update(DialogueDetail dialogueDetail);
	
	@Delete("delete from dialogue_detail where ID=#{id}")
    int deleteById(Integer id);
	
	@Select("select * from dialogue_detail where id=#{id}")
	@Results(id = "dialogue",value= {
		 @Result(property = "userId", column = "USER_ID", javaType = Integer.class),
         @Result(property = "adminId", column = "ADMIN_ID", javaType = Integer.class),
         @Result(property = "status", column = "STATUS", javaType = Integer.class),
         @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class),
         @Result(property = "channelId", column = "CHANNEL_ID", javaType = Integer.class)
	})
	DialogueDetail queryDialogueDetailById(Integer id);
}
