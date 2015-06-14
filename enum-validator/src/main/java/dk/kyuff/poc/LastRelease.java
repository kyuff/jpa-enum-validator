package dk.kyuff.poc;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class LastRelease {

    public static class Change {
        private String field;
        private String comment;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public LastRelease() {
    }

    public LastRelease(Class<Enum<?>> enumType) {
        this.enumType = enumType;
    }

    // Java Type of Enum
    @NotNull
    private Class<Enum<?>> enumType;

    @NotNull
    @Size(min = 1)
    private List<String> enumFieldsInRelease = new ArrayList<>();

    @NotNull
    @Size(min = 1)
    private List<Class<?>> classes = new ArrayList<>();

    private List<Change> changes = new ArrayList<>();

    public Class<Enum<?>> getEnumType() {
        return enumType;
    }

    public void setEnumType(Class<Enum<?>> enumType) {
        this.enumType = enumType;
    }

    public List<String> getEnumFieldsInRelease() {
        return enumFieldsInRelease;
    }

    public void setEnumFieldsInRelease(List<String> enumFieldsInRelease) {
        this.enumFieldsInRelease = enumFieldsInRelease;
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public void setClasses(List<Class<?>> classes) {
        this.classes = classes;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }
}
