package co.siempo.phone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.activities.JunkfoodFlaggingActivity;
import co.siempo.phone.adapters.JunkFoodPaneAdapter;
import co.siempo.phone.customviews.ItemOffsetDecoration;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;


public class JunkFoodPaneFragment extends CoreFragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<String> items = new ArrayList<>();
    private JunkFoodPaneAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private Set<String> junkFoodList = new HashSet<>();
    private LinearLayout linSelectJunkFood;
    private Button btnSelect;

    public JunkFoodPaneFragment() {
        // Required empty public constructor
    }

    public static JunkFoodPaneFragment newInstance() {
        return new JunkFoodPaneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_junkfood_pane, container, false);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initView();
        }
    }

    private void initView() {
        junkFoodList = PrefSiempo.getInstance(getActivity()).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        if (getActivity() != null && view != null) {
            linSelectJunkFood = view.findViewById(R.id.linSelectJunkFood);
            btnSelect = view.findViewById(R.id.btnSelect);
            if (junkFoodList.size() > 0) {
                items = new ArrayList<>(junkFoodList);
                if (PrefSiempo.getInstance(getActivity()).read(PrefSiempo.IS_RANDOMIZE_JUNKFOOD, true)) {
                    Collections.shuffle(items);
                } else {
                    items = Sorting.sortJunkAppAssignment(items);
                }
                recyclerView = view.findViewById(R.id.recyclerView);
                mLayoutManager = new GridLayoutManager(getActivity(), 4);
                recyclerView.setLayoutManager(mLayoutManager);
                if (itemDecoration != null) {
                    recyclerView.removeItemDecoration(itemDecoration);
                }
                itemDecoration = new ItemOffsetDecoration(context, R.dimen.dp_10);
                recyclerView.addItemDecoration(itemDecoration);
                boolean isHideIconBranding = PrefSiempo.getInstance(context).read(PrefSiempo.IS_ICON_BRANDING, true);
                mAdapter = new JunkFoodPaneAdapter(getActivity(), items, isHideIconBranding);
                recyclerView.setAdapter(mAdapter);
                linSelectJunkFood.setVisibility(View.GONE);
            } else {
                linSelectJunkFood.setVisibility(View.VISIBLE);
                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), JunkfoodFlaggingActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                });
            }
        }
    }

}
