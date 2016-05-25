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

import com.graphhopper.storage.SPTEntry;

/**
 *
 * @author Paul de Goffau
 */
public class TDSPTEntry extends SPTEntry
{
    public double time;
    public TDSPTEntry parent;
    
    public TDSPTEntry(int edgeId, int adjNode, double weight, double time){
        super(edgeId,adjNode,weight);
        this.time = time;
    }
    
    public double getTimeOfPath(){
        return this.time;
    }
}
