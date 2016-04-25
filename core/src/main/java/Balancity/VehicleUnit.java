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

/**
 *
 * @author Paul de Goffau
 */
public class VehicleUnit
{
    private GHPoint origin, destination;
    private int startTime;
    
    public VehicleUnit(GHPoint origin, GHPoint destination, int startTime){
        this.origin = origin;
        this.destination = destination;
        this.startTime = startTime;
    }
    
    public String toString(){
        return origin.getLat() + ", "+ origin.getLon()+"; " + destination.getLat() +", " + destination.getLon() +"; " +startTime;
    }
}
