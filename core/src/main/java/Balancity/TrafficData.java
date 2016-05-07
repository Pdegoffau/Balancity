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

package Balancity;

import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.storage.NodeAccess;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Class to load and save traffic data
 * @author Paul de Goffau
 */
public class TrafficData
{
    private int MAX_TIME = 3000;
    public TrafficData(){
        
    }
    
    public void saveTrafficToFile(BalanceHopper graphhopper, String filepath){
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(filepath, "UTF-8");
            AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
            int tfCount =0;
            QueryGraph qg = new QueryGraph(graphhopper.getGraphHopperStorage().getBaseGraph());
            NodeAccess pa = qg.getNodeAccess();
            while(iter.next())
            {
                    double latf= pa.getLat(iter.getBaseNode());
                    double lonf = pa.getLon(iter.getBaseNode());
                    double latt = pa.getLat(iter.getAdjNode());
                    double lont = pa.getLon(iter.getAdjNode());
                for(int time=0;time<MAX_TIME; time= time+1)
                {
                    tfCount = iter.getTrafficCount(time);
                    if(tfCount>0)
                    writer.println(latf+";"+lonf+";"+latt+";"+lont+";"+time+";"+tfCount);
                }
            }
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            writer.close();
        }
    }
}
