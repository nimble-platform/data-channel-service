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
 
package com.nimble.datachannel.producer.internal.custom.demo;

import com.google.gson.Gson;
import com.nimble.datachannel.producer.internal.CustomProducer;
import com.nimble.datachannel.producer.internal.util.PropertiesLoader;
import com.nimble.datachannel.producer.internal.DcProducer;

import java.util.Properties;

/**
 *  Example message producer over file Csv; this can be extended in order to read from database, from twitter topics, to listen remote events and to write them and so on
 * @author a.musumeci
 */
public class DemoJsonProducer implements CustomProducer {
    Properties propsDemo = PropertiesLoader.loadProperties( "DemoNimbleDc" );

    /**
     *
     */
    public DemoJsonProducer() {
        super();
    }
    
    public String getLoginProducer() {
        return (String) propsDemo.get("loginProducer");
    }
    
    public String getPasswordProducer(){
        return (String) propsDemo.get("passwordProducer");
    }
    
    public boolean afterStartTopic(Properties producerProps){
        DcProducer producer = new DcProducer( producerProps );

        System.out.println("Start Demo JsonProducer");
        int iSent;

        int idxDc=0;
        String channelName = (String) propsDemo.get("JsonChannelName");
        String maxMessages = (String) propsDemo.get("JsonMaxMessages");
        int iMaxMessages = Integer.parseInt(maxMessages);

        while ( channelName!= null && idxDc < iMaxMessages ) {
            try {
                
                    DemoAdvData demoAdvData = new DemoAdvData();

                    demoAdvData.setUrl("http://url.ext/id="+idxDc);
                    demoAdvData.setRevenue(idxDc*2000);
                    demoAdvData.setTotalPaid(idxDc*1400);
                    String reference = "REF_"+System.currentTimeMillis();
                    int code = (int) System.currentTimeMillis()/20000;
                    String media = "WEB";
                    
                    demoAdvData.getNestedInfo().setReference(reference);
                    demoAdvData.getNestedInfo().setCode(code);
                    demoAdvData.getNestedInfo().setMedia(media);
                    
                    System.out.println("producing "+demoAdvData);
                    producer.sendJsonMessage(channelName, System.currentTimeMillis()+"", new Gson().toJson(demoAdvData));
                    Thread.sleep(2000*(long) Math.random());
            } catch (Exception ex) {
                System.out.println("ERROR "+ex.getMessage()+"");
                ex.printStackTrace();
            }
            idxDc++;
        }
        producer.close();
        System.out.println("End DemoNimbleDcSystemInitializer");
        return true;
    }
    
    
}
