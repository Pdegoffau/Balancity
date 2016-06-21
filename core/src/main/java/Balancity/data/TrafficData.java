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
package Balancity.data;

import Balancity.BalanceHopper;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.AllEdgesIterator;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.PointList;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to load and save traffic data
 * <p>
 * @author Paul de Goffau
 */
public class TrafficData
{
    private int MAX_TIME = 0;
    private int NUM_OF_HOURS = 28;

    public TrafficData()
    {
        MAX_TIME = (NUM_OF_HOURS * 60 * 60) / Balancity.Balancity.FRAMEWIDTH; //Total simulation time in seconds divided by framewidth
    }

    public void saveTrafficOrderedByTime( BalanceHopper graphhopper, String filepath )
    {
        PrintWriter times = null;
        try
        {
            PrintWriter writer = null;
            times = new PrintWriter(filepath+"times.json","UTF-8");
            times.println("{\"times\": [");
            boolean firstTime = true;
            for (int time = 0; time < MAX_TIME; time = time + 1)
            {
                try
                {
                    writer = new PrintWriter(filepath+""+time+".json", "UTF-8");
                    boolean first = true;
                    boolean added = false;
                    boolean timeExists = false;
                    boolean addedHeader = false;
                    AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
                    while (iter.next())
                    {
                        double tfc = Balancity.Balancity.edgeInfo.getFijnstof(iter.getEdge(), time, iter.getDistance());
                        if (tfc > 0)
                        {
                            if(firstTime)
                            {
                                times.print("{\"time\": "+ time+"}");
                                firstTime = false;
                                timeExists = true;
                            }
                            else{
                                if(!timeExists){
                                    times.print(",\n");
                                    times.print("{\"time\": "+ time+"}");
                                    timeExists =true;
                                }
                            }
                            
                            if (!addedHeader)
                            {
                                if (!first)
                                {
                                    writer.println("\n]},");
                                }
                                writer.println("{\"traffic\": [");
                                first = false;
                                addedHeader = true;
                            }
                            
                            PointList pl = iter.fetchWayGeometry(1);
                            if (added && pl.size() > 1)
                            {
                                writer.print(",\n");
                                added = false;
                            }
                            for (int j = 0; j < (pl.size() - 1); j++)
                            {
                                if (j != (pl.size() - 2))
                                {
                                    writer.println("\t\t{\"time\":" + time + ", \"latFrom\":" + pl.getLat(j) + ",\"lonFrom\":" + pl.getLon(j) + ",\"latTo\":" + pl.getLat(j + 1) + ",\"lonTo\":" + pl.getLon(j + 1) + ",\"trafficCount\":" + tfc + "},");
                                } else
                                {
                                    writer.print("\t\t{\"time\":" + time + ", \"latFrom\":" + pl.getLat(j) + ",\"lonFrom\":" + pl.getLon(j) + ",\"latTo\":" + pl.getLat(j + 1) + ",\"lonTo\":" + pl.getLon(j + 1) + ",\"trafficCount\":" + tfc + "}");
                                    added = true;
                                }
                            }
                        }
                    }
                    writer.println("\n]}");
                    writer.close();
                } catch (FileNotFoundException ex)
                {
                    Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex)
                {
                    Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex)
                {
                    Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            times.println("\n]}");
            times.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(TrafficData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
