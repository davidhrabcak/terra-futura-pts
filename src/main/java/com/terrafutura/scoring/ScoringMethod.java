package main.java.com.terrafutura.scoring;

import main.java.com.terrafutura.resources.Resource;
import java.util.*;

public class ScoringMethod {

    private final List<Resource> resources;
    private final Points pointPerCombination;
    private Points calculatedTotal;

    public ScoringMethod(List<Resource> resources, Points pointPerCombination) {
        this.resources = new ArrayList<>(resources);
        this.pointPerCombination = pointPerCombination;
    }

    public void selectThisMethodAndCalculate() {

    }

    public String state() {
        return "";
    }

}