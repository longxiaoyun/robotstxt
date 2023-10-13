package io.github.longxiaoyun.is.robotstxt.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import io.github.longxiaoyun.is.robotstxt.enums.DirectiveType;

import java.io.Serializable;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class RobotsRule implements Serializable {
    private DirectiveType directiveType;
    private String value;

    RobotsRule(final DirectiveType directiveType, final String value) {
        this.directiveType = directiveType;
        this.value = value;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RobotsRule other = (RobotsRule) obj;
        return Objects.equals(directiveType, other.directiveType)
                && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directiveType, value);
    }

}
