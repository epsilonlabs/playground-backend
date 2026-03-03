package org.eclipse.epsilon.labs.playground.fn;

import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorSet;
import org.apache.commons.text.WordUtils;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;

import java.awt.*;

public class PlantUMLOperationContributor extends OperationContributor  {

    @Override
    public boolean contributesTo(Object o) {
        return true;
    }
    
    public String darken(int percent) throws Exception {
        Color c = toColor(getTarget()+"");
        int p = clamp(percent, 0, 100);
        int r = (c.getRed()   * (100 - p)) / 100;
        int g = (c.getGreen() * (100 - p)) / 100;
        int b = (c.getBlue()  * (100 - p)) / 100;
        return toHex(new Color(r, g, b, c.getAlpha()));
    }

    public String lighten(int percent) throws Exception {
        Color c = toColor(getTarget()+"");
        int p = clamp(percent, 0, 100);
        int r = c.getRed()   + ((255 - c.getRed())   * p) / 100;
        int g = c.getGreen() + ((255 - c.getGreen()) * p) / 100;
        int b = c.getBlue()  + ((255 - c.getBlue())  * p) / 100;
        return toHex(new Color(r, g, b, c.getAlpha()));
    }
    
    public String wrap(int n) {
        return WordUtils.wrap(getTarget() + "", n , "\\n", false);
    }
    
    protected String toHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    protected int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
    
    protected Color toColor(String name) throws Exception {
        HColorSet set = HColorSet.instance();
        HColor hColor = set.getColor(name);
        if (hColor == null) {
            throw new IllegalArgumentException("Unknown color: " + name);
        }
        return hColor.toColor(ColorMapper.IDENTITY);
    }
}
