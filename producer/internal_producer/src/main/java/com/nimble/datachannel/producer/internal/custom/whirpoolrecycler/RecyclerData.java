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
package com.nimble.datachannel.producer.internal.custom.whirpoolrecycler;

import java.util.*;

/**
 *
 * @author a.musumeci
 */
public class RecyclerData {
    String productGroup;
    String productType;
    String serialNumber;
    String photo;
    String description;
    LccData processDuration;
    LccData processCosts;
    LccData transportCost;
    LccData energyConsumption;
    LccData disposalCost;
    LccData quantityIron;
    LccData quantityPlastic;
    LccData quantityCopper;
    LccData quantitySilver;
    LccData quantityGold;
    LccData quantityPalladium;
    LccData quantityInox;
    LccData quantityAluminium;
    LccData quantityGlass;
    LccData quantityGas;
    LccData quantityOil;

    public LccData getQuantityGlass() {
        return quantityGlass;
    }

    public void setQuantityGlass(LccData quantityGlass) {
        this.quantityGlass = quantityGlass;
    }

    public LccData getQuantityGas() {
        return quantityGas;
    }

    public void setQuantityGas(LccData quantityGas) {
        this.quantityGas = quantityGas;
    }

    public LccData getQuantityOil() {
        return quantityOil;
    }

    public void setQuantityOil(LccData quantityOil) {
        this.quantityOil = quantityOil;
    }
    LccData quantityConcreteDisposed;
    LccData quantityWasteDisposed;

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LccData getProcessDuration() {
        return processDuration;
    }

    public void setProcessDuration(LccData processDuration) {
        this.processDuration = processDuration;
    }

    public LccData getProcessCosts() {
        return processCosts;
    }

    public void setProcessCosts(LccData processCosts) {
        this.processCosts = processCosts;
    }

    public LccData getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(LccData transportCost) {
        this.transportCost = transportCost;
    }

    public LccData getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(LccData energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public LccData getDisposalCost() {
        return disposalCost;
    }

    public void setDisposalCost(LccData disposalCost) {
        this.disposalCost = disposalCost;
    }

    public LccData getQuantityIron() {
        return quantityIron;
    }

    public void setQuantityIron(LccData quantityIron) {
        this.quantityIron = quantityIron;
    }

    public LccData getQuantityPlastic() {
        return quantityPlastic;
    }

    public void setQuantityPlastic(LccData quantityPlastic) {
        this.quantityPlastic = quantityPlastic;
    }

    public LccData getQuantityCopper() {
        return quantityCopper;
    }

    public void setQuantityCopper(LccData quantityCopper) {
        this.quantityCopper = quantityCopper;
    }

    public LccData getQuantitySilver() {
        return quantitySilver;
    }

    public void setQuantitySilver(LccData quantitySilver) {
        this.quantitySilver = quantitySilver;
    }

    public LccData getQuantityGold() {
        return quantityGold;
    }

    public void setQuantityGold(LccData quantityGold) {
        this.quantityGold = quantityGold;
    }

    public LccData getQuantityPalladium() {
        return quantityPalladium;
    }

    public void setQuantityPalladium(LccData quantityPalladium) {
        this.quantityPalladium = quantityPalladium;
    }

    public LccData getQuantityInox() {
        return quantityInox;
    }

    public void setQuantityInox(LccData quantityInox) {
        this.quantityInox = quantityInox;
    }

    public LccData getQuantityAluminium() {
        return quantityAluminium;
    }

    public void setQuantityAluminium(LccData quantityAluminium) {
        this.quantityAluminium = quantityAluminium;
    }

    public LccData getQuantityConcreteDisposed() {
        return quantityConcreteDisposed;
    }

    public void setQuantityConcreteDisposed(LccData quantityConcreteDisposed) {
        this.quantityConcreteDisposed = quantityConcreteDisposed;
    }

    public LccData getQuantityWasteDisposed() {
        return quantityWasteDisposed;
    }

    public void setQuantityWasteDisposed(LccData quantityWasteDisposed) {
        this.quantityWasteDisposed = quantityWasteDisposed;
    }

    @Override
    public String toString() {
        return "RecyclerData{" + "productGroup=" + productGroup + ", productType=" + productType + ", serialNumber=" + serialNumber + ", photo=" + photo + ", description=" + description + ", processDuration=" + processDuration + ", processCosts=" + processCosts + ", transportCost=" + transportCost + ", energyConsumption=" + energyConsumption + ", disposalCost=" + disposalCost + ", quantityIron=" + quantityIron + ", quantityPlastic=" + quantityPlastic + ", quantityCopper=" + quantityCopper + ", quantitySilver=" + quantitySilver + ", quantityGold=" + quantityGold + ", quantityPalladium=" + quantityPalladium + ", quantityInox=" + quantityInox + ", quantityAluminium=" + quantityAluminium + ", quantityConcreteDisposed=" + quantityConcreteDisposed + ", quantityWasteDisposed=" + quantityWasteDisposed + '}';
    }

    public RecyclerData( RecyclerData other) {
        if (other == null) return;
        this.setDescription(other.description);
        this.setDisposalCost(other.disposalCost);
        this.setEnergyConsumption(other.energyConsumption);
        this.setPhoto(other.photo);
        this.setProcessCosts(other.processCosts);
        this.setProcessCosts(other.processCosts);
        this.setProcessDuration(other.processDuration);
        this.setProductGroup(other.productGroup);
        this.setProductType(other.productType);
        this.setQuantityAluminium(other.quantityAluminium);
        this.setQuantityConcreteDisposed(other.quantityConcreteDisposed);
        this.setQuantityCopper(other.quantityCopper);
        this.setQuantityGas(other.quantityGas);
        this.setQuantityGold(other.quantityGold);
        this.setQuantityInox(other.quantityInox);
        this.setQuantityOil(other.quantityOil);
        this.setQuantityIron(other.quantityIron);
        this.setQuantityPalladium(other.quantityPalladium);
        this.setQuantityPlastic(other.quantityPlastic);
        this.setQuantitySilver(other.quantitySilver);
        this.setQuantityWasteDisposed(other.quantityWasteDisposed);
        this.setSerialNumber(other.serialNumber);
        this.setTransportCost(other.transportCost);
    }

    
    
}
