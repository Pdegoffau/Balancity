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
import java.io.*;
import java.util.HashMap;

/**
 *
 * @author Paul de Goffau
 */
public class PostCode
{
    private HashMap<String, GHPoint> postCodes;
    private String file ="";

    public PostCode(String filepath){
        this.file = filepath;
        this.postCodes = new HashMap<>();
        this.loadPostCodes();
    }
    
    public void loadPostCodes()
    {
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] pcodes = line.split(",");
                double lat = Double.parseDouble(pcodes[1]);
                double lon = Double.parseDouble(pcodes[2]);
                postCodes.put(pcodes[0], new GHPoint(lat, lon));
            }
        } catch (FileNotFoundException ex)
        {
            System.err.println(ex.getMessage());
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }
    }
    
    public GHPoint getGPSPoint(String postcode){
        System.out.println(postCodes.get(postcode));
        return postCodes.get(postcode);
    }
    
    public boolean PointInMap(GHPoint point, GraphHopper hopper){
        return hopper.getGraphHopperStorage().getBounds().intersect(new Circle(point.lat,point.lon,1));
    }
}
