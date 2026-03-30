/*
 * Copyright 2018-2026 Pavel Castornii.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techsenger.alpha.console.gui.window;

import com.techsenger.tabshell.core.pane.AbstractPaneViewModel;
import javafx.geometry.Bounds;

/**
 *
 * @author Pavel Castornii
 */
public abstract class AbstractWindowViewModel extends AbstractPaneViewModel {

    protected static final int WIDTH = 600;

    protected static final int HEIGHT = 300;

    private static final int MARGIN = 15;

    private double top;

    private double left;

    public AbstractWindowViewModel() {
        super();
    }

    public double getTop() {
        return top;
    }

    public double getLeft() {
        return left;
    }

    protected void resolveWindowPositions(Bounds bounds, double width, double height) {
        top = 0;
        left = 0;
        if (width - bounds.getCenterX() + MARGIN >= WIDTH) {
            left = bounds.getCenterX() - MARGIN;
        } else {
            var diff = (width - bounds.getCenterX()) - WIDTH;
            diff = Math.abs(diff);
            left = bounds.getCenterX() - diff - 5;
        }
        if (height - bounds.getCenterY() - MARGIN >= HEIGHT) {
            top = bounds.getCenterY() + MARGIN;
        } else {
            top = bounds.getCenterY() - HEIGHT - MARGIN;
        }
    }
}
