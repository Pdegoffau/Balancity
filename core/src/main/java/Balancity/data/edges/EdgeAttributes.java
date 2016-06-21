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

package Balancity.data.edges;

/**
 *
 * @author Paul de Goffau
 */
public class EdgeAttributes
{
    private double trafficCount = 0;
    private double fijnstof = 0.0;
    private int soundLevel = 0;
    
    public EdgeAttributes(double counter, double fijnstof, int sound)
    {
        this.trafficCount = counter;
        this.fijnstof = fijnstof;
        this.soundLevel = sound;
    }
    
    public EdgeAttributes(double counter, String vehicleType){
        this.trafficCount = counter;
        //TODO: set values for fijnstof and sound
    }
    
    public EdgeAttributes(double fijnstof)
    {
        this.fijnstof = fijnstof;
    }
    
    public EdgeAttributes(int soundLevel)
    {
        this.soundLevel = soundLevel;
    }
    
    public double getTrafficCount(){
        return this.trafficCount;
    }
    
    public int getSoundLevel(){
        return this.soundLevel;
    }
    
    public double getFijnstof(){
        return this.fijnstof;
    }

    public EdgeAttributes addSound(int soundLevel)
    {
        this.soundLevel = soundLevel;
        return this;
    }
    
    
}
