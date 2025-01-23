package com.reconstruct.view.component;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;

public class SimpleTextFlowBuilder {
    private final LinkedList<Text> textLinkedList = new LinkedList<>();

    public SimpleTextFlowBuilder addRegularText(String text) {
        textLinkedList.add(new Text(text));
        return this;
    }

    public SimpleTextFlowBuilder addSubscriptText(String subscriptText) {
        Text text = new Text(subscriptText);
        text.setTranslateY(text.getFont().getSize() * 0.3);
        textLinkedList.add(text);
        return this;
    }

    public SimpleTextFlowBuilder addSuperscriptText(String superscriptText) {
        Text text = new Text(superscriptText);
        text.setTranslateY(text.getFont().getSize() * -0.3);
        textLinkedList.add(text);
        return this;
    }

    public TextFlow build() {
        return new TextFlow(textLinkedList.toArray(Node[]::new));
    }
}
