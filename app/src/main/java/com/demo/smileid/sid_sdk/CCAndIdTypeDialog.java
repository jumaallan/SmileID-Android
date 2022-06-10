package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.hbb20.CountryCodePicker;
import com.smileidentity.libsmileid.core.idcard.IdCard;

import java.util.List;

public class CCAndIdTypeDialog {

    private Dialog mDialog;
    private CountryCodePicker mCcpCountryPicker;
    private Spinner mSIdType;
    private DlgListener mListener;
    private String mSelectedCountryName = "", mSelectedCountryCode = "", mSelectedIdType;

    private static final String SUPPORTED_COUNTRIES = "DZ,AO,BJ,BW,BF,BI,CM,CV,TD,KM,CG,CI,CD,DJ," +
        "EG,GQ,ER,ET,GA,GM,GH,GN,GW,KE,LS,LR,LY,MG,MW,ML,MU,MA,MZ,NA,NE,NG,RW,ST,SN,SC,SL,SO,ZA," +
            "SS,SD,TG,TN,UG,TZ,ZM,ZW,AL,AD,AT,BY,BE,BA,BG,HR,CZ,DK,EE,FI,FR,DE,GR,VA,HU,IS,IE," +
                "IT,XK,LV,LI,LT,LU,MT,MC,ME,NL,NO,PL,PT,MD,RO,SM,RS,SK,SI,ES,SE,CH,MK,UA,GB,BS," +
                    "BM,CA,JM,US";

    public interface DlgListener {
        void submit(String countryCode, String idType);
        void cancel();
    }

    public CCAndIdTypeDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_cc_id_type_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
             if (mListener == null) return;
             mDialog.cancel();
             mListener.submit(mSelectedCountryName, mSelectedIdType);
        });

        mDialog.findViewById(R.id.ivBtnCancel).setOnClickListener(v -> {
            mDialog.cancel();
            mListener.cancel();
        });

        mListener = listener;

        mCcpCountryPicker = mDialog.findViewById(R.id.ccpCountry);
        mCcpCountryPicker.setCustomMasterCountries(SUPPORTED_COUNTRIES);
        mCcpCountryPicker.setOnCountryChangeListener(() -> {
            getSelectedCountryName();
            populateIdCard();
        });

        mSIdType = mDialog.findViewById(R.id.spIdType);
        mSIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIdType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSelectedCountryName();
        populateIdCard();
    }

    private String getSelectedCountryName() {
        mSelectedCountryCode = mCcpCountryPicker.getSelectedCountryNameCode();
        return mSelectedCountryName = mCcpCountryPicker.getSelectedCountryName();
    }

    private void populateIdCard() {
        IdCard idCard = IdTypeUtil.idCards(mSelectedCountryName);
        initSpinner(idCard.getIdCards());
    }

    private void initSpinner(List<String> idTypes) {
        ArrayAdapter dataAdapter = new ArrayAdapter<>(mDialog.getContext(), android.R.layout.simple_spinner_item, idTypes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSIdType.setAdapter(dataAdapter);
    }

    public void showDialog() {
        mDialog.show();
    }
}