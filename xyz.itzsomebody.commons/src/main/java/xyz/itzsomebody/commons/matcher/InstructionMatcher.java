/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package xyz.itzsomebody.commons.matcher;

import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.ArrayList;
import java.util.List;

public class InstructionMatcher {
    private final InstructionPattern pattern;
    private final AbstractInsnNode start;
    private final List<List<AbstractInsnNode>> captured = new ArrayList<>();

    public InstructionMatcher(InstructionPattern pattern, AbstractInsnNode start) {
        this.pattern = pattern;
        this.start = start;
    }

    public boolean matches() {
        var rules = pattern.getRules();
        if (rules.size() == 0) {
            return false;
        }

        var current = start;
        ArrayList<AbstractInsnNode> possibleMatch = new ArrayList<>();
        for (var rule : rules) {
            if (current == null || !rule.matches(this, current)) {
                return false;
            }
            possibleMatch.add(current);
            current = current.getNext();
        }
        if (current != null) {
            return false;
        }
        captured.add(possibleMatch);
        return true;
    }

    public boolean find() { // This is a good example of why you shouldn't let me algorithmic programming
        var rules = pattern.getRules();
        if (rules.size() == 0) {
            return false;
        }
        var initialSize = captured.size();
        ArrayList<AbstractInsnNode> possibleMatch = new ArrayList<>();
        var current = start;
        int currentRuleIndex = 0;

        while (current != null) {
            if (rules.get(currentRuleIndex).matches(this, current)) {
                possibleMatch.add(current);
                if (++currentRuleIndex == rules.size()) {
                    captured.add(possibleMatch);
                    currentRuleIndex = 0;
                    possibleMatch = new ArrayList<>();
                }
            } else {
                currentRuleIndex = 0;
                possibleMatch.clear();
            }
            current = current.getNext();
        }

        return captured.size() - initialSize != 0;
    }

    public List<AbstractInsnNode> getCaptured(int which) {
        return captured.get(which);
    }

    public List<List<AbstractInsnNode>> getAllCaptured() {
        return captured;
    }
}
