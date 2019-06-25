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

import com.google.gson.Gson;
import com.nimble.datachannel.producer.internal.CustomProducer;
import com.nimble.datachannel.producer.internal.util.PropertiesLoader;
import com.nimble.datachannel.producer.internal.DcProducer;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.Properties;

/**
 *  Example message producer over file Csv; this can be extended in order to read from database, from twitter topics, to listen remote events and to write them and so on
 * @author a.musumeci
 */
public class WhiRecyclerProducer implements CustomProducer {
    Properties propsDemo = PropertiesLoader.loadProperties( "WhiRecyclerProducer" );

    /**
     *
     */
    public WhiRecyclerProducer() {
        super();
    }
    
    public String getLoginProducer() {
        return (String) propsDemo.get("loginProducer");
    }
    
    public String getPasswordProducer(){
        return (String) propsDemo.get("passwordProducer");
    }


    private ArrayList<RecyclerData> produceData() {
        ArrayList data = new ArrayList();
                        RecyclerData recyclerData = new RecyclerData(null);

                        //1
                        String productGroup = "DishWasher";
                        String productType= "Whirlpool WFO 3T123 PF";
                        String serialNumber= "SN1547896314567";
                        String photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/859991021030_1000x1000_perspective.jpg";
                        String description="lavastoviglie Whirlpool: color inox. Classe energetica A++, per consumi ridotti di energia elettrica. Un utile timer digitale che segnala la fine del ciclo di lavaggio. Tecnologia innovativa che garantisce un funzionamento super silezioso, per un elettrodomestico senza rumori. Eccellente capacità di pulizia per risultati di lavaggio ideali.";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        recyclerData.setProcessDuration ( new LccData("Hours","0,3") );
                        recyclerData.setProcessCosts(  new LccData("€","15") );
                        recyclerData.setTransportCost ( new LccData("€","10") );
                        recyclerData.setEnergyConsumption( new LccData("kWh/kg","0,08") );
                        recyclerData.setDisposalCost( new LccData("€/kg","3") );
                        recyclerData.setQuantityIron ( new LccData("Kg","21,991") );
                        recyclerData.setQuantityPlastic(new LccData("Kg","14,186") );
                        recyclerData.setQuantityCopper ( new LccData("Kg","1,098") );
                        recyclerData.setQuantitySilver( new LccData("Kg","0,035") );
                        recyclerData.setQuantityGold( new LccData("Kg","0,0001") );
                        recyclerData.setQuantityPalladium ( new LccData("Kg","0,0099") );
                        recyclerData.setQuantityInox( new LccData("Kg","2,875") );
                        recyclerData.setQuantityAluminium( new LccData("Kg","1,053") );
                        recyclerData.setQuantityGlass( new LccData("Kg","0") );
                        recyclerData.setQuantityGas( new LccData("Kg","0") );
                        recyclerData.setQuantityOil( new LccData("Kg","0") );
                        recyclerData.setQuantityConcreteDisposed( new LccData("Kg","6,935") );
                        recyclerData.setQuantityWasteDisposed ( new LccData("Kg","2,355") );
                   
                        data.add(recyclerData);
                        
                        //2
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "DishWasher";
                        productType= "Whirlpool WFO 3P23 PL X";
                        serialNumber= "SNWHI007644";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/859991001730_1000x1000_frontal.jpg";
                        description="Lavastoviglie a libera installazione. Classe di efficienza energetica A++. Tecnologia 6° SENSO Power Clean Pro. 10 programmi. Capacità 15 coperti. Consumo d'acqua 11 l Tecnologia 6° SENSO Power Clean Pro. 10 programmi. Partenza ritardata. Emissione acustica 43 dB. Colore Acciaio Inox";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);


                        data.add(recyclerData);
                        
                        
                        //3
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "DishWasher";
                        productType= "Whirlpool WFO 3O32 P X";
                        serialNumber= "SN859991020920";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/859991020920_1000x1000_perspective.jpg";
                        description="Lavastoviglie a libera installazione. Classe di efficienza energetica A+++. Capacità 14 coperti. Consumo d'acqua 9 l. Tecnologia 6° SENSO Power Clean Pro. 10 programmi 3 temperature. Partenza ritardata. Emissione acustica 42 dB. Colore Acciaio Inox.";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        data.add(recyclerData);

                        //4
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Refrigerator";
                        productType= "WTH5244NFX";
                        serialNumber= "SN648534";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/WTH5244-NFX_850144611110_1000x1000.jpg";
                        description="Frigorifero Doppia Porta WTH5244NFX No Frost Classe A+ Capacità Lorda / Netta 532 / 515 Litri Colore Inox";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        recyclerData.setProcessDuration ( new LccData("Hours","0,3") );
                        recyclerData.setProcessCosts(  new LccData("€","15") );
                        recyclerData.setTransportCost ( new LccData("€","10") );
                        recyclerData.setEnergyConsumption( new LccData("kWh/kg","0,08") );
                        recyclerData.setDisposalCost( new LccData("€/kg","3") );
                        recyclerData.setQuantityIron ( new LccData("Kg","27,1200") );
                        recyclerData.setQuantityPlastic(new LccData("Kg","5,800") );
                        recyclerData.setQuantityCopper ( new LccData("Kg","1,7800") );
                        recyclerData.setQuantitySilver( new LccData("Kg","0") );
                        recyclerData.setQuantityGold( new LccData("Kg","0") );
                        recyclerData.setQuantityPalladium ( new LccData("Kg","0") );
                        recyclerData.setQuantityInox( new LccData("Kg","0") );
                        recyclerData.setQuantityAluminium( new LccData("Kg","1") );
                        recyclerData.setQuantityGlass( new LccData("Kg","0,2") );
                        recyclerData.setQuantityGas( new LccData("Kg","0,3") );
                        recyclerData.setQuantityOil( new LccData("Kg","0,25") );
                        recyclerData.setQuantityConcreteDisposed( new LccData("Kg","7,6") );
                        recyclerData.setQuantityWasteDisposed ( new LccData("Kg","0,95") );
                   
                        data.add(recyclerData);
                        
                        //5
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Refrigerator";
                        productType= "WTNF82OMXH1";
                        serialNumber= "SN8378372";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/859991549840_1000x1000_closed.jpg";
                        description="Frigorifero: Combinato Classe di efficienza energetica: A ++ Capacità netta frigo: 234 l Capacità netta congelatore: 104 l Consumo energia annuo: 271 kWh/anno Sistema di raffreddamento frigo: No Frost Colore: Inox Mirror";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        data.add(recyclerData);
                        
                        
                        
                        //6
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Refrigerator";
                        productType= "TTNF8212OX";
                        serialNumber= "SNp-722590";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/850509811080_1000x1000_closed.jpg";
                        description="Frigorifero: Doppia porta. Classe di efficienza energetica: A ++. Capacità netta frigo: 322 l. Capacità netta congelatore: 101 l. Consumo energia annuo: 296 kWh/anno. Sistema di raffreddamento frigo: No Frost. Colore: Inox.";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        data.add(recyclerData);
                        
                        //7
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Air conditioner";
                        productType= "COPR3S-SAI18K39DC2";
                        serialNumber= "SNSAI18K39DC2";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/853120201060_1000x1000_perspective.jpg";
                        description="3D Cool Wifi Pro 1.5 Ton, 3 Star Inverter Air Conditioner (Copper)";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        recyclerData.setProcessDuration ( new LccData("Hours","0,3") );
                        recyclerData.setProcessCosts(  new LccData("€","15") );
                        recyclerData.setTransportCost ( new LccData("€","10") );
                        recyclerData.setEnergyConsumption( new LccData("kWh/kg","0,08") );
                        recyclerData.setDisposalCost( new LccData("€/kg","3") );
                        recyclerData.setQuantityIron ( new LccData("Kg","17,893") );
                        recyclerData.setQuantityPlastic(new LccData("Kg","9,9991") );
                        recyclerData.setQuantityCopper ( new LccData("Kg","5,0457") );
                        recyclerData.setQuantitySilver( new LccData("Kg","0") );
                        recyclerData.setQuantityGold( new LccData("Kg","0") );
                        recyclerData.setQuantityPalladium ( new LccData("Kg","0") );
                        recyclerData.setQuantityInox( new LccData("Kg","0") );
                        recyclerData.setQuantityAluminium( new LccData("Kg","2,5811") );
                        recyclerData.setQuantityGlass( new LccData("Kg","0") );
                        recyclerData.setQuantityGas( new LccData("Kg","0") );
                        recyclerData.setQuantityOil( new LccData("Kg","0,4522") );
                        recyclerData.setQuantityConcreteDisposed( new LccData("Kg","0,1306") );
                        recyclerData.setQuantityWasteDisposed ( new LccData("Kg","0,8944") );
                   
                        data.add(recyclerData);
                        
                        //8
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Air conditioner";
                        productType= "COPR3S-SAI18K39DC1";
                        serialNumber= "SNSAI18K39DC1";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/853120201060_1000x1000_perspective.jpg";
                        description="3D Cool Purafresh Pro 1.5 Ton, 3 Star Inverter Air Conditioner (Copper)";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        data.add(recyclerData);
                        
                        //9
                        recyclerData = new RecyclerData(recyclerData);
                        productGroup = "Air conditioner";
                        productType= "COPR3S-SAI22B39MC0";
                        serialNumber= "SNSAI22B39MC0";
                        photo="https://www.whirlpool.it/digitalassets/Picture/web1000x1000/853120201060_1000x1000_perspective.jpg";
                        description="Magicool Pro 2 Ton, 3 Star Inverter Air Conditioner (Copper) ";

                        recyclerData.setSerialNumber(serialNumber);
                        recyclerData.setProductGroup(productGroup);
                        recyclerData.setProductType(productType);
                        recyclerData.setPhoto(photo);
                        recyclerData.setDescription(description);

                        data.add(recyclerData);
                        
                        
                        
                        
        return data;
    }

    
    public boolean afterStartTopic(Properties producerProps){
        DcProducer producer = new DcProducer( producerProps );

        System.out.println("Start WhiRecyclerProducer");
        int iSent;

        int idxDc=0;
        String channelName = "IT_WHIRPOOL_RECYCLERDATABAL";
        
        ArrayList<RecyclerData> data = produceData();
        Iterator iter = data.iterator();
        while (iter.hasNext()) {
        
                       RecyclerData recyclerData = (RecyclerData) iter.next();

                        for ( int i=0; i<10; i++ ) {
                            try {
                        String sn = recyclerData.getSerialNumber();
                        recyclerData.setSerialNumber(recyclerData.getSerialNumber()+"-"+i);
                        System.out.println(""+recyclerData.getProductGroup() + ";"+recyclerData.getProductType() + ";"+recyclerData.getSerialNumber() + ";"+recyclerData.getPhoto());
                        LCCInputData inputData = new LCCInputData();
                        inputData.setSerialNumberProduct(recyclerData.getSerialNumber());
                        inputData.setJsonProduct( new Gson().toJson(recyclerData) );
                        producer.sendGenericMessage(channelName, recyclerData.getSerialNumber(), new Gson().toJson(inputData) );
                        recyclerData.setSerialNumber(sn);

                        Thread.sleep(500*(long) Math.random());
            } catch (Exception ex) {
                System.out.println("ERROR "+ex.getMessage()+"");
                ex.printStackTrace();
            }

                            
            }     
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            
                            idxDc++;
        }
        producer.close();
        System.out.println("End WhiRecyclerProducer");
        return true;
    }
    
    
}
