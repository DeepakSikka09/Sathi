package in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_opv.OpvFragment;
import in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmFragment;
import in.ecomexpress.sathi.ui.dummy.eds.vodafone.VodafoneFragment;
import in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection.AcDocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.capture_image.CaptureImageFragment;
import in.ecomexpress.sathi.ui.dummy.eds.cash_collection.CashCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.document_list_collection.DocumentListCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_amazon.AmazonFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection.DocumentCollectionFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification.DocumentVerificationFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycXMLFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc.EdsEkycHdfcFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc.EdsBkycIdfcFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc.EdsEkycIdfcFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo.EdsEkycNiyoFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl.EdsRblFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_hdfc_masking.HDFCMaskingFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_opv.OpvFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_res_opv.ResOpvFragment;
import in.ecomexpress.sathi.ui.dummy.eds.edsantwork.EdsEkycAntWorkFragment;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo.EdsEkycFreyoFragment;
import in.ecomexpress.sathi.ui.dummy.eds.ekyc_paytm.EdsEkycPaytmFragment;
import in.ecomexpress.sathi.ui.dummy.eds.icic_standard.IciciEkycFragment_standard;
import in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc.IciciEkycFragment;
import in.ecomexpress.sathi.ui.dummy.eds.paytm.PaytmFragment;
import in.ecomexpress.sathi.ui.dummy.eds.vodafone.VodafoneFragment;
import in.ecomexpress.sathi.utils.Constants;

/**
 * Created by dhananjayk on 06-11-2018.
 */

public class EDSDetailPagerAdapter extends FragmentStatePagerAdapter {
    EdsWithActivityList edsWithActivityList;
    EDSDetailViewModel edsDetailViewModel;
    List<MasterActivityData> masterActivityData;
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    Set<String> mcount = new HashSet<>();


    public EDSDetailPagerAdapter(FragmentManager fragmentManager, EdsWithActivityList edsWithActivityList, List<MasterActivityData> masterActivityData, EDSDetailViewModel edsDetailViewModel) {
        super(fragmentManager);
        this.edsWithActivityList = edsWithActivityList;
        this.masterActivityData = masterActivityData;
        this.edsDetailViewModel = edsDetailViewModel;

    }

    private void getstagetotalcount() {

        for (MasterActivityData masterData : masterActivityData) {
            if (masterData.getCode().startsWith("DC"))
                mcount.add("DC");
            else if (masterData.getCode().startsWith("DV"))
                mcount.add("DV");
            else if (masterData.getCode().startsWith("AC"))
                mcount.add("AC");
        }
    }

    @Override
    public Fragment getItem(int position) {
        MasterActivityData edsMasterData = getCurrentEdsMasterData(edsWithActivityList.edsActivityWizards.get(position).getCode());
        String activityCode = edsMasterData.getCode();
        edsDetailViewModel.setActivityCode(position, activityCode);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EDS_ACTIVITY_LIST, edsWithActivityList.edsActivityWizards.get(position));
        bundle.putParcelable(Constants.EDS_DATA, edsWithActivityList);
        bundle.putParcelable(Constants.EDS_MASTER_LIST, edsMasterData);
        edsDetailViewModel.setTotalStageCount(mcount.size());
        if (activityCode.startsWith("DC") || activityCode.startsWith("DV")) {

            if (edsMasterData.getVerificationMode().startsWith("INPUT")) {

                DocumentVerificationFragment documentVerificationFragment = DocumentVerificationFragment.newInstance();
                documentVerificationFragment.setArguments(bundle);
                return documentVerificationFragment;

            } else if (edsMasterData.getVerificationMode().equalsIgnoreCase("CHECK")) {
                if (activityCode.startsWith("DC_LIST")) {
                    DocumentListCollectionFragment documentListCollectionFragment = DocumentListCollectionFragment.newInstance();
                    documentListCollectionFragment.setArguments(bundle);
                    return documentListCollectionFragment;
                } else {
                    DocumentCollectionFragment documentCollectionFragment = DocumentCollectionFragment.newInstance();
                    documentCollectionFragment.setArguments(bundle);
                    return documentCollectionFragment;
                }
            }

        } else if (activityCode.startsWith("AC")) {

            if (activityCode.startsWith("AC_") && activityCode.endsWith("CPV")) {
                if (activityCode.contains("RES")) {
                    ResOpvFragment resOpvFragment = ResOpvFragment.newInstance();
                    resOpvFragment.setArguments(bundle);
                    return resOpvFragment;

                } else {
                    OpvFragment opvFragment = OpvFragment.newInstance();
                    opvFragment.setArguments(bundle);
                    return opvFragment;
                }
            }
            else if (activityCode.startsWith("AC_LIST")) {
                AcDocumentListCollectionFragment acDocumentListCollectionFragment = AcDocumentListCollectionFragment.newInstance();
                acDocumentListCollectionFragment.setArguments(bundle);
                return acDocumentListCollectionFragment;
            }
            else if (activityCode.startsWith("AC") && activityCode.endsWith("IMAGE")) {
                CaptureImageFragment captureImageFragment = CaptureImageFragment.newInstance();
                captureImageFragment.setArguments(bundle);
                return captureImageFragment;
            }
            else if(activityCode.startsWith("AC_AMAZON_EKYC"))
            {
                AmazonFragment amazonFragment = AmazonFragment.newInstance();
                amazonFragment.setArguments(bundle);
                return amazonFragment;

            }
            else if(activityCode.startsWith("AC_HDFC_MASKING"))
            {
                HDFCMaskingFragment hdfcMaskingFragment = HDFCMaskingFragment.newInstance();
                hdfcMaskingFragment.setArguments(bundle);
                return hdfcMaskingFragment;

            }
            else {
                switch (activityCode) {

                    case "AC_NEO_EKYC":
                        VodafoneFragment vodafoneFragment = VodafoneFragment.newInstance();
                        vodafoneFragment.setArguments(bundle);
                        return vodafoneFragment;

                    case "AC_CASH_COLLECTION":
                        Constants.IS_CASH_COLLECTION_ENABLE = true;
                        Constants.CASH_RECEIPT="true";
                        CashCollectionFragment cashCollectionFragment = CashCollectionFragment.newInstance();
                        cashCollectionFragment.setArguments(bundle);
                        return cashCollectionFragment;


                    case "AC_KYC_XML":
                        Constants.IS_ICICI_FINKARE = 0;
                        EdsEkycXMLFragment EkycXmlFragment = EdsEkycXMLFragment.newInstance();
                        EkycXmlFragment.setArguments(bundle);
                        return EkycXmlFragment;


                    case "AC_HDFC_BKYC":
                        EdsEkycHdfcFragment edsEkycHdfcFragment1=EdsEkycHdfcFragment.newInstance();
                        edsEkycHdfcFragment1.setArguments(bundle);
                        return edsEkycHdfcFragment1;

                    case "AC_YES_EKYC":
                        EdsEkycAntWorkFragment edsEkycAntWorkFragment=EdsEkycAntWorkFragment.newInstance();
                        edsEkycAntWorkFragment.setArguments(bundle);
                        return edsEkycAntWorkFragment;

                    case "AC_IDFC_BKYC":

                        EdsBkycIdfcFragment edsBkycIdfcFragment = EdsBkycIdfcFragment.newInstance();
                        edsBkycIdfcFragment.setArguments(bundle);
                        return edsBkycIdfcFragment;

                    case "AC_IDFC_EKYC":

                        EdsEkycIdfcFragment edsEkycIdfcFragment = EdsEkycIdfcFragment.newInstance();
                        edsEkycIdfcFragment.setArguments(bundle);
                        return edsEkycIdfcFragment;

                    case "AC_ICICI_BKYC":
                        Constants.IS_ICICI_FINKARE = 1;
                        IciciEkycFragment iciciEkycFragment = IciciEkycFragment.newInstance();
                        iciciEkycFragment.setArguments(bundle);
                        return iciciEkycFragment;


                    case "AC_RBL_BKYC":
                        Constants.IS_ICICI_FINKARE = 0;
                        EdsRblFragment RblFragment = EdsRblFragment.newInstance();
                        RblFragment.setArguments(bundle);
                        return RblFragment;

                    case "AC_ECOMGEN_BKYC":
                        Constants.IS_ICICI_FINKARE = 0;
                        IciciEkycFragment_standard iciciEkycFragment_standard = IciciEkycFragment_standard.newInstance();
                        iciciEkycFragment_standard.setArguments(bundle);
                        return iciciEkycFragment_standard;

                    case "AC_RBL_A_BKYC":
                        Constants.IS_ICICI_FINKARE = 0;
                        EdsRblFragment RblFragment1 = EdsRblFragment.newInstance();
                        RblFragment1.setArguments(bundle);
                        return RblFragment1;
                    case "AC_PAYTM_EKYC":
                        PaytmFragment paytmFragment = PaytmFragment.newInstance();
                        paytmFragment.setArguments(bundle);
                        return paytmFragment;

                    case "AC_PAYTMM_EKYC":
                        EdsEkycPaytmFragment edsEkycPaytmFragment = EdsEkycPaytmFragment.newInstance();
                        edsEkycPaytmFragment.setArguments(bundle);
                        return edsEkycPaytmFragment;

                    case "AC_NIYO_BKYC":
                        //  case "AC_HDFC_BKYC":
                        EdsEkycNiyoFragment edsEkycNiyoFragment = EdsEkycNiyoFragment.newInstance();
                        edsEkycNiyoFragment.setArguments(bundle);
                        return edsEkycNiyoFragment;

                    case "AC_FREO_BKYC":
                        EdsEkycFreyoFragment edsEkycFreyoFragment = EdsEkycFreyoFragment.newInstance();
                        edsEkycFreyoFragment.setArguments(bundle);
                        return edsEkycFreyoFragment;


                    default:
                        break;
                }
            }
        }

        return null;
    }

    @Override
    public int getCount() {
        if (edsWithActivityList != null && edsWithActivityList.edsActivityWizards != null && !edsWithActivityList.edsActivityWizards.isEmpty()) {
            return edsWithActivityList.edsActivityWizards.size();

        } else {
            return 0;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    private MasterActivityData getCurrentEdsMasterData(String qcCode) {
        getstagetotalcount();
        for (MasterActivityData edsMasterData : masterActivityData) {
            if (edsMasterData.getCode().equalsIgnoreCase(qcCode)) {
                return edsMasterData;
            }
        }
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}