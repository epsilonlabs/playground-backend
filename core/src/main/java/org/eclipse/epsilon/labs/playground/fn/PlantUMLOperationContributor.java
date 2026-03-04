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
    
    public String wrap(int n) {
        return WordUtils.wrap(getTarget() + "", n , "\\n", false);
    }
}
