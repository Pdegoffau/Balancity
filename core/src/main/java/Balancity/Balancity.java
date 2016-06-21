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

import Balancity.Simulation.SimulationSetup;
import Balancity.Simulation.VehicleUnit;
import Balancity.data.*;
import Balancity.data.edges.Edges;
import com.graphhopper.*;
import com.graphhopper.routing.util.*;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Paul de Goffau
 */
public class Balancity
{
    
    public static int FRAMEWIDTH = 60;
    public static String POSTCODE_CENTERS_FILE = "files/traffic-generation/PostcodeCenters.txt";
    public static String ALL_POSTCODES_FILE = "files/traffic-generation/pcode.txt";
    public static String EDGE_TO_POSTCODE_FILE = "files/environment/edgeToPostcode.txt";
    public static String EDGE_TO_SOUND_FILE = "files/environment/edgeToSound.txt";
    public static String POSCODE_POPULATION_FILE ="files/environment/postCodePopulation.txt";
    public static Edges edgeInfo;

    public static void main( String[] args ) throws IOException
    {
        String ovin = "files/traffic-generation/OViN2014_Databestand.csv";
        String pcData = "balancity-web/testfiles/fijnstof.txt";
        String savedInstance = "balancity-web/testfiles/test/instance.txt";
        String ghLoc = "target/balancity";
        String osmAmsterdam = "Amsterdam.osm.pbf";
        String testGPX = "balancity-web/testfiles/test";
        //String trafficTxt = "balancity-web/trafficData.txt";
        //String trafficJSON = "balancity-web/traffic.json";
        String trafficJSONTime =  "balancity-web/orderedTraffic";
        
        //AveragedPostCode pc = new AveragedPostCode(postcodes);

        BalanceHopper hopper = (BalanceHopper) new BalanceHopper().setStoreOnFlush(true).
                setEncodingManager(new EncodingManager("NORMAL_CAR")).
                setGraphHopperLocation(ghLoc).
                setOSMFile(osmAmsterdam).setCHEnable(false);
        hopper.importOrLoad();

        init(hopper);
        
        SimulationSetup sim = new SimulationSetup();
        //ArrayList<VehicleUnit> instance = sim.generateInstance(10000, 3000); // #iterations, time_range
        ArrayList<VehicleUnit> instance = sim.generateOVINInstance(ovin, hopper);
       //instance = sim.quantifyInstance(instance, 150000);
        //ArrayList<VehicleUnit> instance = sim.generateSameStartEnd(100);
        sim.saveInstance(instance, savedInstance);
        ArrayList<VehicleUnit> loaded_instance = sim.loadInstance(savedInstance);
        
        int i =0;
        int totalTravelTime =0;
        for (VehicleUnit item:loaded_instance)
        {
            GHRequest routerequest = new GHRequest(item.getOrigin(),item.getDestination()).setAlgorithm("dijkstraTimeDependent").setWeighting("balanced").setTimeOffset(item.getStartTime());//new GHRequest(latFrom, lonFrom, latTo, lonTo);
            GHResponse ans = hopper.route(routerequest);
            
            if(!ans.hasErrors())
            {
                PathWrapper path = ans.getBest();
                if(!path.hasErrors()){
                    InstructionList instr = path.getInstructions();
                    System.out.println("Time for route "+i+": " + path.getTime());
                    totalTravelTime += path.getTime();
                    FileUtils.writeStringToFile(new File(testGPX + i + ".gpx"), instr.createGPX(""+i, 0, false, false, true, false));
                    i++;
                }
                else
                {
                    System.out.println(path.getErrors().toString());
                    System.out.println(item.getOrigin() + " to: " + item.getDestination());
                }
            }
            else{
                System.out.println(ans.getErrors().toString());
            }
        }
        
        System.out.println("Average Travel time: " + totalTravelTime/i);
        edgeInfo.saveToTxt(pcData, 0, 100);
        TrafficData dt = new TrafficData();
        dt.saveTrafficOrderedByTime(hopper, trafficJSONTime);
    }
    
    public static void init(GraphHopper graphhopper){
        edgeInfo = new Edges();
        File edgeToPostcode = new File(EDGE_TO_POSTCODE_FILE);
        if(edgeToPostcode.isFile()){
            PostCode.loadEdges(EDGE_TO_POSTCODE_FILE);
        }
        else{
            PrintWriter writer = null;
            try
            {
                PostCode postCode = new PostCode(ALL_POSTCODES_FILE);
                writer = new PrintWriter(EDGE_TO_POSTCODE_FILE, "UTF-8");
                AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
                NodeAccess na = graphhopper.getGraphHopperStorage().getNodeAccess();
                while(iter.next()){
                    edgeInfo.setPostcode(iter.getEdge(),postCode.closestPostCode(na.getLat(iter.getAdjNode()),na.getLon(iter.getAdjNode())));
                    writer.println(iter.getEdge() + "," + edgeInfo.getPostCode(iter.getEdge()));
                }
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(Balancity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex)
            {
                Logger.getLogger(Balancity.class.getName()).log(Level.SEVERE, null, ex);
            } finally
            {
                writer.close();
            }
        }
        
        File edgeToSound = new File(EDGE_TO_SOUND_FILE);
        if(edgeToSound.isFile())
        {
            edgeInfo.loadSound(EDGE_TO_SOUND_FILE);
        }
        else
        {
            PrintWriter writer = null;
            try{
                writer = new PrintWriter(EDGE_TO_SOUND_FILE,"UTF-8");
                AllEdgesIterator iter = graphhopper.getGraphHopperStorage().getAllEdges();
                NodeAccess na = graphhopper.getGraphHopperStorage().getNodeAccess();
                while(iter.next())
                {
                    edgeInfo.setSoundLevel(iter.getEdge(),Sound.loadSoundFile(na.getLat(iter.getAdjNode()),na.getLon(iter.getAdjNode())));
                    writer.println(iter.getEdge()+","+edgeInfo.getSoundLevel(iter.getEdge()));
                    System.out.println(iter.getEdge()+","+edgeInfo.getSoundLevel(iter.getEdge()));
                    
                }
            }            
            catch (FileNotFoundException ex)
            {
                Logger.getLogger(Balancity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex)
            {
                Logger.getLogger(Balancity.class.getName()).log(Level.SEVERE, null, ex);
            } finally
            {
                writer.close();
            }
        }
                
    }
    
}
