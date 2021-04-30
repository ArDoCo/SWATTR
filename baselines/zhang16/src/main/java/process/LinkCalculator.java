package process;

import entity.*;
import util.MetricCalculator;

import java.util.*;
import java.util.stream.Collectors;

public class LinkCalculator {

    double delta;

    public LinkCalculator(double delta) {
        this.delta = delta;
    }

    public RelationGraph buildRelationGraph(Collection<ModelEntityDocument> entityDocument){
        RelationGraph relationGraph = new RelationGraph();
        Map<String, ModelEntityDocument> idDocumentMapping = new HashMap<>();
        for(ModelEntityDocument entity: entityDocument){
            idDocumentMapping.put(entity.getId(), entity);
        }

        for(ModelEntityDocument entity: entityDocument){
            for(String id:entity.getReferencedEntityIds()){
                relationGraph.addEdge(entity, idDocumentMapping.get(id));
            }
        }

        return relationGraph;
    }

    public void initVectors(Collection<Document> documents){
        Set<VerbObjectPhrase> allPhrases = new HashSet<>();
        Set<String> allWords = new HashSet<>();
        for(Document d: documents){
            allPhrases.addAll(d.getDistinctVOPhrases());
            allWords.addAll(d.getDistinctWords());
        }

        for(Document d: documents){
            d.setVerbObjectWeightVector(
                    allPhrases.stream().collect(Collectors.toMap(VerbObjectPhrase::hashCode, v->0.0))
            );

            d.setWordWeightVector(
                    allWords.stream().collect(Collectors.toMap(String::hashCode, v->0.0))
            );

        }

    }

    public void calculateWeights(Collection<Document> documents){

        for(Document d: documents){
            for(VerbObjectPhrase phrase: d.getDistinctVOPhrases()){
                double tf = MetricCalculator.tf(d, phrase);
                double idf = MetricCalculator.idf(documents, phrase);
                double eta = d.getTuningFactor(phrase);
                double tfidf = tf * idf * eta;
                d.updateVerbObjectWeight(phrase.hashCode(), tfidf);
            }
        }

        for(Document d: documents){
            for(String word: d.getDistinctWords()){
                double tf = MetricCalculator.tf(d, word);
                double idf = MetricCalculator.idf(documents, word);
                double eta = d.getTuningFactor(word);
                double tfidf = tf * idf * eta;
                d.updateWordWeight(word.hashCode(), tfidf);
            }
        }
    }

    public Set<TraceLink> calculateLinks(Collection<Document> documentationDocuments, Collection<ModelEntityDocument> entityDocuments){
        Set<TraceLink> traceLinks = new HashSet<>();

        List<Document> allDocuments = new ArrayList<>();
        allDocuments.addAll(documentationDocuments);
        allDocuments.addAll(entityDocuments);

        //initialize empty vectors for all documents
        initVectors(allDocuments);

        //calc weights based on tfidf
        calculateWeights(allDocuments);

        //calculate the actual links
        for(Document dd: documentationDocuments){
            for(ModelEntityDocument ed: entityDocuments){
                    double sim = MetricCalculator.calcSim(dd, ed);
                    if(sim>0) {
                        traceLinks.add(new TraceLink(sim, dd, ed));
                    }
            }
        }

        RelationGraph relationGraph = buildRelationGraph(entityDocuments);
        updateLinksWithStructuralInfos(entityDocuments, relationGraph, traceLinks, this.delta);


        return traceLinks;
    }

    public Set<TraceLink> calculateLinks(Collection<Document> documentationDocuments, Collection<ModelEntityDocument> entityDocuments, double threshold){
        Set<TraceLink> traceLinks = new HashSet<>();
        for(TraceLink traceLink: calculateLinks(documentationDocuments, entityDocuments)){
            if(traceLink.getWeight()>=threshold){
                traceLinks.add(traceLink);
            }
        }
        return traceLinks;
    }


    public void updateLinksWithStructuralInfos(Collection<ModelEntityDocument> documents,
                                                                 RelationGraph relationGraph, Collection<TraceLink> traceLinks, double delta){
        for(TraceLink link: new ArrayList<>(traceLinks)){
            for(ModelEntityDocument entityDoc: documents){
                if(relationGraph.hasEdge(link.getEntityDocument(), (entityDoc))){
                    double sim = (MetricCalculator.calcSim(link.getDocumentationDocument(), entityDoc))
                            + delta * link.getWeight();
                    TraceLink newLink = new TraceLink(sim, link.getDocumentationDocument(), (entityDoc));

                    if (!traceLinks.contains(newLink)) {
                        traceLinks.add(newLink);
                    }

                    else{
                        for(TraceLink l: traceLinks){
                            if(l.equals(newLink)){
                                l.setWeight(sim);
                            }
                        }
                    }
                }
            }
        }
    }

}
