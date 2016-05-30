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
package Balancity.Simulation;

import Balancity.data.AveragedPostCode;
import Balancity.data.PostCode;
import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paul de Goffau
 */
public class SimulationSetup
{
    ArrayList<GHPoint> sources, destinations;
    GHPoint a2_south_in = new GHPoint(52.222376, 4.986595);
    GHPoint a2_south_out = new GHPoint(52.222291, 4.985683);
    GHPoint a4_south_west_in = new GHPoint(52.20969489819195, 4.623870849609375);
    GHPoint a4_south_west_out = new GHPoint(52.21101098, 4.62421417);
    GHPoint a44_west_in = new GHPoint(52.221418, 4.542734);
    GHPoint a44_west_out = new GHPoint(52.221515, 4.542645);
    GHPoint a9_north_in = new GHPoint(52.522346, 4.719106);
    GHPoint a9_north_out = new GHPoint(52.522333, 4.719353);
    GHPoint n246_north_in = new GHPoint(52.521681, 4.785096);
    GHPoint n246_north_out = new GHPoint(52.521680, 4.785155);
    GHPoint a7_north_in = new GHPoint(52.523629, 4.941131);
    GHPoint a7_north_out = new GHPoint(52.523554, 4.941453);
    GHPoint n247_north_east_in = new GHPoint(52.518829, 5.044243);
    GHPoint n247_north_east_out = new GHPoint(52.518845, 5.044302); //Alternative path from Afsluitdijk
    GHPoint a6_east_in = new GHPoint(52.353025, 5.204856); //Almere
    GHPoint a6_east_out = new GHPoint(52.352809, 5.204910);
    GHPoint a1_south_east_in = new GHPoint(52.26264780, 5.20870506);
    GHPoint a1_south_east_out = new GHPoint(52.2621126, 5.20872116);

    GHPoint Amsterdam_arena = new GHPoint(52.313722, 4.940938); //Football stadion parking
    GHPoint central_station = new GHPoint(52.378151, 4.899816);
    GHPoint schiphol_airport = new GHPoint(52.306986, 4.759697); //Parking Schiphol
    GHPoint zandvoort_racing = new GHPoint(52.387805, 4.544671); //Zandvoort circuit
    GHPoint rijksmuseum = new GHPoint(52.357194, 4.881610); // Parking nearby Rijksmuseum
    GHPoint offices = new GHPoint(52.400623, 4.836363); //Industry park Westpoort
    GHPoint molenwijk = new GHPoint(52.418934, 4.890091); //Residences north of Amsterdam

    public SimulationSetup()
    {
        this.sources = new ArrayList<GHPoint>();
        this.destinations = new ArrayList<GHPoint>();

        //List of sources used for traffic that comes in from the outside of the map
        this.sources.add(a2_south_in);
        this.sources.add(a4_south_west_in);
        this.sources.add(a44_west_in);
        this.sources.add(a9_north_in);
        this.sources.add(n246_north_in);
        this.sources.add(a7_north_in);
        this.sources.add(n247_north_east_in);
        this.sources.add(a6_east_in);
        this.sources.add(a1_south_east_in);
        //this.sources.add(Amsterdam_arena);
        //this.sources.add(central_station);
        //this.sources.add(schiphol_airport);
        //this.sources.add(zandvoort_racing);
        //this.sources.add(rijksmuseum);
        //this.sources.add(offices);
        //this.sources.add(molenwijk);

        //List of destinations that is used as a mapping for the locations that are outside the map
        this.destinations.add(a2_south_out);
        this.destinations.add(a4_south_west_out);
        this.destinations.add(a44_west_out);
        this.destinations.add(a9_north_out);
        this.destinations.add(n246_north_out);
        this.destinations.add(a7_north_out);
        this.destinations.add(n247_north_east_out);
        this.destinations.add(a6_east_out);
        this.destinations.add(a1_south_east_out);
        //this.destinations.add(Amsterdam_arena);
        //this.destinations.add(central_station);
        //this.destinations.add(schiphol_airport);
        //this.destinations.add(zandvoort_racing);
        //this.destinations.add(rijksmuseum);
        //this.destinations.add(offices);
        //this.destinations.add(molenwijk);
    }

    public ArrayList<VehicleUnit> generateInstance( int num_items, int time_interval )
    {
        if (num_items < sources.size() * destinations.size())
        {
            System.err.println("Not every combination between sources and destination can be generated!");
        }

        ArrayList<VehicleUnit> test_objects = new ArrayList<VehicleUnit>(num_items);

        while (test_objects.size() < num_items)
        {
            int s = (int) (Math.random() * (sources.size() - 1));
            int d = (int) (Math.random() * (destinations.size() - 1));
            int t = (int) (Math.random() * (time_interval + 1));
            if ((!sources.get(s).equals(destinations.get(d))) && (!tooClose(sources.get(s), destinations.get(d))))
            {
                test_objects.add(new VehicleUnit(sources.get(s), destinations.get(d), t));
            }
        }

        return test_objects;
    }

    public void saveInstance( ArrayList<VehicleUnit> instance, String filename )
    {
        try
        {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            for (VehicleUnit vu : instance)
            {
                writer.println(vu.toString());
            }
            writer.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SimulationSetup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SimulationSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<VehicleUnit> loadInstance( String filename )
    {
        ArrayList<VehicleUnit> res = new ArrayList<VehicleUnit>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitted = line.split(";");
                VehicleUnit vu = new VehicleUnit(new GHPoint(Double.parseDouble(splitted[0].split(", ")[0]), Double.parseDouble(splitted[0].split(", ")[1])), new GHPoint(Double.parseDouble(splitted[1].split(", ")[0]), Double.parseDouble(splitted[1].split(", ")[1])), Integer.parseInt(splitted[2]));
                res.add(vu);
            }
            br.close();
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(SimulationSetup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(SimulationSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * This method determines if two GPS coordinates are to close to each others to be used for
     * vehicle simulation
     * <p>
     * @param s point 1
     * @param d point 2
     * @return true if points are within have euclidean distance < 0.001
     */
    private boolean tooClose( GHPoint s, GHPoint d )
    {
        double dis_lon = Math.abs(s.getLon() - d.getLon());
        double dis_lat = Math.abs(s.getLat() - d.getLat());
        if (Math.sqrt(Math.pow(dis_lat, 2) + Math.pow(dis_lon, 2)) < 0.001)
        {
            return true;
        } else
        {
            return false;
        }
    }

    public ArrayList<VehicleUnit> generateOVINInstance( String OVINfile, GraphHopper hopper )
    {
        AveragedPostCode postCodeCenters = new AveragedPostCode();
        ArrayList<VehicleUnit> generatedInstance = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(OVINfile)))
        {
            String line;
            br.readLine(); // skip header row
            int counter = 0; //  Count the number of generated routes
            while ((line = br.readLine()) != null)
            {
                String[] entries = line.split(";");
                if (entries[141].length() > 0)
                {
                     // car driver or passenger
                    int wayOfTraveling = Integer.parseInt(entries[141]);
                    if ((wayOfTraveling == 10 || wayOfTraveling == 6))
                    {
                        String fromPC = entries[80];
                        String toPC = entries[85];
                        GHPoint realFrom = postCodeCenters.getGPS(fromPC);
                        GHPoint realTo = postCodeCenters.getGPS(toPC);
                        GHPoint chosenFrom = realFrom;
                        GHPoint chosenTo = realTo;
                        int additionalTimeOffset = 0;   //Additional timeoffset with respect to the ovin start time that is caused by the distance that must be traveled outside the map

                        boolean startInMap = false;
                        boolean endInMap = false;
                        if (realFrom != null && realTo != null)
                        {
                            startInMap = postCodeCenters.PointInMap(realFrom, hopper);
                            endInMap = postCodeCenters.PointInMap(realTo, hopper);
                            if (startInMap && endInMap)
                            {
                                //Do nothing special, skip branches
                            } else if (startInMap)
                            {
                                // Only the start of the route is directly in the map, find a good approximation of the endpoint
                                chosenTo = hopper.getGraphHopperStorage().getBounds().intersect(realFrom, realTo).get(0);
                                
                                //Map endpoint to the closest road at borders of map
                                GHPoint closest = destinations.get(0);
                                for (int i=1; i<destinations.size();i++)
                                {
                                    if (chosenTo.distanceTo(closest) > chosenTo.distanceTo(destinations.get(i)))
                                    {
                                        closest = destinations.get(i);
                                    }
                                }
                                chosenTo = closest;
                            } else if (endInMap)
                            {
                                //Only the endpoint lies within the map, so find a good approximation of the ingoing source
                                chosenFrom = hopper.getGraphHopperStorage().getBounds().intersect(realFrom, realTo).get(0);
                                //Map startpoint to the closest road at borders of map
                                GHPoint closest = sources.get(0);
                                for (int i=1; i<sources.size(); i++)
                                {
                                    if (chosenFrom.distanceTo(closest) > chosenFrom.distanceTo(sources.get(i)))
                                    {
                                        closest = sources.get(i);
                                    }
                                }
                                chosenFrom = closest;
                                additionalTimeOffset = ((int) chosenFrom.distanceTo(realFrom)/70)*3600;
                            }
                            else
                            {
                                //Both the start and the end are outside the map, but there may be a route between them through the map;
                                //Find the intersections between a straight line between the two points with the bounding box of the map
                                ArrayList<GHPoint> intersections = hopper.getGraphHopperStorage().getBounds().intersect(realFrom, realTo);
                                if(intersections.size() > 0){
                                    if(intersections.get(0).distanceTo(intersections.get(1))>10){
                                        if(realFrom.distanceTo(intersections.get(0))<realFrom.distanceTo(intersections.get(1)))
                                        {
                                            chosenFrom = intersections.get(0);
                                            chosenTo = intersections.get(1);
                                        }
                                        else
                                        {
                                            chosenFrom = intersections.get(1);
                                            chosenTo = intersections.get(0);
                                        }
                                        //Map startpoint to the closest road at borders of map
                                        GHPoint closestStart = sources.get(0);
                                        for (int i=1; i<sources.size(); i++)
                                        {
                                            if (chosenFrom.distanceTo(closestStart) > chosenFrom.distanceTo(sources.get(i)))
                                            {
                                                closestStart = sources.get(i);
                                            }
                                        }
                                        chosenFrom = closestStart;
                                        //Map endpoint to the closest road at borders of map
                                        GHPoint closestEnd = destinations.get(0);
                                        for (int i=1; i<destinations.size();i++)
                                        {
                                            if (chosenTo.distanceTo(closestEnd) > chosenTo.distanceTo(destinations.get(i)))
                                            {
                                                closestEnd = destinations.get(i);
                                            }
                                        }
                                        chosenTo = closestEnd;
                                        additionalTimeOffset = ((int) chosenFrom.distanceTo(realFrom)/70)*3600;
                                    }else{continue;}
                                }else{continue;}
                            }
                            
                            
                            if(chosenFrom.distanceTo(chosenTo)>1){
                                //System.out.println("From PC: " + fromPC + " GPS: " + chosenFrom + " to PC: " + toPC + " GPS: " + chosenTo);
                                int startTime = (Integer.parseInt(entries[96]) * 60 + Integer.parseInt(entries[97])) * 60 + additionalTimeOffset;
                                generatedInstance.add(new VehicleUnit(chosenFrom, chosenTo, startTime));
                                counter++;
                            }
                        }
                    }
                }
                
            }
            System.out.println("Number of cars: " + counter);
        } catch (FileNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }
        return generatedInstance;
    }
}
