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

import com.nimble.datachannel.producer.internal.CustomProducer;
import com.nimble.datachannel.producer.internal.util.PropertiesLoader;

import com.nimble.datachannel.producer.internal.DcProducer;
import java.util.*;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 *
 * @author a.musumeci
 */
class NimbleInternalDataChannelProducerManager {
    
    int executeCustomProducer() {
            
            Properties systemProps = PropertiesLoader.loadProperties();
            String producerPropertiesfile = systemProps.getProperty("dc.topic.producer.propertiesfile");
            System.out.println("producerPropertiesfile="+producerPropertiesfile);
            Properties producerProps = PropertiesLoader.loadProperties(producerPropertiesfile);
            
            String customSystemInitializerName;
            int idxCs=0;
            while ( (customSystemInitializerName = (String) systemProps.get("dc.custom.CustomProducer"+idxCs)) != null) {
                try {
                    
                    //are you sure your kafka 
                    CustomProducer customSystemInitializer = (CustomProducer) Class.forName(customSystemInitializerName).newInstance();
                           Thread threadCustomizer = new Thread(){
                                public void run(){
                                  System.out.println("Executing customSystemInitializer in new Thread");
                                  customSystemInitializer.afterStartTopic(producerProps);
                                }
                            };
                        threadCustomizer.start();
                    
                } catch (Exception ex) {
                    System.out.println("FAILED "+customSystemInitializerName);
                    ex.printStackTrace();
                }

                idxCs++;
            }
    
            return idxCs;
    
    }
    
    public static void main (String argc[]) {
            NimbleInternalDataChannelProducerManager customProducerManager = new NimbleInternalDataChannelProducerManager();
            customProducerManager.executeCustomProducer();
    }
    
    
}
