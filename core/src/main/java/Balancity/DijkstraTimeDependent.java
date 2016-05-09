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
/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import com.graphhopper.routing.AbstractRoutingAlgorithm;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.Path;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.PriorityQueue;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.util.Weighting;
import com.graphhopper.storage.SPTEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeExplorer;
import com.graphhopper.util.EdgeIterator;

/**
 * Implements a single source shortest path algorithm
 * http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * <p>
 * @author Peter Karich
 */
public class DijkstraTimeDependent extends AbstractRoutingAlgorithm
{
    protected TIntObjectMap<TDSPTEntry> fromMap;
    protected PriorityQueue<TDSPTEntry> fromHeap;
    protected TDSPTEntry currEdge;
    private int visitedNodes;
    private int to = -1;

    public DijkstraTimeDependent( Graph g, FlagEncoder encoder, Weighting weighting, TraversalMode tMode )
    {
        super(g, encoder, weighting, tMode);
        initCollections(1000);
    }

    protected void initCollections( int size )
    {
        fromHeap = new PriorityQueue<TDSPTEntry>(size);
        fromMap = new TIntObjectHashMap<TDSPTEntry>(size);
    }

    @Override
    public Path calcPath( int from, int to )
    {
        checkAlreadyRun();
        this.to = to;
        currEdge = createTDSPTEntry(from, 0,0);
        if (!traversalMode.isEdgeBased())
        {
            fromMap.put(from, currEdge);
        }
        runAlgo();
        return extractPath();
    }

    protected void runAlgo()
    {
        EdgeExplorer explorer = outEdgeExplorer;
        while (true)
        {
            visitedNodes++;
            if (isWeightLimitExceeded() || finished())
                break;

            int startNode = currEdge.adjNode;
            EdgeIterator iter = explorer.setBaseNode(startNode);
            while (iter.next())
            {
                if (!accept(iter, currEdge.edge))
                    continue;

                int traversalId = traversalMode.createTraversalId(iter, false);
                double[] tmpWeights = weighting.calcWeight(iter, false, currEdge.edge,(int)(currEdge.time));
                double tmpWeight = tmpWeights[0] + currEdge.weight;
                if (Double.isInfinite(tmpWeight))
                    continue;

                TDSPTEntry nEdge = fromMap.get(traversalId);
                if (nEdge == null)
                {
                    nEdge = new TDSPTEntry(iter.getEdge(), iter.getAdjNode(), tmpWeight,tmpWeights[1]);
                    nEdge.parent = currEdge;
                    fromMap.put(traversalId, nEdge);
                    fromHeap.add(nEdge);
                } else if (nEdge.weight > tmpWeight)
                {
                    fromHeap.remove(nEdge);
                    nEdge.edge = iter.getEdge();
                    nEdge.weight = tmpWeight;
                    nEdge.time = tmpWeights[1];
                    nEdge.parent = currEdge;
                    fromHeap.add(nEdge);
                } else
                    continue;

                updateBestPath(iter, nEdge, traversalId);
            }

            if (fromHeap.isEmpty())
                break;

            currEdge = fromHeap.poll();
            if (currEdge == null)
                throw new AssertionError("Empty edge cannot happen");
        }
    }
    
    protected TDSPTEntry createTDSPTEntry( int node, double weight, double time )
    {
        return new TDSPTEntry(EdgeIterator.NO_EDGE, node, weight,time);
    }

    @Override
    protected boolean finished()
    {
        return currEdge.adjNode == to;
    }

    @Override
    protected Path extractPath()
    {
        if (currEdge == null || isWeightLimitExceeded() || !finished()){
            return createEmptyPath();
        }
        Path result = new Path(graph, flagEncoder).setWeight(currEdge.weight).setTDSPTEntry(currEdge).extract();
        result.updateTraffic(currEdge, result.getTime());
        return result;
    }

    @Override
    public int getVisitedNodes()
    {
        return visitedNodes;
    }

    @Override
    protected boolean isWeightLimitExceeded()
    {
        return currEdge.weight > weightLimit;
    }

    @Override
    public String getName()
    {
        return AlgorithmOptions.DIJKSTRA_TIME_DEPENDENT;
    }
}

