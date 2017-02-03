package tokyo.northside.jrst.ast;

public interface Visitor {

    /*
    void visit(BulletListNode node);
    void visit(DefinitionListNode node);
    void visit(HeaderNode node);
    void visit(ListItemNode node);
    void visit(OrderedListNode node);
    void visit(ParaNode node);
    void visit(StrikeNode node);
    void visit(StrongEmphSuperNode node);
    void visit(TableBodyNode node);
    void visit(TableCaptionNode node);
    void visit(TableCellNode node);
    void visit(TableColumnNode node);
    void visit(TableNode node);
    void visit(TableRowNode node);
    */

    void visit(FooterElement node);
    void visit(TextElement node);
    void visit(Document node);

    void visit(Element node);
    void visit(Node node); // general catch all for custom Node implementations
}
