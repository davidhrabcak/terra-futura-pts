package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class EffectOr implements Effect {
    List<Effect> children;

    public EffectOr() {
        children = new ArrayList<>();
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        for (Effect e : children) {
            List<Resource> result = new ArrayList<>();
            if (e.check(input, result, pollution)) {
                output.addAll(result);
                return true;
            }
        }
        return false;
    }

    public void addChild(Effect e) {
        children.add(e);
    }

    public List<Effect> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public String state() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (Effect e : children) {
            s.append(e.state());
            if (!(children.getLast() == e)) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();
    }
}