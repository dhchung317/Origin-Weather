package com.hyunki.origin_weather_app.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.hyunki.origin_weather_app.R;

import java.util.Objects;

abstract class BaseFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);
        progressBar = getActivity().findViewById(R.id.progress_bar);
        swipeRefreshLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refresh();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    abstract void refresh();

     void showProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showSnackBar(View v, String message) {
        Snackbar.make(v, message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public void showNetworkErrorSnack() {
        showSnackBar(coordinatorLayout, getString(R.string.network_error));
    }

    public void showLocationErrorSnack() {
        showSnackBar(Objects.requireNonNull(getActivity(),getString(R.string.require_non_null_activity))
                .findViewById(R.id.coordinatorLayout), getString(R.string.location_error));
    }
}
