package org.processmining.framework.models.petrinet.pattern;

import att.grappa.Node;
import org.processmining.framework.models.petrinet.PetriNet;

import java.util.Map;

public class LibraryComponent extends Component {

    private final String path;

    private final Map<Node, Node> isomorphism;

    public LibraryComponent(PetriNet wfnet, String path,
                            Map<Node, Node> isomorphism) {
        super(wfnet);
        this.path = path;
        this.isomorphism = isomorphism;
    }

    /**
     * @return the isomorphism
     */
    public Map<Node, Node> getIsomorphism() {
        return isomorphism;
    }

    /**
     * @return the name
     */
    public String getPath() {
        return path;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return path;
    }

    @Override
    public Component cloneComponent() {
        return new LibraryComponent(getWfnet(), path, isomorphism);
    }

}
