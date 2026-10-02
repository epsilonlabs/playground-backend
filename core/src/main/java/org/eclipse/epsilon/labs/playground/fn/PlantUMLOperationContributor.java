package org.eclipse.epsilon.labs.playground.fn;

import net.sourceforge.plantuml.klimt.color.HColor;
import net.sourceforge.plantuml.klimt.color.HColorSet;
import net.sourceforge.plantuml.klimt.color.NoSuchColorException;
import org.apache.commons.text.WordUtils;
import org.eclipse.epsilon.eol.execute.operations.contributors.OperationContributor;

import java.awt.*;

public class PlantUMLOperationContributor extends OperationContributor  {

    @Override
    public boolean contributesTo(Object o) {
        return true;
    }
    
    public String darken(int ratio) throws Exception {
        HColor color = HColorSet.instance().getColor(getTarget()+"");
        color = color.darken(ratio);
        return color.asString();
    }

    public String lighten(int ratio) throws Exception {
        HColor color = HColorSet.instance().getColor(getTarget()+"");
        color = color.lighten(ratio);
        return color.asString();
    }
    
    /**
     * Converts a PlantUML color (e.g. "azure", "FFE45D" or "#FFE45D") to a CSS hex color.
     * Colors unknown to PlantUML are returned as-is, as they may still be valid CSS colors.
     */
    public String toCssColor() {
        try {
            return HColorSet.instance().getColor(getTarget() + "").asString();
        } catch (NoSuchColorException e) {
            return getTarget() + "";
        }
    }

    public String wrap(int n) {
        return WordUtils.wrap(getTarget() + "", n , "\\n", false);
    }

    public String wrap(int n, String newLineStr) {
        return WordUtils.wrap(getTarget() + "", n , newLineStr, false);
    }
}
