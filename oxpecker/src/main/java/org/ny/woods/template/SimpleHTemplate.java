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
        asFinal("linear-layout", HLinearLayout.class.getName());
        asFinal("relative-layout", HRelativeLayout.class.getName());
        asFinal("hscroll-layout", HHorizontalScrollView.class.getName());
        asFinal("vscroll-layout", HScrollView.class.getName());
        asFinal("default-view", HCanvasView.class.getName());
        asFinal("text-view", HTextView.class.getName());
        asFinal("edit-view", HEditText.class.getName());
        asFinal("button-view", HButton.class.getName());
        asFinal("img-view", HImageView.class.getName());
        asFinal("list-view", HListView.class.getName());
        asFinal("grid-view", HGridView.class.getName());
    }
}
