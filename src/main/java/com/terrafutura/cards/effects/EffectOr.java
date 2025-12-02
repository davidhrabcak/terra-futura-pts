package main.java.com.terrafutura.cards.effects;

import main.java.com.terrafutura.cards.Effect;
import main.java.com.terrafutura.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class EffectOr implements Effect {
    private final List<Effect> children;

    public EffectOr(Effect ...children) {
        this.children = new ArrayList<>();
        this.children.addAll(List.of(children));
    }

    @Override
    public boolean check(List<Resource> input, List<Resource> output, int pollution) {
        for (Effect e : children) {
            if (e.check(input, output, pollution)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAssistance() {
        for (Effect ch : children) {
            if (ch.hasAssistance()) return true;
        }
        return false;
    }

    public void addChild(Effect e) {
        children.add(e);
    }

    public void addChild(Effect ... es) {
        children.addAll(List.of(es));
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