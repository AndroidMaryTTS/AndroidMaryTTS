package de.dfki.lt.freetts;

import com.sun.speech.freetts.Item;

/**
 * A ClusterUnitNamer using the simplest unitname,
 * i.e. just the uniphone itself
 */
public class UniphoneClusterUnitNamer implements ClusterUnitNamer {

    public UniphoneClusterUnitNamer() {
        super();
    }

    public void setUnitName(Item seg) {
        String cname = seg.getFeatures().getString("name");
        seg.getFeatures().setString("clunit_name", cname);
    }

}
