package tokyo.northside.jrst.ast;

public class BulletListNodeElement extends TextElement {
    private String bullet;

    public BulletListNodeElement(String text) {
        super(text);
    }

    public String getBullet() {
        return bullet;
    }

    public void setBullet(String bullet) {
        this.bullet = bullet;
    }
}
