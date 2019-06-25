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
 
package com.nimble.datachannel.producer.internal.util;

/**
 *
 * @author a.musumeci
 */


import java.util.*;

public class TestUtilityClass {
    
    public static void main (String argc[]) {
        //TestUtilityClass testObj = new TestUtilityClass();
        //testObj.testGetFieldFilterList(new Integer(1), new Integer(7), "IT_WHIRPOOL_STREAMS_DC_PRODUCTDATA");
        //testObj.testGetMetadataChannelList(new Integer(1), new Integer(7), "IT_WHIRPOOL_STREAMS_DC_PRODUCTDATA");
        //testObj.testLoginConsumer();
        StringBuilder sb = new StringBuilder(); 
        java.util.Map<String, String> env = System.getenv(); 
        for (String key : env.keySet()) { 
         sb.append(key + ": " + env.get(key)  + "\n"); 
        } 

        //System.out.println(sb.toString());
        
        System.out.println(System.getenv("user"));
        
        
        
        
    }
    
    
}
