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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Paul de Goffau
 */
public class Sound
{
    public static int loadSoundFile( double lat, double lon )
    {
        int[] res = convertRijksDriehoek.convertToRD(lat, lon);
        int ncols = 26400;
        int nrows = 31200;
        double xll = 14000.00012207;
        double yll = 307000.00012207;
        int cellsize = 10;
        int NO_DATA = -9999;
        int target = (int) (((res[1] - (int) (yll)) / cellsize) * ncols + (res[0] - (int) (xll)) / cellsize);
        int numberOfIgnoreRows = target / ncols;
        int counter = numberOfIgnoreRows * ncols;    
        String invoer = "C:\\Users\\Paul de Goffau\\Desktop\\Master thesis\\Balancity\\core\\files\\environment/splitted/soundMap"+(numberOfIgnoreRows)+".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(invoer)))
        {
            String line;
            if ((line = br.readLine()) != null)
            {
                String entries[] = line.split(" ");
                for (String entrie : entries)
                {
                    if (counter == target)
                    {
                        return Integer.parseInt(entrie);
                    }
                    counter++;
                }
            }
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
