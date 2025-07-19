package in.ecomexpress.sathi.repo.remote.model.print_receipt;

/**
 * Created by anshika on 11/3/20.
 */

public class Bank_code_list {
    private String bank_code;

    private String bank_id;

    public String getBank_code ()
    {
        return bank_code;
    }

    public void setBank_code (String bank_code)
    {
        this.bank_code = bank_code;
    }

    public String getBank_id ()
    {
        return bank_id;
    }

    public void setBank_id (String bank_id)
    {
        this.bank_id = bank_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [bank_code = "+bank_code+", bank_id = "+bank_id+"]";
    }
}
