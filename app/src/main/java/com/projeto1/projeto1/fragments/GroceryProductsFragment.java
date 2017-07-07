package com.projeto1.projeto1.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projeto1.projeto1.MainActivity;
import com.projeto1.projeto1.R;
import com.projeto1.projeto1.listeners.SaleListener;
import com.projeto1.projeto1.adapters.CategoryListAdapter;
import com.projeto1.projeto1.adapters.ProductListAdapter;
import com.projeto1.projeto1.endpoints.HerokuGetSalesTask;
import com.projeto1.projeto1.models.Sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by samirsmedeiros on 17/06/17.
 */

public class GroceryProductsFragment extends Fragment implements SaleListener{


    public static final String TAG = "GROCERY_PRODUCTS_FRAGMENT";

    private View mview;
    private CategoryListAdapter mAdapter;
    private ProductListAdapter mProductAdapter;
    private List<String> categoryList;
    private RecyclerView categoryRecycleView;
    private RecyclerView productRecycleView;
    private List<Sale> salesList;
    private HerokuGetSalesTask salesTask;
    private MainActivity myMainActivity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GroceryProductsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GroceryProductsFragment getInstance() {
        GroceryProductsFragment fragment = new GroceryProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        salesList = new ArrayList<>();

        categoryList = new ArrayList<>(Arrays.asList( "Grãos", "Bebidas", "Laticínio",
                "Carnes", "Frutas e Verduras", "Oleos"));

        mview = inflater.inflate(R.layout.fragment_grocery_products, container, false);
        mAdapter = new CategoryListAdapter(getActivity(), categoryList);

        categoryRecycleView = (RecyclerView) mview.findViewById(R.id.product_categories);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecycleView.setLayoutManager(llm);
        categoryRecycleView.setAdapter(mAdapter);

        productRecycleView = (RecyclerView) mview.findViewById(R.id.product_list);

        /* GET DE PROMOÇÕES */
        salesTask = new HerokuGetSalesTask(String.format(getResources().getString(R.string.HEROKU_SALE_ENDPOINT)), this);
        salesTask.execute();

        //salesList = new ArrayList<>(Arrays.asList(new Sale("0000", "Feijao",null, 3.99, null, null,0,0,null,null,0,0, "comida")));

        mProductAdapter = new ProductListAdapter(getActivity(), salesList);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        productRecycleView.setLayoutManager(llm2);
        productRecycleView.setAdapter(mProductAdapter);

        return mview;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @SuppressLint("LongLogTag")
    @Override
    public void OnGetSalesReady(boolean ready, ArrayList<Sale> sales) {
        salesList =  sales;
        mProductAdapter = new ProductListAdapter(getActivity(), salesList);
        productRecycleView.setAdapter(mProductAdapter);

        Log.d(TAG, "Quantidade de sales no fragment " + String.valueOf(salesList.size()));

    }

    @Override
    public void OnPostSaleFinished(boolean finished) {

    }
}