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
public class ElectricCarFlagEncoder extends CarFlagEncoder implements EnvironmentImpact
{
    public ElectricCarFlagEncoder(){
        this(5,5,0);
    }
    
    public ElectricCarFlagEncoder(String propertiesStr){
        super(propertiesStr);
    }
    
    public ElectricCarFlagEncoder( PMap properties )
    {
        super(properties);
    }
    
    
    public ElectricCarFlagEncoder( int speedBits, double speedFactor, int maxTurnCosts ){
        super(speedBits,speedFactor,maxTurnCosts);
    }
    
    @Override
    public String toString()
    {
        return "electric_car";
    }
    
    @Override
    public double getEmission( int speed )
    {
        return 0.0;
    }
}
