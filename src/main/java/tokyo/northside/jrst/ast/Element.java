package tokyo.northside.jrst.ast;

import java.util.ArrayList;
import java.util.List;

public class Element extends AbstractNode implements Node {
    private final List<Node> children = new ArrayList<>();
    private int level;

    public Element() {
    }

    public Element(Node child) {
        children.add(child);
    }

    public Element(List<Node> children) {
        this.children.addAll(children);
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getLevel() {
        return level;
    }

    public Element setLevel(int level) {
        this.level = level;
        return this;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
