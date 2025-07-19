package in.ecomexpress.sathi.ui.dummy.eds.uid;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "PidOptions", strict = false)
public class PidOptions {

    public PidOptions() {
    }

    @Attribute(name = "ver", required = false)
    public String ver;

    @Element(name = "Opts", required = false)
    public in.ecomexpress.sathi.ui.dummy.eds.uid.Opts Opts;

    @Element(name = "CustOpts", required = false)
    public in.ecomexpress.sathi.ui.dummy.eds.uid.CustOpts CustOpts;

}
