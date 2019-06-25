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

import com.nimble.datachannel.producer.internal.custom.CsvProducer;
import com.nimble.datachannel.producer.internal.CustomProducer;
import com.nimble.datachannel.producer.internal.util.PropertiesLoader;
import com.nimble.datachannel.producer.internal.DcProducer;

import java.util.Properties;

/**
 *  Example message producer over file Csv; this can be extended in order to read from database, from twitter topics, to listen remote events and to write them and so on
 * @author a.musumeci
 */
public class DemoProducer implements CustomProducer {
    Properties propsDemo = PropertiesLoader.loadProperties( "DemoNimbleDc" );

    /**
     *
     */
    public DemoProducer() {
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

        System.out.println("Start DemoNimbleDc SystemInitializer");
        CsvProducer cvsProducer;
        int iSent;

        int idxDc=0;
        String channelName, channelKey, csvName;
        String csvPath = (String) propsDemo.get("dataCsvPath");
        while ( (channelName = (String) propsDemo.get("ChannelName"+idxDc)) != null) {
            try {
                channelKey = (String) propsDemo.get("ChannelKey"+idxDc);
                csvName = (String) propsDemo.get("csvName"+idxDc);
                cvsProducer = new CsvProducer(producer, propsDemo, channelName, channelKey );
                iSent = cvsProducer.workCsv(csvPath+""+csvName);
                System.out.println("Sent "+iSent+" messages of "+channelName);
            } catch (Exception ex) {
                System.out.println("ERROR "+ex.getMessage()+"");
                ex.printStackTrace();
            }
            idxDc++;
        }
        producer.close();
        System.out.println("End DemoProducer");
        return true;
    }
    
    
}
