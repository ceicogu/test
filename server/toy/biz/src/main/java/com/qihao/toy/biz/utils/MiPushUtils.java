package com.qihao.toy.biz.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import com.xiaomi.xmpush.server.TargetedMessage;

@Slf4j
public class MiPushUtils {
	private final static String PACKAGENAME = " box.qihao.com.qihaohao";
	private final static String APP_SECRET_KEY = "eGZnHPky5HHDXGwN1+ON9Q==";

	/**
	 * 构造推送消息体
	 * @param messagePayload
	 * @param title
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public static Message buildMessage(String title, String description,String messagePayload) throws Exception {
	     Message message = new Message.Builder()
	             .title(title)
	             .description(description)
	             .payload(messagePayload)
	             .restrictedPackageName(PACKAGENAME)
	             .passThrough(1)  //消息使用透传方式
	             .notifyType(1)     // 使用默认提示音提示
	             .build();
	     return message;
	}
	public static Message buildMessage( String title, String description, String messagePayload,Map<String, Object> extraMaps) throws Exception {
	     Message message = new Message.Builder()
	             .title(title)
	             .description(description).payload(messagePayload)
	             .restrictedPackageName(PACKAGENAME)
	             .passThrough(1)  //消息使用透传方式
	             .notifyType(1)     // 使用默认提示音提示
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
	public static void sendMessage(Message message,String regId ) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.send(message,regId,0);//根据regID，发送消息到指定设备上，不重试。	     
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());	     
	}
	public static void sendMessages(List<TargetedMessage> messages) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.send(messages, 0); //根据alias，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	}

	public static void sendMessageToAlias(Message message, String alias) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =   sender.sendToAlias(message, alias, 0); //根据alias，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	}
	 //alias非空白，不能包含逗号，长度小于128。
	public static void sendMessageToAliases(Message message, List<String> aliasList) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     Result result =  sender.sendToAlias(message, aliasList, 0); //根据aliasList，发送消息到指定设备上，不重试。
	     log.debug("Server response: ", "MessageId: " + result.getMessageId()
                 + " ErrorCode: " + result.getErrorCode().toString()
                 + " Reason: " + result.getReason());
	}

	public static void sendBroadcast(Message message, String topic) throws Exception {
	     Constants.useOfficial();
	     Sender sender = new Sender(APP_SECRET_KEY);
	     sender.broadcast(message, topic, 0); //根据topic，发送消息到指定一组设备上，不重试。
	}
	public static void main(String args[]) {
	     String messagePayload = "This is a message";
	     String title = "notification title";
	     String description = "notification description";
		try {
			Message message = buildMessage(messagePayload, title, description);
			sendMessage(message, "1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
