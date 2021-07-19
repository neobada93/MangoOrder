package com.todo.order.view.order.additem;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.todo.order.R;
import com.todo.order.view.order.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {

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
    RecyclerView rvAddItemFragment;

    ArrayList<OrderItem> mOrderItems = new ArrayList<>();

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    public AddItemFragment() {
        // Required empty public constructor
        mContext = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2, String param3) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mOrderItems.clear();

        v = inflater.inflate(R.layout.fragment_add_item, container, false);

        rvAddItemFragment = v.findViewById(R.id.rvAddItemFragment);

        JSONArray array;

        try {
            array = new JSONArray(mParam3);

            for(int i = 0; i < array.length(); i++) {
                JSONObject listObj = array.getJSONObject(i);
                OrderItem orderItem = new OrderItem();

                orderItem.setName(listObj.has("cate") ? listObj.get("cate").toString() : "");
                orderItem.setName(listObj.has("item") ? listObj.get("item").toString() : "");
                orderItem.setUnit(listObj.has("unit") ? listObj.get("unit").toString() : "");
                orderItem.setPrice(listObj.has("price") ? listObj.get("price").toString() : "");
                orderItem.setCd(listObj.has("itemcd") ? listObj.get("itemcd").toString() : "");
                orderItem.setUrl(listObj.has("url") ? listObj.get("url").toString() : "");
                orderItem.setQty("0");

                mOrderItems.add(orderItem);
            }

            final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);


            AddItemAdapter itemAdapter = new AddItemAdapter(mContext, mOrderItems, activity);
            itemAdapter.notifyDataSetChanged();
            RecyclerView target = v.findViewById(R.id.rvAddItemFragment);
            target.setLayoutManager(new LinearLayoutManager(mContext));
            target.setAdapter(itemAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Inflate the layout for this fragment
        return v;
    }

}