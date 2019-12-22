package com.wdg.chatonlinewebsocket.utils;

/**
 * ClassName: ImController
 * Description:
 * date: 2019/12/6 14:11
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wdg.chatonlinewebsocket.ChatOnlineWebsocketApplication;
import com.wdg.chatonlinewebsocket.bean.Dialogue;
import com.wdg.chatonlinewebsocket.bean.DialogueDetail;
import com.wdg.chatonlinewebsocket.service.DialogueDetailService;
import com.wdg.chatonlinewebsocket.service.DialogueService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{channel}/{dialogueDetailId}/{userId}")
@Component
@SuppressWarnings("all")
public class ChatUtil {

    @Autowired
    private DialogueService dialogueService;
    @Autowired
    private DialogueDetailService dialogueDetailService;


    private static DialogueService realDialogueService;
    private static DialogueDetailService realDialogueDetailService;

    private static final Logger log = LoggerFactory.getLogger(ChatUtil.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * 旧：concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * private static CopyOnWriteArraySet<ImController> webSocketSet = new CopyOnWriteArraySet<ImController>();
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 新：使用map对象，便于根据userId来获取对应的WebSocket
     */
    private static ConcurrentHashMap<String, ChatUtil> websocketList = new ConcurrentHashMap<>();
    
    private String userId = "";
    private String channel = "";
    private String dialogueDetailId = "";
    private String storeUserId = "";
    private DialogueDetail detail;
    private Boolean isExpert = false;

    /**
     * 判断是否是在发消息时过来的数据，如果是在用户连接时查询出来的数据则不插入数据库中
     */
    private String userIp = "";
    private static Set<String> userSet = new HashSet<>();

    @PostConstruct
    public void init() {
        realDialogueService = dialogueService;
        realDialogueDetailService = dialogueDetailService;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("channel") String channel, @PathParam("userId") String userId, @PathParam("dialogueDetailId") String dialogueDetailId) {
        this.session = session;
        //拿到用户访问IP
        this.userIp = ChatOnlineWebsocketApplication.userIp;
        websocketList.put(userId, this);
        log.info("websocketList->" + JSON.toJSONString(websocketList));
        //加入到不可重复集合中
        userSet.add(userId);
        addOnlineCount();
        log.info("有新窗口开始监听:" + userId +",IP:"+userIp+ ",当前在线人数为" + getOnlineCount() + ",接入渠道ID为:" + channel + ",表单ID:" + dialogueDetailId);
        log.info("此时全部在线用户ID:" + JSON.toJSONString(userSet));
        this.userId = userId;
        this.channel = channel;
        this.storeUserId = userId;
        this.dialogueDetailId = dialogueDetailId;
        try {
            sendMessage(JSON.toJSONString(Result.success("连接成功")));
            // TODO: 2019/12/17 用户连接上websocket后查询出未读消息 并置位已读
            //用户上线后 把数据库消息读出
            Dialogue tmp = new Dialogue();
            detail = realDialogueDetailService.queryDialogueDetailById(Integer.parseInt(dialogueDetailId));
            isExpert = detail.getAdminId().toString().equals(userId);
            //如果当前用户ID不属于表单对应ID中聊天双方之一，则不返回数据
            if(detail == null || (!userId.equals(detail.getUserId().toString()) && !userId.equals(detail.getAdminId().toString()))){
                sendMessage(JSON.toJSONString(Result.success("数据查询出错")));
                return;
            }
            log.info("detail="+ JSON.toJSONString(detail));
            tmp.setDialogueDetailId(Integer.parseInt(dialogueDetailId));
            List<Dialogue> dialoguesList = realDialogueService.selectByDialogue(tmp);
            sendWebScoket(dialoguesList);
//            log.info("根据表单ID拿到聊天记录:" + JSON.toJSONString(dialoguesList));

        } catch (IOException e) {
            log.error(e.toString());
        }
    }
    /**
     * 把数据库中数据对应的双方数据全部发送给进入页面的人
     */

    public void sendWebScoket(List<Dialogue> dialoguesList) throws IOException {
        //返回当前用户是否是专家
        ChatUtil socketx = websocketList.get(userId);
        if (socketx != null) {
            JSONObject object = new JSONObject();
            object.put("isExpert", isExpert);
            log.info("是否是专家:"+ JSON.toJSONString(Result.success(object)));
            socketx.sendMessage(JSON.toJSONString(Result.success(object)));
            //此处可以放置相关业务代码，例如存储到数据库
            //如果对方在线 则发送并置位已读
            //log.info("object="+JSON.toJSONString(object));
        }
        for (Dialogue dia : dialoguesList) {
            byte[] contentText = dia.getMessageContent();
            if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(contentText.toString())) {
//                    log.info("toUserId=" + JSON.toJSONString(userId));
                    JSONObject object = new JSONObject();
//                    object.put("fromUserId", dia.getSenderId());
                    object.put("toUserId", dia.getSenderId());
                    object.put("sendTime", dia.getCreateTime());
                    //byte[]转string
                    object.put("contentText", new String(contentText));
                    //需要进行转换，userId
                    if (socketx != null) {
                        socketx.sendMessage(JSON.toJSONString(Result.success(object)));
                        //此处可以放置相关业务代码，例如存储到数据库
                        //如果对方在线 则发送并置位已读
//                        log.info("object="+JSON.toJSONString(object));
                    }
                }

        }
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (websocketList.get(this.userId) != null) {
            websocketList.remove(this.userId);
            userSet.remove(userId);
            subOnlineCount();
            log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
            log.info("现在在线用户ID:" + JSON.toJSONString(userSet));
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        if(dialogueDetailId == "" || !dialogueDetailId.equals(detail.getId().toString())){
            sendMessage(JSON.toJSONString(Result.success("登录表单ID出错")));
        }
        if (StringUtils.isNotBlank(message)) {
            JSONArray list = JSONArray.parseArray(message);
            for (int i = 0; i < list.size(); i++) {
                try {
                    //解析发送的报文
                    JSONObject object = list.getJSONObject(i);
                    log.info("当前用户是专家吗?"+isExpert);
                    Integer toUserId = isExpert?detail.getUserId():detail.getAdminId();
                    byte[] contentText = object.getString("contentText").getBytes();;
                    object.put("contentText", new String(contentText).replaceAll("&quot;", "\""));
//                    object.put("fromUserId", storeUserId);
                    object.put("channId", this.channel);
                    object.put("toUserId", userId);
                    //传送给对应用户的websocket
                    log.info("接收到的数据:" + JSON.toJSONString(Result.success(object)));
                    Dialogue dialogue = new Dialogue();
                    dialogue.setMessageContent(contentText);
//                    dialogue.setReceiveId(isExpert?detail.getExpertId():detail.getUserId());
                    dialogue.setSenderId(Integer.parseInt(userId));
                    dialogue.setCreateTime(new Date());
                    dialogue.setStatus(2);
                    dialogue.setUserIp(userIp);
                    //此处是提交的提问表单ID
                    dialogue.setDialogueDetailId(Integer.parseInt(this.dialogueDetailId));
                    dialogue.setMessageType(1);
                    dialogue.setChannelId(Integer.parseInt(this.channel));
                    if (StringUtils.isNotBlank(toUserId.toString()) && StringUtils.isNotBlank(contentText.toString())) {
                        log.info("toUserId=" + JSON.toJSONString(toUserId));
                        log.info("在线的人:websocketList->" + JSON.toJSONString(websocketList));
                        ChatUtil socketx = websocketList.get(toUserId.toString());

                        //需要进行转换，userId
                        if (socketx != null) {
                            socketx.sendMessage(JSON.toJSONString(Result.success(object)));
                            log.info("object="+ JSON.toJSONString(object));
                            //此处可以放置相关业务代码，例如存储到数据库
                            //如果对方在线 则发送并置位已读
                            dialogue.setStatus(0);
                        }else{
                            //这个判断消息是否已读未读，未读则发送模板消息
                            log.info("这里应该通知"+toUserId+" 有新消息，模板消息中放入链接");
                        }
                    }
                    // TODO: 2019/12/17  把聊天消息存库
                    realDialogueService.add(dialogue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    /*public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException {
        log.info("推送消息到窗口"+userId+"，推送内容:"+message);
        for (ImController item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if(userId==null) {
                    item.sendMessage(message);
                }else if(item.userId.equals(userId)){
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }*/
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ChatUtil.onlineCount = userSet.size();
    }

    public static synchronized void subOnlineCount() {
        ChatUtil.onlineCount = userSet.size();
    }
}