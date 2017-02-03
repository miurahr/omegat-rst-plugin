package tokyo.northside.jrst.ast;

import java.util.List;

public interface Node  {
    List<Node> getChildren();
    /**
     * @return the index of the first character in the underlying buffer that is covered by this node
     */
    int getStartIndex();

    /**
     * @return the index of the character after the last one in the underlying buffer that is covered by this node
     */
    int getEndIndex();

    void accept(Visitor visitor);
}
