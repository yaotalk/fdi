package com.minivision.fdi.mqtt;

import java.io.IOException;

import com.minivision.fdi.domain.QrRecMsg;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minivision.fdi.domain.FaceRecMsg;
import com.minivision.fdi.mqtt.protocol.Packet;
import com.minivision.fdi.mqtt.protocol.SecurityUtil;
import com.minivision.fdi.mqtt.protocol.Packet.Head;
import com.minivision.fdi.mqtt.protocol.Packet.Head.Type;

public class MqttSimulator {
  public static final String HOST = "tcp://localhost:1883";
  
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private static final String clientid = "8";

  private MqttClient client;
  private String userName = "8";
  private String passWord = "pad::8";


  public MqttSimulator() throws MqttException {
    client = new MqttClient(HOST, clientid, new MemoryPersistence());
    connect();
  }

  private void connect() {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(false);
    options.setUserName(userName);
    SecurityUtil passwordUtil = new SecurityUtil("SHA-256", "minivision");
    options.setPassword(passwordUtil.encode(passWord).toCharArray());
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(20);
    
    try {
      client.setCallback(new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message)
            throws Exception {
          System.out.println("receive a message on : "+ topic);
          System.out.println("payload : " + new String(message.getPayload()));
          Packet<?> packet = MAPPER.readValue(message.getPayload(), Packet.class);
          Head head = packet.getHead();
          head.setType(Type.RESPONSE_OK);
          Packet<Void> rePacket = new Packet<>();
          rePacket.setHead(head);
          publishObject("/s/pad", rePacket);
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
          System.out.println("deliveryComplete : messageId = "+ token.getMessageId());
        }
        @Override
        public void connectionLost(Throwable paramThrowable) {

        }
      });
      client.connect(options);
      client.subscribe("/d/pad/"+clientid);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void publish(String topic, MqttMessage message)
      throws MqttPersistenceException, MqttException {
    client.publish(topic, message);
  }
  
  public void publishObject(String topic, Object object)
      throws MqttPersistenceException, MqttException, JsonProcessingException {
    MqttMessage message = new MqttMessage();
    message.setPayload(MAPPER.writeValueAsBytes(object));
    client.publish(topic, message);
  }
  
  public static void main(String[] args) throws MqttException, IOException, InterruptedException {
    MqttSimulator simulator = new MqttSimulator();
    /*Status status = new Status();
    status.setCpu(33.33f);
    status.setMem(44.44f);
    status.setTimestamp(new Date());*/
    
//    FaceRecMsg faceRegMsg = new FaceRecMsg();
//    faceRegMsg.setMeetingId("8ebaa40a-4e27-4002-81ff-7ee27c16727c");
//    faceRegMsg.setTimestamp(System.currentTimeMillis());
//    faceRegMsg.setFeature("321");

    QrRecMsg recMsg = new QrRecMsg();
    recMsg.setMeetingId("8ebaa40a-4e27-4002-81ff-7ee27c16727c");
    recMsg.setQrCode("111111");
    recMsg.setTimestamp(System.currentTimeMillis());
    
    Head head = new Head(10000, 109, Type.REQUEST);
    
    Packet<?> packet = new Packet<>(head, recMsg);
    
    simulator.publishObject("/s/pad", packet);
    
  }
}
