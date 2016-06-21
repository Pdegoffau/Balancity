/*
 * Copyright 2016 Paul de Goffau.
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

package Balancity.data.edges;

import com.graphhopper.GraphHopper;
import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paul de Goffau
 */
public class Edges
{
    private HashMap<Integer, HashMap<Integer, EdgeAttributes>> edgeTableTD; //edgeId, (time, attr)
    private HashMap<Integer, String> edgePostcode; //edgeId,postCode
    private HashMap<Integer, HashMap<String,AreaAttributes>> areaTable; //time, (Postcode,attr)
    private HashMap<Integer, EdgeAttributes> edgeTableFixed;
    
    public Edges()
    {
        this.edgeTableTD = new HashMap<>();
        this.edgePostcode = new HashMap<>();
        this.areaTable = new HashMap<>();
        this.edgeTableFixed = new HashMap<>();
    }
    
    public void addEntry(int edgeId, int time, double duration, double speed, String vehicleType)
    {
        String pc = getPostCode(edgeId);
        if(!pc.equals(""))
        {
            if(!areaTable.containsKey(time))
            {
                    HashMap hm = new HashMap();
                    hm.put(pc,new AreaAttributes(duration*speed*0.00005,pc));
                    this.areaTable.put(time, hm);
                    HashMap em = new HashMap();
                    em.put(edgeId,new EdgeAttributes(duration*speed*0.001));
                    this.edgeTableTD.put(time,em);
            }
            else
            {
                if(!areaTable.get(time).containsKey(pc))
                {
                    ((HashMap) areaTable.get(time)).put(pc,new AreaAttributes(duration*speed*0.00005,pc));

                }            
                else
                {
                    AreaAttributes oldAttr = areaTable.get(time).get(pc);
                    areaTable.get(time).put(pc, new AreaAttributes(oldAttr.getFijnstof() + duration*speed*0.00005, pc));
                }

                if(!edgeTableTD.get(time).containsKey(edgeId))
                {
                    ((HashMap) edgeTableTD.get(time)).put(edgeId,new EdgeAttributes(duration*speed*0.001));
                }
                else
                {
                    EdgeAttributes oldAttr = edgeTableTD.get(time).get(edgeId);
                    edgeTableTD.get(time).put(edgeId, new EdgeAttributes(oldAttr.getFijnstof() +duration*speed*0.001));
                }
            }
        }
    }
    
    public double getTrafficCount(int time, int edgeId){
        if(edgeTableTD.containsKey(edgeId)){
            if(edgeTableTD.get(time).containsKey(time)){
                return edgeTableTD.get(edgeId).get(time).getTrafficCount();
            }
        }
        return 0.0;
    }
    
    
    public void setTrafficCount(int time, int edgeId, double trafficCount, String vehicleType){
        if (trafficCount > 0)
        {
            if (edgeTableTD.containsKey(edgeId))
            {
                ((HashMap) edgeTableTD.get(edgeId)).put(time, new EdgeAttributes(trafficCount, vehicleType));
            } else
            {
                HashMap<Integer, EdgeAttributes> hM = new HashMap();
                hM.put(time, new EdgeAttributes(trafficCount, vehicleType));
                edgeTableTD.put(edgeId, hM);
            }
        }
    }
    
    public void setPostcode(int edgeId, String PostCode)
    {
        this.edgePostcode.put(edgeId,PostCode);
    }
    
    public String getPostCode(int edgeId)
    {
        if(this.edgePostcode.containsKey(edgeId))
        {
            return this.edgePostcode.get(edgeId);
        }
        else
        {
            return "";
        }
    }
    
    public double getFijnstof(int edgeId, int time)
    {
        if(this.areaTable.containsKey(time))
        {
            if(this.areaTable.get(time).containsKey(getPostCode(edgeId)))
            {
                return this.areaTable.get(time).get(getPostCode(edgeId)).getFijnstof();
            }
        }
        return 0.0;
    }
    
    public double getFijnstof(int edgeId, int time, double edgeLength)
    {
        if(this.areaTable.containsKey(time))
        {
            if(this.areaTable.get(time).containsKey(getPostCode(edgeId)))
            {
                if(this.edgeTableTD.get(time).containsKey(edgeId))
                {
                    return this.areaTable.get(time).get(getPostCode(edgeId)).getFijnstof()*edgeLength/100+edgeTableTD.get(time).get(edgeId).getFijnstof();
                }
                else
                {
                    return this.areaTable.get(time).get(getPostCode(edgeId)).getFijnstof()*edgeLength/100;
                }
            }
        }
        return 0.0;
    }
    
    
    public void saveToTxt(String filepath, int startTime, int duration)
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(filepath, "UTF-8");
            for(int time = startTime; time<startTime+duration; time++)
            {
                if(this.areaTable.containsKey(time))
                {
                    Set<Entry<String,AreaAttributes>> selection = this.areaTable.get(time).entrySet();
                    for(Entry<String,AreaAttributes> item : selection)
                    {
                        writer.println(time + "; "+ item.getKey() +"; "+ ((AreaAttributes)item.getValue()).toString());
                    }
                }
            }
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Edges.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(Edges.class.getName()).log(Level.SEVERE, null, ex);
        }finally
        {
            writer.close();
        }
    }

    public void setSoundLevel( int edgeId, int soundLevel )
    {
        if(edgeTableFixed.containsKey(edgeId))
        {
            EdgeAttributes old = edgeTableFixed.get(edgeId);
            edgeTableFixed.put(edgeId, old.addSound(soundLevel));
        }
        else
        {
            edgeTableFixed.put(edgeId,new EdgeAttributes(soundLevel));
        }
    }
    
    public int getSoundLevel(int edgeId)
    {
        if(edgeTableFixed.containsKey(edgeId)){
            return edgeTableFixed.get(edgeId).getSoundLevel();
        }
        return 0;
    }

    public void loadSound( String filePath )
    {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(",");
                Balancity.Balancity.edgeInfo.setSoundLevel(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
            }
            
        } catch (FileNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        } 
    }
}
