package com.qihao.toy.biz.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.dom4j.DocumentException;

import cn.teaey.apns4j.keystore.KeyStoreHelper;
import cn.teaey.apns4j.keystore.KeyStoreWraper;
import cn.teaey.apns4j.network.AppleGateway;
import cn.teaey.apns4j.network.AppleNotificationServer;
import cn.teaey.apns4j.network.SecurityConnection;
import cn.teaey.apns4j.network.SecuritySocketFactory;
import cn.teaey.apns4j.protocol.NotifyPayload;

import com.alibaba.fastjson.JSON;
import com.qihao.toy.dal.domain.UserDO;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
/**
 * Created with IntelliJ IDEA.
 * User: luqiao
 * Date: 15-9-15
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class MessageProvider {
	@Setter @Getter
    private String  iosPushCertificatePath = "PushChat.p12";
	@Setter @Getter
    private String  iosPushCertificatePassword = "8910189101";
    @Setter @Getter
    private Boolean isDevlopment	= AppleGateway.ENV_DEVELOPMENT;
    @Setter @Getter
	private String miPushPackageName = "box.qihao.com.qihaohao";
    @Setter @Getter
	private String miPushAppSecrtKey = "eGZnHPky5HHDXGwN1+ON9Q==";
    
    
    private SecuritySocketFactory connectionFactory = null;
    Sender sender = null;
	public void init() throws Exception {
		//init apple anps
    	InputStream in = getClass().getClassLoader().getResourceAsStream(iosPushCertificatePath);
    	//InputStream in = getClass().getResourceAsStream(iosPushCertificatePath);
        KeyStoreWraper keyStoreWrapper = KeyStoreHelper.getKeyStoreWraper(in, iosPushCertificatePassword);
	    AppleNotificationServer appleNotificationServer = AppleNotificationServer.get(AppleGateway.ENV_DEVELOPMENT);
        connectionFactory = SecuritySocketFactory.Builder.newBuilder().appleServer(appleNotificationServer).keyStoreWrapper(keyStoreWrapper).build();	
        //init miPush
        Constants.useOfficial();
        sender = new Sender(miPushAppSecrtKey);
        
	}  

    /**
     * 构造推送消息体
     * @param title
     * @param description
     * @param commandMessage
     * @return
     * @throws Exception
     */
	public Message buildMessage(String title, String description,Object commandMessage) throws Exception {
	     Message message = new Message.Builder()
	             .title(title)
	             .description(description)
	             .payload(JSON.toJSONString(commandMessage))
	             .restrictedPackageName(miPushPackageName)
	             .passThrough(1)  //消息使用透传方式
	             .notifyType(1)  // 使用默认提示音提示
	             .build();
	     return message;
	}
	public void sendMessage(Message message,UserDO.DeviceType deviceType, String deviceToken ) throws Exception {
		if(deviceType.equals(UserDO.DeviceType.Android)) {
		     Result result =  sender.sendToAlias(message,deviceToken,0);//根据regID，发送消息到指定设备上，不重试。	     
		     log.debug("Server response: ", "MessageId: " + result.getMessageId()
	                + " ErrorCode: " + result.getErrorCode().toString()
	                + " Reason: " + result.getReason());
		}else {
			try {
		        SecurityConnection connection = SecurityConnection.newSecurityConnection(connectionFactory);
		        NotifyPayload notifyPayload = NotifyPayload.newNotifyPayload();
		        notifyPayload.alert(message.getTitle());
		        notifyPayload.badge(2);
		        notifyPayload.alertBody(message.getPayload());
		        notifyPayload.alertActionLocKey(message.getDescription());
		        //notifyPayload.sound("default");
		        //notifyPayload.alertBody("Pushed By \\\" apns4j");
		        //notifyPayload.alertActionLocKey("Button Text");
		
				connection.sendAndFlush(deviceToken, notifyPayload);
		        connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException,
	DocumentException {
		MessageProvider provider = new MessageProvider();
		try {
			provider.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message message;
		try {
			message = provider.buildMessage("同志", null, "3333333333");
			provider.sendMessage(message, UserDO.DeviceType.Android,"fffffffff");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
