package com.khokhlov.khokhlovart.price_watcher.ItemInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.khokhlov.khokhlovart.price_watcher.Api.IApi;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.khokhlov.khokhlovart.price_watcher.App;
import com.khokhlov.khokhlovart.price_watcher.MainActivity;
import com.khokhlov.khokhlovart.price_watcher.R;
import com.khokhlov.khokhlovart.price_watcher.Results.Item;
import com.khokhlov.khokhlovart.price_watcher.Results.PriceHistoryItem;

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
        graph.getGridLabelRenderer().setNumVerticalLabels(4);
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        //graph.getViewport().setYAxisBoundsManual(true);
        //graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);


        DataPoint[] tmp  = new DataPoint[priceHistory.size()];
        int i = 0;
        double max = 0;
        double min = 1000000;
        for (PriceHistoryItem historyItem : priceHistory)
        {
            /*double val = historyItem.priceValue == null ? 0 : historyItem.priceValue;
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            try {
                date =  sdf.parse(historyItem.changeDate);
            } catch (ParseException e) {
            }
            tmp[i] = new DataPoint(date, val);
            i++;*/
            double val = historyItem.priceValue == null ? 0 : historyItem.priceValue;
            tmp[i] = new DataPoint(i, val);
            max = max < val ? val : max;
            min = min > val ? val : min;
            i++;
        }
        graph.getViewport().setMinY(min);
        graph.getViewport().setMaxY(max);
        graph.getViewport().setMaxX(priceHistory.size());
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(tmp);
        graph.addSeries(series);
        series.setColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "" +  priceHistory.get((int) dataPoint.getX()).changeDate + " - " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
//                Date date = new Date((long) dataPoint.getX());
//                Toast.makeText(getActivity(), "" +  new SimpleDateFormat("dd-MM-yyyy").format(date) + " - " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setVisibleInMainGIThred(final int id, final int visible)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tmp = (View) getActivity().findViewById(id);
                tmp.setVisibility(visible);
            }
        });
    }

    private void loadPriceHistory() {
        getActivity().getSupportLoaderManager().restartLoader(MainActivity.LOADER_PRICE_HISTORY, null, new LoaderManager.LoaderCallbacks<List<PriceHistoryItem>>() {
            @Override
            public Loader<List<PriceHistoryItem>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<List<PriceHistoryItem>>(getActivity().getApplicationContext()) {
                    @Override

                    public List<PriceHistoryItem> loadInBackground() {
                        try {
                            App apl = (App)getActivity().getApplicationContext();
                            List<PriceHistoryItem> items = api.getPriceHistory(apl.getPreferences(apl.KEY_AUTH_TOKEN), item.id).execute().body();
                            setVisibleInMainGIThred(R.id.lbl_check_internet, View.GONE);
                            setVisibleInMainGIThred(R.id.graph, View.VISIBLE);
                            return items;
                        } catch (IOException e) {
                            setVisibleInMainGIThred(R.id.lbl_check_internet, View.VISIBLE);
                            setVisibleInMainGIThred(R.id.graph, View.GONE);
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
