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
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import javax.swing.JPanel;

/**
 * General static plot with easy updating inerface
 * @author Christe, Anthony
 */
@SuppressWarnings({"serial", "unchecked"})
public class PacketExplorerPlot extends JPanel {
    private InteractivePanel panel = null;
    private DataTable dataTable = null;
    private DataTable constantDataTable = null;
    private XYPlot plot = null;
    private LineRenderer normalLines = null;
    private LineRenderer redLines = null;
    private String title = null;

    /**
     * Constructs a new packet explorer plot
     * @param title the title of plot
     */
    public PacketExplorerPlot(String title) {
        super(new BorderLayout());
        this.dataTable = new DataTable(Long.class, Integer.class);
        this.constantDataTable = new DataTable(Long.class, Integer.class);
        this.plot = new XYPlot(dataTable, constantDataTable);
        this.panel = new InteractivePanel(plot);
        this.normalLines = new DefaultLineRenderer2D();
        this.redLines = new DefaultLineRenderer2D();
        this.title = title;
        init();
    }

    private void init() {
        redLines.setSetting(LineRenderer.COLOR, java.awt.Color.RED);
        plot.setLineRenderer(dataTable, normalLines);
        plot.setLineRenderer(constantDataTable, redLines);

        plot.getAxis(XYPlot.AXIS_Y).setRange(0, 1024);
        plot.setInsets(new Insets2D.Double(10.0, 50.0, 10.0, 20.0));

        plot.setSetting(BarPlot.TITLE, title + ": ");

        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.LABEL,
                                                       "Time");
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.INTERSECTION,
                                                       -Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(
                AxisRenderer.TICK_LABELS_ROTATION, 90.0);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(
                AxisRenderer.TICKS_SPACING, 2);

        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.LABEL, "DAC");
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.INTERSECTION,
                                                       -Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_MINOR,
                                                       false);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(
                AxisRenderer.TICKS_SPACING, 200);

        this.add(panel, BorderLayout.CENTER);
        this.repaint();
    }
    
    /**
     * Change insets around plot
     * @param n north
     * @param w west
     * @param s south
     * @param e east
     */
    public void updateInsets(double n, double w, double s, double e) {
        plot.setInsets(new Insets2D.Double(n, w, s, e));
    }

    /**
     * Update plot with new samples.
     * 
     * Dynamically sets x-range updates title based off of data
     * @param samples
     * @param constant 
     */
    public void update(int[] samples, int constant) {
        int pk = 0;

        dataTable.clear();
        constantDataTable.clear();

        plot.getAxis(XYPlot.AXIS_X).setRange(0, samples.length - 1);
        
        for (int i = 0; i < samples.length; i++) {
            dataTable.add((long) i, samples[i]);
            if (constant >= 0) {
                constantDataTable.add((long) i, constant);
            }
            if (samples[i] > pk) {
                pk = samples[i];
            }
        }
        
        plot.getAxis(XYPlot.AXIS_Y).setRange(0, pk + (pk * .10));
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.TICKS_SPACING, pk/5);
        
        plot.setSetting(BarPlot.TITLE, title + ": " + pk);

        if (this.isVisible()) {
            this.repaint();
        }
    }
}
