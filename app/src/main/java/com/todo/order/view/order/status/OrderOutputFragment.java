package com.todo.order.view.order.status;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.todo.order.R;

import java.util.Calendar;

import static com.todo.order.config.MangoPreferences.GBN_ORDER_STATUS_LIST;
import static com.todo.order.config.ServerAddress.API_ACTION;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderOutputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderOutputFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    private View v;
    private Context mContext;
    private String result_txt;
    RecyclerView rv_order_status;

    private Activity mActivity;
    private TextView tv_order_output_stdt, tv_order_output_eddt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public OrderOutputFragment() {
        // Required empty public constructor
        mContext = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderOutputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderOutputFragment newInstance(String param1, String param2) {
        OrderOutputFragment fragment = new OrderOutputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_order_output, container, false);

        ImageButton ib_order_output_stdt = (ImageButton) v.findViewById(R.id.ib_order_output_stdt);
        ImageButton ib_order_output_eddt = (ImageButton) v.findViewById(R.id.ib_order_output_eddt);

        tv_order_output_stdt = (TextView) v.findViewById(R.id.tv_order_output_stdt);
        tv_order_output_eddt = (TextView) v.findViewById(R.id.tv_order_output_eddt);

        Calendar cc = Calendar.getInstance();
        int year1 = cc.get(Calendar.YEAR);
        int month1 = cc.get(Calendar.MONTH);
        int day1 = cc.get(Calendar.DAY_OF_MONTH);

        String strFirstDay = String.valueOf(year1) + "-" + String.format("%02d", (month1+1)) + "-" + "01";
        String strLastDay = String.valueOf(year1) + "-" + String.format("%02d", (month1+1)) + "-" + cc.getActualMaximum(Calendar.DAY_OF_MONTH);
        tv_order_output_stdt.setText(strFirstDay);
        tv_order_output_eddt.setText(strLastDay);

        ib_order_output_stdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListenerstdt, year, month, day);
                dialog.show();
            }
        });

        ib_order_output_eddt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListenereddt, year, month, day);
                dialog.show();
            }
        });

        return v;
    }

    private DatePickerDialog.OnDateSetListener dateSetListenerstdt = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            tv_order_output_stdt.setText(String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));

            if(mActivity.getClass().equals(OrderStatusActivity.class)) {
                String uid = ((OrderStatusActivity) mActivity).getUid();
                ((OrderStatusActivity) mActivity).getDataOutput(API_ACTION, GBN_ORDER_STATUS_LIST, uid, "2", tv_order_output_stdt.getText().toString().replace("-", ""), tv_order_output_eddt.getText().toString().replace("-", ""));
            }
        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListenereddt = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            month = month + 1;
            tv_order_output_eddt.setText(String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));
            if(mActivity.getClass().equals(OrderStatusActivity.class)) {
                String uid = ((OrderStatusActivity) mActivity).getUid();
                ((OrderStatusActivity) mActivity).getDataOutput(API_ACTION, GBN_ORDER_STATUS_LIST, uid, "2", tv_order_output_stdt.getText().toString().replace("-", ""), tv_order_output_eddt.getText().toString().replace("-", ""));
            }
        }
    };
}