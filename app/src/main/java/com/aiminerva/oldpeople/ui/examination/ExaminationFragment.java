package com.aiminerva.oldpeople.ui.examination;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aiminerva.oldpeople.R;
import com.aiminerva.oldpeople.bean.DeviceInfo;
import com.aiminerva.oldpeople.bluetooth4.ble.service.BluetoothLeService;
import com.aiminerva.oldpeople.ui.bleutooth.BlueToothActivity;
import com.aiminerva.oldpeople.ui.examination.bean.MenuBean;
import com.aiminerva.oldpeople.ui.heat.HeatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExaminationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExaminationFragment extends Fragment implements BaseQuickAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.recycler)
    RecyclerView recycler;

    Unbinder un;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private List<MenuBean> menuList = new ArrayList<>();

    private ExaminationAdapter adapter;

    private int[] imgs = {
            R.drawable.physical_icon_xueya, R.drawable.physical_icon_xindian,
            R.drawable.physical_icon_xueyang, R.drawable.physical_icon_xuetang,
            R.drawable.physical_icon_xuezhi, R.drawable.physical_icon_tiwen
    };

    public static int[] names = {
            R.string.blood_pressure, R.string.electrocardio,
            R.string.blood_oxygen, R.string.blood_glucose,
            R.string.body_fat, R.string.animal_heat
    };

    public ExaminationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ExaminationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExaminationFragment newInstance(String param1) {
        ExaminationFragment fragment = new ExaminationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_examination, container, false);
        //注入ButterKnife
        un = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        for (int i = 0; i < imgs.length; i ++) {
            MenuBean menu = new MenuBean();
            menu.setName(getActivity().getResources().getString(names[i]));
            menu.setImg(imgs[i]);
            menuList.add(menu);
        }

        adapter = new ExaminationAdapter(R.layout.examination_menu_item, menuList);
        adapter.setOnItemClickListener(this);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        un.unbind();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        int mode = 7;
        int devic_type = 7;

        Intent intent = new Intent();
        switch (position) {
            case 0://血压
                mode = BluetoothLeService.MODE_XUEYA;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODPRESSURE;
                intent.setClass(getActivity(), BlueToothActivity.class);
                break;
            case 1:

                break;
            case 2://血氧
                mode = BluetoothLeService.MODE_XUEYANG;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODOXYGEN;
                intent.setClass(getActivity(), BlueToothActivity.class);
                break;
            case 3://血糖
                mode = BluetoothLeService.MODE_XUETANG;
                devic_type = DeviceInfo.DEVICE_TYPE_BLOODSUGER;
                intent.setClass(getActivity(), BlueToothActivity.class);
                break;
            case 4:

                break;
            case 5://体温
                mode = BluetoothLeService.MODE_TIWEN;
                devic_type = DeviceInfo.DEVICE_TYPE_TEMP;
                intent.setClass(getActivity(), HeatActivity.class);
                break;
        }
        if (mode != 7) {
            Bundle bundle = new Bundle();
            bundle.putInt("TITLE_TYPE", position);
            bundle.putInt("MEASURE_TYPE", mode);
            bundle.putInt("DEVIC_TYPE", devic_type);
            intent.putExtra("INTENT", bundle);
            startActivity(intent);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
