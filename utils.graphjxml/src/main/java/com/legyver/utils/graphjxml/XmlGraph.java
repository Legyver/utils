package com.legyver.utils.graphjxml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * XmlGraph that represents the XML as a graph.
 * Note: There will never be more than one root node as there can't be more than one root element in an XML file
 */
public class XmlGraph {
    /**
     * The name of the node.  This will be either the element name or the attribute name
     */
    private String name;
    /**
     * The parent of the node
     */
    private final XmlGraph parent;
    /**
     * The type of the node, either element or attribute
     */
    private final NodeType nodeType;
    /**
     * The value associated with the node.  This will either be the characters between the element tags or the attribute value
     */
    private String value;
    /**
     * String representation of the node with wrapping tags/attributes
     */
    private final StringBuilder context = new StringBuilder();
    /**
     * Child nodes
     */
    protected final List<XmlGraph> children = new ArrayList<>();

    /**
     * Construct an XML Graph node
     * @param name the name of the node
     * @param parent the parent node
     * @param nodeType the type of the node
     */
    public XmlGraph(String name, XmlGraph parent, NodeType nodeType) {
        this.name = name;
        this.parent = parent;
        this.nodeType = nodeType;
    }

    /**
     * Add a child to the node
     * @param child the node to add
     */
    public void accept(XmlGraph child) {
        if (child.getParent() == null) {
            //We're accepting a root graph node so assume its name
            if (name == null) {
                name = child.getName();
            }
            //children must be added independently
            for (XmlGraph grandchild : child.children) {
                accept(grandchild);
            }
        } else {
            //we're adding a new node
            children.add(child);
        }
    }

    /**
     * Append the text to the textual representation of the node
     * @param text the text to append
     * @return the textual representation context for additional append operations
     */
    public StringBuilder appendContext(String text) {
        context.append(text);
        return context;
    }

    //generated getters/setters

    /**
     * Get the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the parent node
     * @return the parent node
     */
    public XmlGraph getParent() {
        return parent;
    }

    /**
     * Get the value of the node
     * @return the value of the node
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of the node
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the textual representation of the node
     * @return return the node textual representation
     */
    public StringBuilder getContext() {
        return context;
    }

    /**
     * Get the children of the node
     * @return the node children
     */
    public List<XmlGraph> getChildren() {
        return children;
    }

    /**
     * Get the type of the node
     * @return the node type
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Get the first child node
     * @return the first child node
     * @deprecated There is no reason to use this as we no longer have a common node hanging off the root
     */
    @Deprecated
    public XmlGraph pop() {
        return children.isEmpty() ? null : children.iterator().next();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlGraph xmlGraph = (XmlGraph) o;
        return Objects.equals(name, xmlGraph.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Type of the XML node
     */
    public enum NodeType {
        /**
         * An XML attribute
         */
        ATTRIBUTE,
        /**
         * An XML element name
         */
        ELEMENT
    }
}
