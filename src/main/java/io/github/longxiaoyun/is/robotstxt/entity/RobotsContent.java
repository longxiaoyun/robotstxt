package io.github.longxiaoyun.is.robotstxt.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class RobotsContent implements Serializable {
    private final List<RobotsGroup> robotsGroups;

    public RobotsContent() {
        robotsGroups = new ArrayList<>();
    }

    public RobotsContent(final List<RobotsGroup> robotsGroups) {
        this.robotsGroups = robotsGroups;
    }

    public void addGroup(RobotsGroup robotsGroup) {
        robotsGroups.add(robotsGroup);
    }
}
