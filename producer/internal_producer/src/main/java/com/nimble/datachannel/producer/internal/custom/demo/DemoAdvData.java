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

/**
 *
 * @author a.musumeci
 */
public class DemoAdvData {
    String url;
    long totalPaid, revenue;
    private DemoAdvNested nestedInfo = new DemoAdvNested();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "DemoAdvData{" + "url=" + url + ", totalPaid=" + totalPaid + ", revenue=" + revenue + "', nestedInfo=" + nestedInfo.toString() +"}";
    }

    public DemoAdvNested getNestedInfo() {
        return nestedInfo;
    }

    public void setNestedInfo(DemoAdvNested nestedInfo) {
        this.nestedInfo = nestedInfo;
    }

    
    
}
