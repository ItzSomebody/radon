/**
 * Copyright (c) 2008, http://www.snakeyaml.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.itzsomebody.radon.yaml.nodes;

import java.util.List;

import me.itzsomebody.radon.yaml.error.Mark;

/**
 * Base class for the two collection types {@link MappingNode mapping} and
 * {@link SequenceNode collection}.
 */
public abstract class CollectionNode<T> extends Node {
    private Boolean flowStyle;

    public CollectionNode(Tag tag, Mark startMark, Mark endMark, Boolean flowStyle) {
        super(tag, startMark, endMark);
        this.flowStyle = flowStyle;
    }

    /**
     * Returns the elements in this sequence.
     *
     * @return Nodes in the specified order.
     */
    abstract public List<T> getValue();

    /**
     * Serialization style of this collection.
     *
     * @return <code>true</code> for flow style, <code>false</code> for block
     *         style.
     */
    public Boolean getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(Boolean flowStyle) {
        this.flowStyle = flowStyle;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}
