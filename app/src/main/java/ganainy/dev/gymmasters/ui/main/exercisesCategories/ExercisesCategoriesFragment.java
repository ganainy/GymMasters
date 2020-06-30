package ganainy.dev.gymmasters.ui.main.exercisesCategories;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.ui.exercise.MuscleFragment;

import butterknife.ButterKnife;
import ganainy.dev.gymmasters.ui.main.ActivityCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExercisesCategoriesFragment extends Fragment {


    public static final String SELECTED_MUSCLE = "selectedMuscle";
    public static final String TRICEPS = "triceps";
    public static final String CHEST = "chest";
    public static final String SHOULDER = "shoulder";
    public static final String BICEPS = "biceps";
    public static final String ABS = "abs";
    public static final String BACK = "back";
    public static final String CARDIO = "cardio";
    public static final String LOWERLEG = "lowerleg";
    public static final String SHOWALL = "showall";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public ExercisesCategoriesFragment() {
        // Required empty public constructorp
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_excercies, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(selectedCategory -> {
            ((ActivityCallback) requireActivity()).onOpenMuscleFragment(selectedCategory);
        });
        categoriesAdapter.setData(getCategoriesList());

        recyclerView.setAdapter(categoriesAdapter);

    }


    List<Pair<String,Drawable>>getCategoriesList(){
        List<Pair<String,Drawable>> categories = new ArrayList<>();
        categories.add(new Pair<>(TRICEPS,getActivity().getResources().getDrawable(R.drawable.triceps)));
        categories.add(new Pair<>(CHEST,getActivity().getResources().getDrawable(R.drawable.chest)));
        categories.add(new Pair<>(SHOULDER,getActivity().getResources().getDrawable(R.drawable.shoulder)));
        categories.add(new Pair<>(BICEPS,getActivity().getResources().getDrawable(R.drawable.biceps)));
        categories.add(new Pair<>(ABS,getActivity().getResources().getDrawable(R.drawable.abs)));
        categories.add(new Pair<>(BACK,getActivity().getResources().getDrawable(R.drawable.back)));
        categories.add(new Pair<>(CARDIO,getActivity().getResources().getDrawable(R.drawable.cardio)));
        categories.add(new Pair<>(LOWERLEG,getActivity().getResources().getDrawable(R.drawable.lowerleg)));
        categories.add(new Pair<>(SHOWALL,getActivity().getResources().getDrawable(R.drawable.showall)));
        return categories;
    }

}
