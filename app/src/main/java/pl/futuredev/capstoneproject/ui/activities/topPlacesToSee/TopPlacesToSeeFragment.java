package pl.futuredev.capstoneproject.ui.activities.topPlacesToSee;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import pl.futuredev.capstoneproject.CapstoneApplication;
import pl.futuredev.capstoneproject.R;
import pl.futuredev.capstoneproject.di.component.DaggerTopPlacesToSeeComponent;
import pl.futuredev.capstoneproject.di.module.TopPlacesToSeeModule;
import pl.futuredev.capstoneproject.models.Recipe;
import pl.futuredev.capstoneproject.models.Result;
import pl.futuredev.capstoneproject.service.InternetReceiver;
import pl.futuredev.capstoneproject.service.TriposoService;
import pl.futuredev.capstoneproject.ui.adapters.TopPlacesToSeeAdapter;

public class TopPlacesToSeeFragment extends Fragment {

    private static final String TAG = "TopPlacesToSeeActivity";
    private static final String CITY_ID = "city_id";
    private InternetReceiver internetReceiver;
    private TriposoService service;
    private List<Result> resultList;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private String cityId;
    private final CompositeDisposable disposables = new CompositeDisposable();
    Unbinder unbinder;

    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @BindView(R.id.iv_no_city)
    ImageView ivNoCity;
    @BindView(R.id.tv_no_found_city)
    TextView tvNoFoundCity;

    @Inject
    TriposoService triposoService;

    public TopPlacesToSeeFragment() {
        // Required empty public constructor
    }

    public static TopPlacesToSeeFragment newInstance(String cityId) {
        TopPlacesToSeeFragment topPlacesToSeeFragment = new TopPlacesToSeeFragment();
        Bundle args = new Bundle();
        args.putString(CITY_ID, cityId);
        topPlacesToSeeFragment.setArguments(args);
        return topPlacesToSeeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        DaggerTopPlacesToSeeComponent.builder()
                .topPlacesToSeeModule(new TopPlacesToSeeModule(((TopPlacesToSeeActivity) getContext())))
                .applicationComponent(CapstoneApplication.get(getActivity()).getApplicationComponent())
                .build().inject(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_places_to_see, container, false);

        unbinder = ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            cityId = getArguments().getString(CITY_ID);
        }
        internetReceiver = new InternetReceiver();
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        getTopPlacesToSee(cityId);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getTopPlacesToSee(String cityId) {
        disposables.add(triposoService.getTopPlacesToSee(cityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::settingUpView, this::handleError));
    }

    private void settingUpView(Recipe response) {
        resultList = response.getResults();
        if (resultList.isEmpty()) {
            myRecyclerView.setVisibility(View.INVISIBLE);
            ivNoCity.setVisibility(View.VISIBLE);
            tvNoFoundCity.setVisibility(View.VISIBLE);
        } else {
            myRecyclerView.setVisibility(View.VISIBLE);
            ivNoCity.setVisibility(View.INVISIBLE);
            tvNoFoundCity.setVisibility(View.INVISIBLE);
            adapter = new TopPlacesToSeeAdapter(resultList);
            myRecyclerView.setHasFixedSize(true);
            myRecyclerView.setLayoutManager(linearLayoutManager);
            ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
            scaleInAnimationAdapter.setDuration(350);
            scaleInAnimationAdapter.setFirstOnly(false);
            myRecyclerView.setAdapter(scaleInAnimationAdapter);
        }
    }

    private void handleError(Throwable throwable) {
        Log.d(TAG, throwable.getMessage());
        Toast.makeText(getContext(), "Error accessing database" + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        disposables.clear();
    }

}


