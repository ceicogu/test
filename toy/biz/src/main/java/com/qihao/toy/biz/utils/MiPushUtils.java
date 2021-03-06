package com.qihao.toy.biz.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import com.xiaomi.xmpush.server.TargetedMessage;

@Slf4j
public class MiPushUtils {
	private final static String PACKAGENAME = "box.qihao.com.qihaohao";
	private final static String APP_SECRET_KEY = "eGZnHPky5HHDXGwN1+ON9Q==";
	

    /**
     * 构造推送消息体
     * @param title
     * @param description
     * @param commandMessage
     * @return
     * @throws Exception
     */
	public static Message buildMessage(String title, String description,Object commandMessage) throws Exception {
	     Message message = new Message.Builder()
	             .title(title)
	             .description(description)
	             .payload(JSON.toJSONString(commandMessage))
	             .restrictedPackageName(PACKAGENAME)
	             .passThrough(1)  //消息使用透传方式
	             .notifyType(1)  // 使用默认提示音提示
	             .build();
	     return message;
	}

	public static List<TargetedMessage> buildMessages(Message message) throws Exception {
	     List<TargetedMessage> messages = new ArrayList<TargetedMessage>();
	     TargetedMessage message1 = new TargetedMessage();
	     message1.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, "alias1");
	     message1.setMessage(message);
	     messages.add(message1);
	     TargetedMessage message2 = new TargetedMessage();
	     message2.setTarget(TargetedMessage.TARGET_TYPE_ALIAS, "alias2");
	     message2.setMessage(message);
	     messages.add(message2);
	     return messages;
	}
	public static String sendMessage(Message message,String regId ) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.send(message,regId,0);//根据regID，发送消息到指定设备上，不重试。	     
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());	     
	     return result.getMessageId();
	}
	public static String sendMessages(List<TargetedMessage> messages) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.send(messages, 0); //根据alias，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	     return result.getMessageId();
	}

	public static String sendMessageToAlias(Message message, String alias) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =   sender.sendToAlias(message, alias, 0); //根据alias，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	     return result.getMessageId();
	}
	 //alias非空白，不能包含逗号，长度小于128。
	public static String sendMessageToAliases(Message message, List<String> aliasList) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.sendToAlias(message, aliasList, 0); //根据aliasList，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	     return result.getMessageId();
	}

	public static String sendBroadcast(Message message, String topic) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result = sender.broadcast(message, topic, 0); //根据topic，发送消息到指定一组设备上，不重试。
	     return result.getMessageId();
	}
	public static void main(String args[]) {
	     String title = "notification title";
	     String description = "notification description";
	     String regId="Wi3fQCBrI6JHU6gnsqfKdDc3SHkr01XnH3/afTPglWJmdbU3mQ6GzM/jX5lpa0ypMvhM1y0Db1XVu1Ra1JAlJtB4gaOaGziVLjUyOHBzO8o=";
		try {
			Message message = buildMessage(title, description,null);
			sendMessage(message,regId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
