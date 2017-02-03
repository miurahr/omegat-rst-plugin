package tokyo.northside.jrst.ast;

public class TargetAnonymousElement extends Element {
    private String refuri;

    public String getRefuri() {
        return refuri;
    }

    public void setRefuri(String refuri) {
        this.refuri = refuri;
    }

    @Override
    public void accept(Visitor visitor) {
        super.accept(visitor);
    }
}
