package in.ecomexpress.sathi.ui.dashboard.drs.list;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.ui.drs.todolist.DRSListAdapter;
import in.ecomexpress.sathi.utils.GlobalConstant;

public class RecyclerItemTouchCallback extends ItemTouchHelper.Callback {
    DRSListAdapter drsListAdapter;
    private final ItemTouchHelperListener itemTouchHelperListener;

    public RecyclerItemTouchCallback( DRSListAdapter drsListAdapter,ItemTouchHelperListener itemTouchHelperListener){
        this.itemTouchHelperListener = itemTouchHelperListener;
        this.drsListAdapter = drsListAdapter;
    }

    @Override
    public boolean isItemViewSwipeEnabled(){
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target){
        int targetShipmentStatus = 0;
        CommonDRSListItem targetDrsListItem = drsListAdapter.getData().get((target.getAdapterPosition()));
        switch(targetDrsListItem.getType()){
            case GlobalConstant.ShipmentTypeConstants.RVP:
                targetShipmentStatus = targetDrsListItem.getDrsReverseQCTypeResponse().getShipmentStatus();
                break;
            case GlobalConstant.ShipmentTypeConstants.EDS:
                targetShipmentStatus = targetDrsListItem.getEdsResponse().getShipmentStatus();
                break;
            case GlobalConstant.ShipmentTypeConstants.FWD:
                targetShipmentStatus = targetDrsListItem.getDrsForwardTypeResponse().getShipmentStatus();
                break;
            case GlobalConstant.ShipmentTypeConstants.RTS:
                targetShipmentStatus = targetDrsListItem.getIRTSInterface().getDetails().getShipmentStatus();
                break;
        }
        boolean shouldEnableDrag = false;
        CommonDRSListItem drsListItem = drsListAdapter.getData().get((viewHolder.getAdapterPosition()));
        switch(drsListItem.getType()){
            case GlobalConstant.ShipmentTypeConstants.RVP:
                if(drsListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == 0 && targetShipmentStatus ==0){
                    shouldEnableDrag = true;
                }
                break;
            case GlobalConstant.ShipmentTypeConstants.EDS:
                if(drsListItem.getEdsResponse().getShipmentStatus() == 0 && targetShipmentStatus == 0){
                    shouldEnableDrag = true;
                }
                break;
            case GlobalConstant.ShipmentTypeConstants.FWD:
                if(drsListItem.getDrsForwardTypeResponse().getShipmentStatus() == 0 && targetShipmentStatus == 0){
                    shouldEnableDrag = true;
                }
                break;
            case GlobalConstant.ShipmentTypeConstants.RTS:
                if(drsListItem.getIRTSInterface().getDetails().getShipmentStatus() == 0 && targetShipmentStatus == 0){
                    shouldEnableDrag = true;
                }
                break;
        }
        if(shouldEnableDrag){
            itemTouchHelperListener.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        } else{
            return false;
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction){
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder instanceof DRSListAdapter.EDSViewHolder){
                DRSListAdapter.EDSViewHolder myViewHolder = (DRSListAdapter.EDSViewHolder) viewHolder;
                itemTouchHelperListener.onRowSelected(myViewHolder);
            } else if(viewHolder instanceof DRSListAdapter.ForwardViewHolder){
                DRSListAdapter.ForwardViewHolder myViewHolder = (DRSListAdapter.ForwardViewHolder) viewHolder;
                itemTouchHelperListener.onRowSelected(myViewHolder);
            } else if(viewHolder instanceof DRSListAdapter.RTSViewHolder){
                DRSListAdapter.RTSViewHolder myViewHolder = (DRSListAdapter.RTSViewHolder) viewHolder;
                itemTouchHelperListener.onRowSelected(myViewHolder);
            } else if(viewHolder instanceof DRSListAdapter.RVPViewHolder){
                DRSListAdapter.RVPViewHolder myViewHolder = (DRSListAdapter.RVPViewHolder) viewHolder;
                itemTouchHelperListener.onRowSelected(myViewHolder);
            }
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
        super.clearView(recyclerView, viewHolder);
        if(viewHolder instanceof DRSListAdapter.EDSViewHolder){
            DRSListAdapter.EDSViewHolder myViewHolder = (DRSListAdapter.EDSViewHolder) viewHolder;
            itemTouchHelperListener.onRowClear(myViewHolder);
        } else if(viewHolder instanceof DRSListAdapter.ForwardViewHolder){
            DRSListAdapter.ForwardViewHolder myViewHolder = (DRSListAdapter.ForwardViewHolder) viewHolder;
            itemTouchHelperListener.onRowClear(myViewHolder);
        } else if(viewHolder instanceof DRSListAdapter.RTSViewHolder){
            DRSListAdapter.RTSViewHolder myViewHolder = (DRSListAdapter.RTSViewHolder) viewHolder;
            itemTouchHelperListener.onRowClear(myViewHolder);
        } else if(viewHolder instanceof DRSListAdapter.RVPViewHolder){
            DRSListAdapter.RVPViewHolder myViewHolder = (DRSListAdapter.RVPViewHolder) viewHolder;
            itemTouchHelperListener.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperListener {
        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(BaseViewHolder myViewHolder);

        void onRowClear(BaseViewHolder myViewHolder);
    }
}