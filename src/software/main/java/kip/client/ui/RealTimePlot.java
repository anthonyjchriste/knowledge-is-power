/*
 * Copyright 2012 Christe, Anthony
 * 
 * This file is part of KiP.
 *
 * KiP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * KiP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with KiP.  If not, see <http://www.gnu.org/licenses/>.
 */

package kip.client.ui;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import kip.utils.ConfigurationManager;

/**
 * Real time plot UI
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class RealTimePlot extends JPanel {
    private DataTable data = null;
    private XYPlot plot = null;
    private InteractivePanel panel = null;
    private LineRenderer lines = null;
    private int yMax = 0;
    private int yGap = 0;
    private String title = null;
    private String xTitle = null;
    private String yTitle = null;
    private int bufSize = 0;

    /**
     * Constructs a new real time plot
     * @param title     the title of the plot
     * @param xTitle    the title of the x-axis
     * @param yTitle    the title of the y-axis
     * @param yMax      the max range of the y-axis
     * @param yGap      the gap between values display on y-axis
     */
    public RealTimePlot(String title, String xTitle, String yTitle, int yMax,
                        int yGap) {
        super(new BorderLayout());
        this.data = new DataTable(Long.class, Double.class);
        this.plot = new XYPlot(data);
        this.panel = new InteractivePanel(plot);
        this.lines = new DefaultLineRenderer2D();
        this.yMax = yMax;
        this.yGap = yGap;
        this.title = title;
        this.xTitle = xTitle;
        this.yTitle = yTitle;
        this.bufSize = Integer.parseInt(ConfigurationManager.getProperty(
                ConfigurationManager.REAL_TIME_BUFFER));
        initPanel();
    }

    /**
     * Initializes the real time 
     */
    private void initPanel() {
        plot.setInsets(new Insets2D.Double(20.0, 60.0, 10.0, 40.0));
        plot.setSetting(BarPlot.TITLE, title);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.LABEL,
                                                       xTitle);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(
                AxisRenderer.TICK_LABELS_ROTATION, 45.0);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.LABEL,
                                                       yTitle);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.INTERSECTION,
                                                       -Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.INTERSECTION,
                                                       -Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(
                AxisRenderer.TICKS_SPACING, yGap);
        plot.getAxis(XYPlot.AXIS_Y).setRange(0, yMax);
        plot.setLineRenderer(data, lines);

        this.add(panel, BorderLayout.CENTER);
    }
    
    /**
     * Dynamically change insets of plot
     * @param n north
     * @param w west
     * @param s south
     * @param e east
     */
    public void updateInsets(double n, double w, double s, double e) {
        plot.setInsets(new Insets2D.Double(n, w, s, e));
    }

    /**
     * Update real time plot.
     * 
     * Title is changed to match current data. The x-axis and range are also
     * dynamically updated.
     * @param x the x location on the plot
     * @param y the y location on the plot
     */
    public void updatePlot(long x, double y) {
        double min;
        
        if(y > yMax) {
            yMax = (int) y;
        }
        
        plot.setSetting(BarPlot.TITLE, title + ": " + y);
        data.add(x, y);

        if (data.getRowCount() > bufSize) {
            data.remove(0);
        }

        min = data.getColumn(0).getStatistics(Statistics.MIN);

        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(
                AxisRenderer.TICKS_SPACING,
                (x - min) / bufSize);
        plot.getAxis(XYPlot.AXIS_X).setRange(min, x);
        plot.getAxis(XYPlot.AXIS_Y).setRange(0, yMax + (yMax * .20));

        if (this.isVisible()) {
            this.repaint();
        }
    }
}
