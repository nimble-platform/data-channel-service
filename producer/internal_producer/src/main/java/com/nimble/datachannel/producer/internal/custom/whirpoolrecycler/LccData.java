/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nimble.datachannel.producer.internal.custom.whirpoolrecycler;

/**
 *
 * @author a.musumeci
 */
public class LccData {
    String unitOfMeasure;
    String value;

    public LccData( String unitOfMeasure, String value   ) {
        this.unitOfMeasure=unitOfMeasure;
        this.value=value;
    }
    
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LccData{" + "unitOfMeasure=" + unitOfMeasure + ", value=" + value + '}';
    }
    
}
