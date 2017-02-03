package tokyo.northside.jrst.ast;

public class Document extends Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
