package dev.nincodedo.ninbot.components.fun.pathogen.audit;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PathogenAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String action;
    private String description;
    @CreatedDate
    private LocalDateTime creationDate;
    @CreatedBy
    private String createdBy;
    private Integer weekId;


    public PathogenAudit() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(final LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getWeekId() {
        return this.weekId;
    }

    public void setWeekId(final Integer weekId) {
        this.weekId = weekId;
    }

    @java.lang.Override

    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PathogenAudit)) return false;
        final PathogenAudit other = (PathogenAudit) o;
        if (!other.canEqual(this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$weekId = this.getWeekId();
        final java.lang.Object other$weekId = other.getWeekId();
        if (this$weekId == null ? other$weekId != null : !this$weekId.equals(other$weekId)) return false;
        final java.lang.Object this$action = this.getAction();
        final java.lang.Object other$action = other.getAction();
        if (this$action == null ? other$action != null : !this$action.equals(other$action)) return false;
        final java.lang.Object this$description = this.getDescription();
        final java.lang.Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final java.lang.Object this$creationDate = this.getCreationDate();
        final java.lang.Object other$creationDate = other.getCreationDate();
        if (this$creationDate == null ? other$creationDate != null : !this$creationDate.equals(other$creationDate))
            return false;
        final java.lang.Object this$createdBy = this.getCreatedBy();
        final java.lang.Object other$createdBy = other.getCreatedBy();
        return this$createdBy == null ? other$createdBy == null : this$createdBy.equals(other$createdBy);
    }


    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PathogenAudit;
    }

    @java.lang.Override

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $weekId = this.getWeekId();
        result = result * PRIME + ($weekId == null ? 43 : $weekId.hashCode());
        final java.lang.Object $action = this.getAction();
        result = result * PRIME + ($action == null ? 43 : $action.hashCode());
        final java.lang.Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final java.lang.Object $creationDate = this.getCreationDate();
        result = result * PRIME + ($creationDate == null ? 43 : $creationDate.hashCode());
        final java.lang.Object $createdBy = this.getCreatedBy();
        result = result * PRIME + ($createdBy == null ? 43 : $createdBy.hashCode());
        return result;
    }

    @java.lang.Override

    public java.lang.String toString() {
        return "PathogenAudit(id=" + this.getId() + ", action=" + this.getAction() + ", description="
                + this.getDescription() + ", creationDate=" + this.getCreationDate() + ", createdBy="
                + this.getCreatedBy() + ", weekId=" + this.getWeekId() + ")";
    }
}
