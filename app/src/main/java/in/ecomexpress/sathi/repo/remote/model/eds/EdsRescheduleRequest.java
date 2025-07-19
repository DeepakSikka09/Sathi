package in.ecomexpress.sathi.repo.remote.model.eds;

import java.util.ArrayList;

/**
 * Created by anshika on 19/12/19.
 */

public class EdsRescheduleRequest
{
    private ArrayList<Reschedule_info_awb_list> reschedule_info_awb_list;

    public ArrayList<Reschedule_info_awb_list> getReschedule_info_awb_list ()
    {
        return reschedule_info_awb_list;
    }

    public void setReschedule_info_awb_list (ArrayList<Reschedule_info_awb_list> reschedule_info_awb_list)
    {
        this.reschedule_info_awb_list = reschedule_info_awb_list;
    }


}
