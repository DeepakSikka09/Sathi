package in.ecomexpress.sathi.repo.remote.model.eds;

public class Idfc_token_request {

    private String client_assertion;
    private String scope;
    private String grant_type;
    private String client_id;
    private String client_assertion_type;

    public String getScope(){
        return scope;
    }

    public void setScope(String scope){
        this.scope = scope;
    }

    public String getGrant_type(){
        return grant_type;
    }

    public void setGrant_type(String grant_type){
        this.grant_type = grant_type;
    }

    public String getClient_id(){
        return client_id;
    }

    public void setClient_id(String client_id){
        this.client_id = client_id;
    }

    public String getClient_assertion_type(){
        return client_assertion_type;
    }

    public void setClient_assertion_type(String client_assertion_type){
        this.client_assertion_type = client_assertion_type;
    }

    public String getClient_assertion(){
        return client_assertion;
    }

    public void setClient_assertion(String client_assertion){
        this.client_assertion = client_assertion;
    }
}
