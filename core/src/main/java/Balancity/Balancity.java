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
import com.graphhopper.*;
import com.graphhopper.routing.util.*;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Paul de Goffau
 */
public class Balancity
{

    public static void main( String[] args ) throws IOException
    {
        String postcodes = "C:/Users/Paul de Goffau/Desktop/Master thesis/convertPostcode/AveragedPostcodes.txt";
        String ovin = "C:/Users/Paul de Goffau/Desktop/Master thesis/Enquette/OViN2014_Databestand.csv";
        String savedInstance = "C:/xampp/htdocs/testfiles/test/instance.txt";
        String ghLoc = "target/balancity";
        String testOsm = "C:/Users/Paul de Goffau/Desktop/Master thesis/graphhopper/Amsterdam.osm.pbf";
        String testGPX = "C:/xampp/htdocs/testfiles/test";
        //String trafficTxt = "C:/xampp/htdocs/trafficData.txt";
        //String trafficJSON = "C:/xampp/htdocs/traffic.json";
        String trafficJSONTime =  "C:/xampp/htdocs/orderedTraffic.json";
        
        //AveragedPostCode pc = new AveragedPostCode(postcodes);

        BalanceHopper hopper = (BalanceHopper) new BalanceHopper().setStoreOnFlush(true).
                setEncodingManager(new EncodingManager("CAR")).
                setGraphHopperLocation(ghLoc).
                setOSMFile(testOsm).setCHEnable(false);
        hopper.importOrLoad();

        int num_iterations = 10;
        SimulationSetup sim = new SimulationSetup();
        //ArrayList<VehicleUnit> instance = sim.generateInstance(num_iterations, 3000);
        ArrayList<VehicleUnit> instance = sim.generateOVINInstance(ovin, hopper);
        sim.saveInstance(instance, savedInstance);
        ArrayList<VehicleUnit> loaded_instance = sim.loadInstance(savedInstance);
        
        int i =0;
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
        TrafficData dt = new TrafficData();
        //dt.saveTrafficToTextFile(hopper, trafficTxt);
        //dt.saveTrafficToJSON(hopper, trafficJSON);
        dt.saveTrafficOrderedByTime(hopper, trafficJSONTime);

        /*        for(Instruction instruction : instr) {
         System.out.println(instruction.toString());
         }
         System.out.println("Distance: " + path.getDistance());
         */
    }
}
