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
    private int MAX_TIME= 0;
    private int NUM_OF_HOURS = 28;

    public TrafficData()
    {
        MAX_TIME = (NUM_OF_HOURS*60*60)/Balancity.Balancity.FRAMEWIDTH; //Total simulation time in seconds divided by framewidth
    }

    public void saveTrafficToTextFile( BalanceHopper graphhopper, String filepath )
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(filepath, "UTF-8");
            AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
            double tfCount = 0.0;
            QueryGraph qg = new QueryGraph(graphhopper.getGraphHopperStorage().getBaseGraph());
            NodeAccess pa = qg.getNodeAccess();
            while (iter.next())
            {
                PointList pl = iter.fetchWayGeometry(1);
                for (int time = 0; time < MAX_TIME; time = time + 1)
                {
                    tfCount = iter.getTrafficCount(time);
                    if (tfCount > 0)
                        for (int j = 0; j < (pl.size() - 1); j++)
                        {
                            writer.println(pl.getLat(j) + ";" + pl.getLon(j) + ";" + pl.getLat(j + 1) + ";" + pl.getLon(j + 1) + ";" + time + ";" + tfCount);
                        }
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

    public void saveTrafficToJSON( BalanceHopper graphhopper, String filepath )
    {
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(filepath, "UTF-8");
            writer.println("{\"traffic\": [");
            AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
            double tfCount = 0.0;
            QueryGraph qg = new QueryGraph(graphhopper.getGraphHopperStorage().getBaseGraph());
            int counter = 0;
            while (iter.next())
            {
                PointList pl = iter.fetchWayGeometry(1);
                for (int time = 0; time < MAX_TIME; time = time + 1)
                {
                    tfCount = iter.getTrafficCount(time);
                    if (tfCount > 0)
                        for (int j = 0; j < (pl.size() - 1); j++)
                        {
                            if (counter > 0)
                            {
                                writer.print(",\n");
                            }
                            writer.print("{\"latFrom\":" + pl.getLat(j) + ",\"lonFrom\":" + pl.getLon(j) + ",\"latTo\":" + pl.getLat(j + 1) + ",\"lonTo\":" + pl.getLon(j + 1) + ",\"time\":" + time + ",\"trafficCount\":" + tfCount + "}");
                            counter++;
                        }
                }
            }
            writer.println("\n]}");
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

    public HashMap retrieveData( BalanceHopper hopper )
    {
        AllEdgesIterator iter = hopper.getGraphHopperStorage().getAllEdges();
        double tfCount = 0;
        HashMap drop = new HashMap<Integer, ArrayList<TrafficEntry>>();
        while (iter.next())
        {
            PointList pl = iter.fetchWayGeometry(1);
            for (int time = 0; time < MAX_TIME; time = time + 1)
            {
                tfCount = iter.getTrafficCount(time);
                if (tfCount > 0)
                {
                    ArrayList<TrafficEntry> additions;
                    if(drop.containsKey(time)){
                        additions = (ArrayList<TrafficEntry>) drop.get(time);
                    }
                    else
                        additions = new ArrayList<TrafficEntry>();

                    for (int j = 0; j < (pl.size() - 1); j++)
                    {
                        additions.add(new TrafficEntry(pl.getLat(j), pl.getLon(j), pl.getLat(j + 1), pl.getLon(j + 1), tfCount));
                    }
                    drop.put(time, additions);
                }
            }
        }
        return drop;
    }

    public void saveTrafficOrderedByTime( BalanceHopper graphhopper, String filepath )
    {
        HashMap data = retrieveData(graphhopper);
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(filepath, "UTF-8");
            writer.println("{\"traffic\": [");
            for (int j = 0; j < data.size(); j++)
            {
                if(data.containsKey(j))
                {
                    writer.println("{\"time\": [");
                    ArrayList<TrafficEntry> atTime = (ArrayList<TrafficEntry>)data.get(j);
                    for (int i = 0; i < atTime.size(); i++)
                    {
                        TrafficEntry entry = atTime.get(i);
                        if (i != atTime.size() - 1)
                        {
                            writer.println("{\"time\":"+j+", \"latFrom\":" + entry.getLatFrom() + ",\"lonFrom\":" + entry.getLonFrom() + ",\"latTo\":" + entry.getLatTo() + ",\"lonTo\":" + entry.getLonTo() + ",\"trafficCount\":" + entry.getTrafficCount() + "},");
                        } else
                        {
                            writer.println("{\"time\":"+j+", \"latFrom\":" + entry.getLatFrom() + ",\"lonFrom\":" + entry.getLonFrom() + ",\"latTo\":" + entry.getLatTo() + ",\"lonTo\":" + entry.getLonTo() + ",\"trafficCount\":" + entry.getTrafficCount() + "}");
                        }
                    }

                    if (j != data.size() - 1)
                    {
                        writer.println("]},");
                    } else
                    {
                        writer.println("]}");
                    }
                }

            }
            writer.println("\n]}");
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
