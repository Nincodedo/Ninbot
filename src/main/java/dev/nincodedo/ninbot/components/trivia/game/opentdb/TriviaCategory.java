package dev.nincodedo.ninbot.components.trivia.game.opentdb;

public class TriviaCategory {
    private Integer id;
    private String name;


    public TriviaCategory() {
    }


    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof TriviaCategory)) return false;
        final TriviaCategory other = (TriviaCategory) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        return this$name == null ? other$name == null : this$name.equals(other$name);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof TriviaCategory;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "TriviaCategory(id=" + this.getId() + ", name=" + this.getName() + ")";
    }
}
