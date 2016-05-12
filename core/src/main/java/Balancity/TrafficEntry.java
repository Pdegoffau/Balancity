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

/**
 *
 * @author Paul de Goffau
 */
public class TrafficEntry
{
    private double latFrom;
    private double lonFrom;
    private double latTo;
    private double lonTo;
    private double trafficCount;
    
    public TrafficEntry(double latFrom, double lonFrom, double latTo, double lonTo, double trafficCount){
        this.latFrom = latFrom;
        this.lonFrom = lonFrom;
        this.latTo = latTo;
        this.lonTo = lonTo;
        this.trafficCount = trafficCount;
    }

    public double getLatFrom()
    {
        return latFrom;
    }

    public double getLonFrom()
    {
        return lonFrom;
    }

    public double getLatTo()
    {
        return latTo;
    }

    public double getLonTo()
    {
        return lonTo;
    }

    public double getTrafficCount()
    {
        return trafficCount;
    }            
}
