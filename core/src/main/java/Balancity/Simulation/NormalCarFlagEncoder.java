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

import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.util.PMap;

/**
 *
 * @author Paul de Goffau
 */
public class NormalCarFlagEncoder extends CarFlagEncoder implements EnvironmentImpact
{
    public NormalCarFlagEncoder(){
        this(5,5,0);
    }
    
    public NormalCarFlagEncoder(String propertiesStr){
        super(propertiesStr);
    }
    
    public NormalCarFlagEncoder( PMap properties )
    {
        super(properties);
    }
    
    public NormalCarFlagEncoder( int speedBits, double speedFactor, int maxTurnCosts ){
        super(speedBits,speedFactor,maxTurnCosts);
    }
    
    @Override
    public String toString()
    {
        return "normal_car";
    }
    
    @Override
    public double getEmission( int speed )
    {
        if(speed < 31){
            return 5.0;
        }
        else if(speed<60)
        {
            return 1.0;
        }else if(speed<76)
        {
            return 2.0;
        }else if(speed<101)
        {
            return 4.0;
        }
        return 6.0;
    }
    
}
