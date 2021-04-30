package entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RelationGraph {
    private Map<ModelEntityDocument, Set<ModelEntityDocument>> adjacencyMap;

    public RelationGraph() {
        adjacencyMap = new HashMap<>();
    }

    public void addEdge(ModelEntityDocument source, ModelEntityDocument target){
        Set<ModelEntityDocument> neighbors;
        if(adjacencyMap.containsKey(source)){
            neighbors = adjacencyMap.get(source);
            neighbors.add(target);
        } else {
            neighbors = new HashSet<>();
            neighbors.add(target);
        }
        adjacencyMap.put(source, neighbors);
    }

    public void addUndirectedEdge(ModelEntityDocument vertex1, ModelEntityDocument vertex2){
        this.addEdge(vertex1, vertex2);
        this.addEdge(vertex2, vertex1);
    }

    public Map<ModelEntityDocument, Set<ModelEntityDocument>> getAdjacencyMap() {
        return adjacencyMap;
    }

    public Set<ModelEntityDocument> getRelatedDocuments(ModelEntityDocument doc){
        Set<ModelEntityDocument> relatedDocs = adjacencyMap.get(doc);
        return relatedDocs;
    }

    public boolean hasEdge(ModelEntityDocument sourceDoc, ModelEntityDocument destDoc){
        if(adjacencyMap.containsKey(sourceDoc)) {
            return (adjacencyMap.get(sourceDoc).contains(destDoc));
        }
        return false;
    }
}
