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
 
package com.nimble.datachannel.producer.internal.custom.whirpoolfst;

import com.nimble.datachannel.producer.internal.custom.CsvProducer;
import com.nimble.datachannel.producer.internal.CustomProducer;
import com.nimble.datachannel.producer.internal.util.PropertiesLoader;
import com.nimble.datachannel.producer.internal.DcProducer;

import java.util.Properties;

/**
 * Example Producer for use case Whirpool in Nimble
 * @author a.musumeci
 */
public class WhirpoolProducer implements CustomProducer {
    Properties propsWhirpool = PropertiesLoader.loadProperties( "Whirpool" );

    /**
     *
     */
    public WhirpoolProducer() {
        super();
    }
    
    public String getLoginProducer() {
        return (String) propsWhirpool.get("loginProducer");
    }
    
    public String getPasswordProducer(){
        return (String) propsWhirpool.get("passwordProducer");
    }
    
    public boolean afterStartTopic(Properties producerProps){
        System.out.println("Start WhirpoolSystemInitializer");
        CsvProducer cvsProducer;
        int iSent;

        int idxDc=0;
        String channelName, channelKey, csvName;
        String csvPath = (String) propsWhirpool.get("dataCsvPath");
        while ( (channelName = (String) propsWhirpool.get("ChannelName"+idxDc)) != null) {
            try {
                DcProducer producer = new DcProducer( producerProps );
                long init = System.currentTimeMillis();
                System.out.println("");
                channelKey = (String) propsWhirpool.get("ChannelKey"+idxDc);
                csvName = (String) propsWhirpool.get("csvName"+idxDc);
                System.out.println("-----------------------------------------");
                System.out.println("Sending messages to "+channelName+ " from "+ csvName+"");
                System.out.println("-----------------------------------------");
                cvsProducer = new CsvProducer(producer, propsWhirpool, channelName, channelKey );
                iSent = cvsProducer.workCsv(csvPath+""+csvName);
                long end = System.currentTimeMillis();

                System.out.println("");
                System.out.println("-----------------------------------------");
                System.out.println("Sent "+iSent+" messages of "+channelName+ " from "+ csvName+" in ms "+(end-init));
                System.out.println("-----------------------------------------");
                producer.close();
            } catch (Exception ex) {
                System.out.println("ERROR "+ex.getMessage()+"");
                ex.printStackTrace();
            }
            idxDc++;
        }
        System.out.println("End WhirpoolSystemInitializer");
        return true;
    }
    
    
}
