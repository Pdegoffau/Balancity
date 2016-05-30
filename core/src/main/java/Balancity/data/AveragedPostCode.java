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

import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.Circle;
import com.graphhopper.util.shapes.GHPoint;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Paul de Goffau
 */
public class AveragedPostCode
{
    
    private HashMap<String, GHPoint> averagedPostCodes;
    private String file ="";
    
    public AveragedPostCode(String filepath){
        this.file = filepath;
        this.averagedPostCodes = new HashMap<>();
        this.loadAveragePostCodes();
    }
    public AveragedPostCode(){
        this.file = Balancity.Balancity.POSTCODE_CENTERS_FILE;
        this.averagedPostCodes = new HashMap<>();
        this.loadAveragePostCodes();
    }
    
    public void loadAveragePostCodes(){
        try (BufferedReader br = new BufferedReader(new FileReader(file))) 
	{
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] pcodes = line.split(",");
                String pcode = pcodes[0];
                double lat = Double.parseDouble(pcodes[1]);
                double lon = Double.parseDouble(pcodes[2]);
                averagedPostCodes.put(pcode, new GHPoint(lat,lon));
            }
        } catch (FileNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }
    }
    
    public GHPoint getGPS(String postcode){
        return averagedPostCodes.get(postcode);
    }
    
    public boolean PointInMap(GHPoint point, GraphHopper hopper){
        return hopper.getGraphHopperStorage().getBounds().contains(point.lat,point.lon);
    }
    
}
