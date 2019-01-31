package org.ny.woods.template;

import org.ny.woods.layout.widget.HButton;
import org.ny.woods.layout.widget.HCanvasView;
import org.ny.woods.layout.widget.HEditText;
import org.ny.woods.layout.widget.HGridView;
import org.ny.woods.layout.widget.HHorizontalScrollView;
import org.ny.woods.layout.widget.HImageView;
import org.ny.woods.layout.widget.HLinearLayout;
import org.ny.woods.layout.widget.HListView;
import org.ny.woods.layout.widget.HRelativeLayout;
import org.ny.woods.layout.widget.HScrollView;
import org.ny.woods.layout.widget.HTextView;

public class SimpleHTemplate extends HTemplate {
    {
        as("linear-layout", HLinearLayout.class.getName());
        as("relative-layout", HRelativeLayout.class.getName());
        as("hscroll-layout", HHorizontalScrollView.class.getName());
        as("vscroll-layout", HScrollView.class.getName());
        as("default-view", HCanvasView.class.getName());
        as("text-view", HTextView.class.getName());
        as("edit-view", HEditText.class.getName());
        as("button-view", HButton.class.getName());
        as("img-view", HImageView.class.getName());
        as("list-view", HListView.class.getName());
        as("grid-view", HGridView.class.getName());
    }
}
