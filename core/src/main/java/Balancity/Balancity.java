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
        String ghLoc = "target/balancity";
        String testOsm = "C:/Users/Paul de Goffau/Desktop/Master thesis/graphhopper/Amsterdam.osm.pbf";
        String testGPX = "testGPX";
        double latFrom = 52.275159;
        double lonFrom = 4.957009;
        double latTo = 52.444960;
        double lonTo = 4.853645;

        BalanceHopper hopper = (BalanceHopper) new BalanceHopper().setStoreOnFlush(true).
                setEncodingManager(new EncodingManager("CAR")).
                setGraphHopperLocation(ghLoc).
                setOSMFile(testOsm).setCHEnable(false);
        hopper.importOrLoad();

        int num_iterations = 10;
        for (int i = 0; i < num_iterations; i++)
        {
            GHRequest routerequest = new GHRequest(new GHPoint(latFrom, lonFrom),new GHPoint(latTo, lonTo));//new GHRequest(latFrom, lonFrom, latTo, lonTo);
            GHResponse ans = hopper.route(routerequest);

            PathWrapper path = ans.getBest();
            PointList points = path.getPoints();
            InstructionList instr = path.getInstructions();
            System.out.println("Distance: " + path.getTime());
            if (i == num_iterations - 1)
            {
                FileUtils.writeStringToFile(new File("test" + i + ".gpx"), instr.createGPX(testGPX, 925));
            }
            for (GHPoint p : points)
            {
                removeEdge(hopper, p.lat, p.lon);
            }
        }

        /*        for(Instruction instruction : instr) {
         System.out.println(instruction.toString());
         }
         System.out.println("Distance: " + path.getDistance());
         */
    }

    private static double removeEdge( GraphHopper hopper, double lat, double lng )
    {

        LocationIndex index = hopper.getLocationIndex();
        QueryResult qr = index.findClosest(lat, lng, EdgeFilter.ALL_EDGES);
        EdgeIteratorState edge = qr.getClosestEdge();
        FlagEncoder encoder = hopper.getEncodingManager().getEncoder("car");
        double old_speed = encoder.getSpeed(edge.getFlags());
        double new_speed = old_speed - 3;
        if (new_speed < 10)
        {
            new_speed = 10;
        }
        edge.setFlags(encoder.setSpeed(edge.getFlags(), new_speed));
        return Double.POSITIVE_INFINITY;
    }
}
