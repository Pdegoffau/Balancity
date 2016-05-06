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

import com.graphhopper.routing.util.AbstractWeighting;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.util.EdgeIterator;
import com.graphhopper.util.EdgeIteratorState;

/**
 *
 * @author Paul de Goffau
 */
public class BalanceWeighting extends AbstractWeighting
{
    private final double maxSpeed;
    protected final static double SPEED_CONV = 3.6;
    protected final static double PENALTY = 10;
    
    public BalanceWeighting(FlagEncoder encoder){
        super(encoder);
        this.maxSpeed = encoder.getMaxSpeed() / SPEED_CONV;
    }


    @Override
    public double calcWeight( EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId )
    {
        double speed = reverse ? flagEncoder.getReverseSpeed(edge.getFlags()) : flagEncoder.getSpeed(edge.getFlags());
        if (speed == 0)
            return Double.POSITIVE_INFINITY;

        double time = edge.getDistance() / speed * SPEED_CONV;

        // add direction penalties at start/stop/via points
        boolean penalizeEdge = edge.getBoolean(EdgeIteratorState.K_UNFAVORED_EDGE, reverse, false);
        if (penalizeEdge)
            time += PENALTY;

        return time;
    }

    public double calcWeight( EdgeIteratorState edge, boolean reverse, int prevOrNextEdgeId ,int startTime)
    {
        double speed = reverse ? flagEncoder.getReverseSpeed(edge.getFlags()) : flagEncoder.getSpeed(edge.getFlags());
        if (speed == 0)
            return Double.POSITIVE_INFINITY;

        double tmpWeight = edge.getDistance() / speed * SPEED_CONV;
        int tfc;
        tfc = edge.getTrafficCount(startTime);
        
        /*
        if(tfc>0){
            System.out.println("Traffic count: "+tfc);
        }
        */
        //tmpWeight += tfc*30;

        // add direction penalties at start/stop/via points
        boolean penalizeEdge = edge.getBoolean(EdgeIteratorState.K_UNFAVORED_EDGE, reverse, false);
        if (penalizeEdge)
            tmpWeight += PENALTY;

        return tmpWeight;
    }
    
    @Override
    public String getName()
    {
        return "balanced";
    }

    @Override
    public double getMinWeight( double distance )
    {
        return distance / maxSpeed;
    }


    
}
