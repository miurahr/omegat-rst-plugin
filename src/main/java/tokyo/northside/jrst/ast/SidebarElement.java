package tokyo.northside.jrst.ast;

/**
 * Created by miurahr on 2017/02/03.
 */
public class SidebarElement extends TextElement {
    private String title;
    private String subtitle;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public void accept(Visitor visitor) {
        super.accept(visitor);
    }
}
