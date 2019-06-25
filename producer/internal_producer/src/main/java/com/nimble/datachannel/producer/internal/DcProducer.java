/*
 * Copyright 2018 a.musumeci.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.nimble.datachannel.producer.internal;

import com.nimble.datachannel.producer.internal.json.FlatternManager;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 *
 * @author a.musumeci
 */
public class DcProducer implements AutoCloseable {
    Properties producerProps;
    ArrayList<String> avaiableTopics;
    KafkaProducer kProducer;
    boolean verifyGrantForTopic = false; //only for dev; this will be refactored for new datachannel
    long sleepProducerMsTime = 0;
    
    public DcProducer(Properties producerProps) {
        this(producerProps, null, false);
    }
    
    public DcProducer(Properties producerProps, ArrayList<String> avaiableTopics) {
        this(producerProps, avaiableTopics, avaiableTopics!=null);
    }
    
    public DcProducer(Properties producerProps, ArrayList<String> avaiableTopics, boolean verifyGrantForTopic) {
        this.producerProps = producerProps;
        sleepProducerMsTime = Long.parseLong(producerProps.getProperty("dc.producer.sleepMsAfterLastMessage"));
        System.out.println("Created a producer with sleepProducerMsTime "+sleepProducerMsTime);
        this.avaiableTopics = avaiableTopics;
        this.verifyGrantForTopic = verifyGrantForTopic;
        kProducer = new KafkaProducer<String, String>(producerProps);
    }
    
    private boolean canWriteIn(String topicName) {
        return (avaiableTopics==null || avaiableTopics.contains(topicName));
    }
    
    public void sendJsonMessage(String topicName, String key, String json) throws Exception {
        //precond: verify message
        json = FlatternManager.getFlatten(json);
        sendMessage(topicName, key, json);
    }
    public void sendDelimitedMessage(String topicName, String key, String delimited) throws Exception {
        sendMessage(topicName, key, delimited);
    }
    
    public void sendGenericMessage(String topicName, String key, Object value) throws Exception {
        sendMessage(topicName, key, value);
    }
    
    byte[] serialize(final Object obj) {
        return org.apache.commons.lang3.SerializationUtils.serialize((java.io.Serializable) obj);
    }
    private void sendMessage(String topicName, String key, Object value) throws Exception {
        
        if ( !verifyGrantForTopic || canWriteIn(topicName) ) {
             //kProducer.send(new ProducerRecord<>(topicName, serialize(key), serialize(value)));
             kProducer.send(new ProducerRecord<>(topicName, key, value));
             if (sleepProducerMsTime>0) {
                 try {
                     Thread.sleep(sleepProducerMsTime);
                     System.out.print("."+sleepProducerMsTime);
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }
             }
        } else throw new Exception("No grants to write in this topic");
    }

    public void close() {
        long init = System.currentTimeMillis();
        System.out.println("Closing a producer with sleepProducerMsTime "+sleepProducerMsTime);
        kProducer.flush();
        kProducer.close();
        System.out.println("Closed a producer with sleepProducerMsTime "+sleepProducerMsTime +" in "+ (System.currentTimeMillis()-init));
    }

}
