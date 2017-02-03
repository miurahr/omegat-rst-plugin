package tokyo.northside.jrst.ast;

/**
 * Created by miurahr on 2017/02/03.
 */
public class TopicElement extends TextElement {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void accept(Visitor visitor) {
        super.accept(visitor);
    }
}
