package in.ecomexpress.sathi.repo.remote.model.IciciResponse;

/**
 * Created by santosh on 20/8/19.
 */

public class Skey {

        private String ci;

        private String content;

    public String getCi ()
    {
        return ci;
    }

    public void setCi (String ci)
    {
        this.ci = ci;
    }

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ci = "+ci+", content = "+content+"]";
    }
}
