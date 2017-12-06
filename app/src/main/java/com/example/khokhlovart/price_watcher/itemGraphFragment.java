package com.example.khokhlovart.price_watcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.khokhlovart.price_watcher.Api.IApi;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.List;


/**
 * Created by Dom on 03.12.2017.
 */

public class itemGraphFragment extends Fragment {
    private Item item;
    private IApi api;
    private View thisView;
    List<PriceHistoryItem> priceHistory;
    private void setItem(Item item){
        this.item = item;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        api = ((App) getActivity().getApplication()).getApi();

        if (item == null){setItem(ItemPagerAdapter.item);}
        return  inflater.inflate(R.layout.items_graph_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        thisView = view;
        loadPriceHistory();
    }

    public void graphUpdate(){
        if (priceHistory == null) {return;}
        GraphView graph = (GraphView) thisView.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setGridColor(R.color.black);
        graph.getGridLabelRenderer().setVerticalLabelsColor(R.color.black);
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(R.color.black);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(R.color.black);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(R.color.black);
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(R.color.black);

        DataPoint[] tmp  = new DataPoint[priceHistory.size()];
        int i = 0;
        for (PriceHistoryItem historyItem : priceHistory)
        {
            double val = historyItem.priceValue == null ? 0 : historyItem.priceValue;
            tmp[i] = new DataPoint(i, val);
            i++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(tmp);
        graph.addSeries(series);
        series.setColor( MainActivity.getRes().getColor(R.color.colorAccent));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
    }

    private void loadPriceHistory() {
        getActivity().getSupportLoaderManager().restartLoader(MainActivity.LOADER_PRICE_HISTORY, null, new LoaderManager.LoaderCallbacks<List<PriceHistoryItem>>() {
            @Override
            public Loader<List<PriceHistoryItem>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<List<PriceHistoryItem>>(getActivity().getApplicationContext()) {
                    @Override

                    public List<PriceHistoryItem> loadInBackground() {
                        try {
                            List<PriceHistoryItem> items = api.getPriceHistory(( (App)getActivity().getApplicationContext()).getAuthToken(), item.id).execute().body();
                            return items;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<PriceHistoryItem>> loader, List<PriceHistoryItem> data) {
                if (data == null) {
                }else{
                    priceHistory = data;
                    graphUpdate();
                }
            }

            @Override
            public void onLoaderReset(Loader<List<PriceHistoryItem>> loader) {

            }
        }).forceLoad();
    }
}
