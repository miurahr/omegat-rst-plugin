package tokyo.northside.jrst.ast;

public class IncludeNode extends TextElement {
    private String option;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public void accept(Visitor visitor) {
        super.accept(visitor);
    }
}
