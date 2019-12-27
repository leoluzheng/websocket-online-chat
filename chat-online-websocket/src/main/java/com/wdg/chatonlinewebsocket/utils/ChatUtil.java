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
import com.wdg.chatonlinewebsocket.utils.penum.ChatEnum;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat/{channel}/{userId}/{toUserId}")
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

    // concurrent包的线程安全Set，用来存放每个客户端对应的ProductWebSocket对象。
    private static CopyOnWriteArraySet<ChatUtil> webSocketSet = new CopyOnWriteArraySet<ChatUtil>();

    private String userId = "";
    private String toUserId = "";
    private String channel = "";
    private String storeUserId = "";
    private DialogueDetail detail;
    private Boolean isExpert = false;
    private List<DialogueDetail> listDetail;

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
    public void onOpen(Session session, @PathParam("channel") String channel, @PathParam("userId") String userId,@PathParam("toUserId") String toUserId) {
        this.session = session;
        //拿到用户访问IP
        this.userIp = ChatOnlineWebsocketApplication.userIp;
        websocketList.put(userId, this);
        log.info("websocketList->" + JSON.toJSONString(websocketList));
        //加入到不可重复集合中
        userSet.add(userId);
        webSocketSet.add(this); // 加入set中
        addOnlineCount();
        log.info("有新窗口开始监听:" + userId +"对方用户ID:"+toUserId+",IP:"+userIp+ ",当前在线人数为" + getOnlineCount() + ",接入渠道ID为:" + channel);
        log.info("此时全部在线用户ID:" + JSON.toJSONString(userSet));
        this.userId = userId;
        this.toUserId = toUserId;
        this.channel = channel;
        this.storeUserId = userId;
        try {
            sendMessage(JSON.toJSONString(Result.success("连接成功")));
            // TODO: 2019/12/17 用户连接上websocket后查询出未读消息 并置位已读
            //用户上线后 把数据库消息读出
            Dialogue tmp = new Dialogue();
            //查询管理员用户
            DialogueDetail de = new DialogueDetail();
            de.setChannelId(Integer.parseInt(channel));
            de.setStatus(1);
            listDetail = realDialogueDetailService.selectByDialogueDetail(de);
            log.info("查询到DialogueDetailList:"+JSON.toJSONString(listDetail));
            isExpert = listDetail.get(0).getUserId().equals(userId);
            //如果当前用户ID不属于表单对应ID中聊天双方之一，则不返回数据
            //查询相同频道下面的全部用户
            de.setStatus(null);
            listDetail = realDialogueDetailService.selectByDialogueDetail(de);
            if(listDetail.size()>0){
                List<Integer> userIds = new ArrayList<>();
                listDetail.stream().forEach(det ->{
                    userIds.add(det.getUserId());
                });
                if(!userIds.contains(Integer.valueOf(userId))){
                    sendMessage(JSON.toJSONString(Result.success("数据查询出错")));
                    return;
                }
            }
            tmp.setChannelId(Integer.parseInt(channel));
            List<Dialogue> dialoguesList = realDialogueService.selectByDialogue(tmp);
            sendWebScoket(dialoguesList);
            log.info("根据表单ID拿到聊天记录:" + JSON.toJSONString(dialoguesList));

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
                    JSONObject object = new JSONObject();
                    object.put("toUserId", dia.getSenderId());
                    object.put("receiveId", dia.getReceiveId());
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
            webSocketSet.remove(this); // 从set中删除
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
//        if(dialogueDetailId == "" || !dialogueDetailId.equals(detail.getId().toString())){
//            sendMessage(JSON.toJSONString(Result.success("登录表单ID出错")));
//        }
        if (StringUtils.isNotBlank(message)) {
            JSONArray list = JSONArray.parseArray(message);
            for (int i = 0; i < list.size(); i++) {
                try {
                    //解析发送的报文
                    JSONObject object = list.getJSONObject(i);
                    log.info("当前用户是专家吗?"+isExpert);
                    byte[] contentText = object.getString("contentText").getBytes();;
                    object.put("contentText", new String(contentText).replaceAll("&quot;", "\""));
//                    object.put("fromUserId", storeUserId);
                    object.put("toUserId", userId);
                    object.put("receiveId", toUserId);
                    //传送给对应用户的websocket
                    log.info("接收到的数据:" + JSON.toJSONString(Result.success(object)));
                    Dialogue dialogue = new Dialogue();
                    dialogue.setMessageContent(contentText);
                    dialogue.setSenderId(Integer.parseInt(userId));
                    dialogue.setCreateTime(new Date());
                    dialogue.setReceiveId(Integer.parseInt(toUserId));
                    dialogue.setStatus(ChatEnum.UNREAD.getValue());
                    dialogue.setUserIp(userIp);
                    //此处是提交的提问表单ID
                    dialogue.setChannelId(Integer.parseInt(channel));
                    dialogue.setMessageType(ChatEnum.CHARACTER.getValue());
                    dialogue.setChannelId(Integer.parseInt(this.channel));
                    if (StringUtils.isNotBlank(toUserId.toString()) && StringUtils.isNotBlank(contentText.toString())) {
                        log.info("toUserId=" + JSON.toJSONString(toUserId));
                        log.info("在线的人:websocketList->" + JSON.toJSONString(websocketList));

                        if("0".equals(toUserId)){
                            //按所在频道群发
                            log.info("群发:"+JSON.toJSONString(listDetail));
                            List<Integer> userIds = new ArrayList<>();
                            listDetail.stream().forEach(det ->{
                                userIds.add(det.getUserId());
                            });
                            userIds.remove(Integer.valueOf(userId));
                            for(Integer id:userIds){
                                ChatUtil tx = websocketList.get(id.toString());
                                if (tx != null) {
                                    tx.sendMessage(JSON.toJSONString(Result.success(object)));
                                    log.info("object="+ JSON.toJSONString(object));
                                    //此处可以放置相关业务代码，例如存储到数据库
                                    //如果对方在线 则发送并置位已读
                                    dialogue.setStatus(ChatEnum.EXPIRE.getValue());
                                }
                            }

                        }
                        else{
                            //一对一发信息
                            ChatUtil socketx = websocketList.get(toUserId);
                            //需要进行转换，userId
                            if (socketx != null) {
                                socketx.sendMessage(JSON.toJSONString(Result.success(object)));
                                log.info("object="+ JSON.toJSONString(object));
                                //此处可以放置相关业务代码，例如存储到数据库
                                //如果对方在线 则发送并置为已读
                                dialogue.setStatus(ChatEnum.MARKREAD.getValue());
                            }else{
                                //这个判断消息是否已读未读，未读则发送模板消息
                                log.info("这里应该通知"+toUserId+" 有新消息，模板消息中放入链接");
                            }
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
    public static void sendInfo(String message,@PathParam("userId") String userId) throws IOException {
        log.info("推送消息到窗口"+userId+"，推送内容:"+message);
        for (ChatUtil item : webSocketSet) {
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
    }
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