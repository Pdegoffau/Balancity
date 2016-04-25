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
    GHPoint a2_south_in= new GHPoint(52.222376, 4.986595);
    GHPoint a2_south_out = new GHPoint(52.222291, 4.985683);
    GHPoint a4_south_west_in = new GHPoint(52.206032, 4.620566);
    GHPoint a4_south_west_out = new GHPoint(52.206100, 4.620024);
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
    GHPoint a1_south_east_in = new GHPoint(52.253760, 5.210795);
    GHPoint a1_south_east_out = new GHPoint(52.253746, 5.210620);
    
    GHPoint Amsterdam_arena = new GHPoint(52.313722, 4.940938); //Football stadion parking
    GHPoint central_station = new GHPoint(52.378151, 4.899816); 
    GHPoint schiphol_airport = new GHPoint(52.306986, 4.759697); //Parking Schiphol
    GHPoint zandvoort_racing = new GHPoint(52.387805, 4.544671); //Zandvoort circuit
    GHPoint rijksmuseum = new GHPoint(52.357194, 4.881610); // Parking nearby Rijksmuseum
    GHPoint offices = new GHPoint(52.400623, 4.836363); //Industry park Westpoort
    GHPoint molenwijk = new GHPoint(52.418934, 4.890091); //Residences north of Amsterdam
    
    public SimulationSetup(){
        this.sources = new ArrayList<GHPoint>();
        this.destinations = new ArrayList<GHPoint>();
        
        this.sources.add(a2_south_in);
        this.sources.add(a4_south_west_in);
        this.sources.add(a44_west_in);
        this.sources.add(a9_north_in);
        this.sources.add(n246_north_in);
        this.sources.add(a7_north_in);
        this.sources.add(n247_north_east_in);
        this.sources.add(a6_east_in);
        this.sources.add(a1_south_east_in);
        this.sources.add(Amsterdam_arena);
        this.sources.add(central_station);
        this.sources.add(schiphol_airport);
        this.sources.add(zandvoort_racing);
        this.sources.add(rijksmuseum);
        this.sources.add(offices);
        this.sources.add(molenwijk);
        
        this.destinations.add(a2_south_out);
        this.destinations.add(a4_south_west_out);
        this.destinations.add(a44_west_out);
        this.destinations.add(a9_north_out);
        this.destinations.add(n246_north_out);
        this.destinations.add(a7_north_out);
        this.destinations.add(n247_north_east_out);
        this.destinations.add(a6_east_out);
        this.destinations.add(a1_south_east_out);
        this.destinations.add(Amsterdam_arena);
        this.destinations.add(central_station);
        this.destinations.add(schiphol_airport);
        this.destinations.add(zandvoort_racing);
        this.destinations.add(rijksmuseum);
        this.destinations.add(offices);
        this.destinations.add(molenwijk);
    }
    
    public ArrayList<VehicleUnit> generateInstance(int num_items, int time_interval){
        if(num_items<sources.size()*destinations.size()){
            System.err.println("Not every combination between sources and destination can be generated!");
        }
        
        ArrayList<VehicleUnit> test_objects = new ArrayList<VehicleUnit>(num_items);
        
        while(test_objects.size()<num_items){
            int s = (int) (Math.random()*(sources.size()-1));
            int d = (int) (Math.random()*(destinations.size()-1));
            int t = (int) (Math.random()*time_interval);
            if((!sources.get(s).equals(destinations.get(d)))&&(!tooClose(sources.get(s),destinations.get(d)))){
                test_objects.add(new VehicleUnit(sources.get(s),destinations.get(d),t));
            }
        }

        return test_objects;
    }
    
    public void saveInstance(ArrayList<VehicleUnit> instance, String filename){
        try
        {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            for(VehicleUnit vu:instance){
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
    
    public ArrayList<VehicleUnit> loadInstance(String filename){
        ArrayList<VehicleUnit> res = new ArrayList<VehicleUnit>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
               String[] splitted = line.split(";");
               VehicleUnit vu = new VehicleUnit(new GHPoint(Double.parseDouble(splitted[0].split(", ")[0]),Double.parseDouble(splitted[0].split(", ")[1])),new GHPoint(Double.parseDouble(splitted[1].split(", ")[0]),Double.parseDouble(splitted[1].split(", ")[1])),Integer.parseInt(splitted[2]));
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
     * This method determines if two GPS coordinates are to close to each others to be used for vehicle simulation
     * @param s point 1
     * @param d point 2
     * @return true if points are within have euclidean distance < 0.001 
     */
    private boolean tooClose( GHPoint s, GHPoint d)
    {
        double dis_lon = Math.abs(s.getLon()-d.getLon());
        double dis_lat = Math.abs(s.getLat()-d.getLat());
        if(Math.sqrt(Math.pow(dis_lat, 2) + Math.pow(dis_lon, 2)) <0.001)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
