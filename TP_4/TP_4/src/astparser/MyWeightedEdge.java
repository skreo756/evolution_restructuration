package astparser;

import org.jgrapht.graph.DefaultWeightedEdge;

public class MyWeightedEdge extends DefaultWeightedEdge {

public MyWeightedEdge() {
    super();
}

@Override
public String toString() {
    return Double.toString(getWeight());
}
}
