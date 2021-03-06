package com.projeto1.projeto1.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projeto1.projeto1.MainActivity;
import com.projeto1.projeto1.R;
import com.projeto1.projeto1.adapters.CategoryListAdapter;
import com.projeto1.projeto1.adapters.ProductListAdapter;
import com.projeto1.projeto1.endpoints.HerokuGetMarketsTask;
import com.projeto1.projeto1.endpoints.HerokuGetProductsTask;
import com.projeto1.projeto1.endpoints.HerokuGetSalesTask;
import com.projeto1.projeto1.listeners.GetSubcategoryListener;
import com.projeto1.projeto1.listeners.MarketListener;
import com.projeto1.projeto1.listeners.ProductListener;
import com.projeto1.projeto1.listeners.SaleListener;
import com.projeto1.projeto1.models.Market;
import com.projeto1.projeto1.models.Product;
import com.projeto1.projeto1.models.Sale;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samirsmedeiros on 17/06/17.
 */

public class GroceryProductsFragment extends Fragment implements SaleListener, MarketListener, ProductListener, GetSubcategoryListener{


    public static final String TAG = "GROCERY_PRODUCTS_FRAGMENT";

    private View mview;
    private CategoryListAdapter mAdapter;
    private ProductListAdapter mProductAdapter;
    private List<String> categoryList;
    private RecyclerView categoryRecycleView;
    private RecyclerView productRecycleView;
    private List<Sale> salesList;
    private List<Market> marketsList;
    private List<Product> productsList;
    private HerokuGetSalesTask salesTask;
    private HerokuGetProductsTask productTask;
    private HerokuGetMarketsTask marketTask;
    private MainActivity myMainActivity;
    private ArrayList<Sale> saleSub;
    private ArrayList<Product> productsListAux;

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
        saleSub = new ArrayList<>();
        productsListAux = new ArrayList<>();
        productsList = new ArrayList<>();

        mview = inflater.inflate(R.layout.fragment_grocery_products, container, false);
        mAdapter = new CategoryListAdapter(getActivity(), ((MainActivity) getActivity()).getCurrentCategory(), this);

        categoryRecycleView = (RecyclerView) mview.findViewById(R.id.product_categories);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecycleView.setLayoutManager(llm);
        categoryRecycleView.setAdapter(mAdapter);

        productRecycleView = (RecyclerView) mview.findViewById(R.id.product_list);

        updateProductList();
        /* GET DE PROMOÇÕES */
        salesTask = new HerokuGetSalesTask(String.format(getResources().getString(R.string.HEROKU_SALE_ENDPOINT)), this);
        salesTask.execute();

        //salesList = new ArrayList<>(Arrays.asList(new Sale("0000", "Feijao",null, 3.99, null, null,0,0,null,null,0,0, "comida")));

        mProductAdapter = new ProductListAdapter(getActivity(), salesList, marketsList,productsList, getContext());
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        productRecycleView.setLayoutManager(llm2);
        productRecycleView.setAdapter(mProductAdapter);

        final TextView no_results = (TextView) mview.findViewById(R.id.no_results);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(salesList.size() == 0) no_results.setVisibility(View.VISIBLE);
                else no_results.setVisibility(View.GONE);
                mview.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        }, 1500);

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
        if(!isAdded()) {
            return;
        }
        salesList =  getProductsSaleByCategory(sales);
   //     mProductAdapter = new ProductListAdapter(getActivity(), salesList, marketsList,productsList);
   //     productRecycleView.setAdapter(mProductAdapter);
        productTask = new HerokuGetProductsTask(String.format(getResources().getString(R.string.HEROKU_PRODUCT_ENDPOINT)) , this);
        productTask.execute();
        Log.d(TAG, "Quantidade de sales no fragment " + String.valueOf(salesList.size()));

    }

    @Override
    public void OnGetSalesByMarketReady(boolean ready, ArrayList<Sale> sales) {

    }

    @Override
    public void OnPostSaleFinished(boolean finished) {

    }

    @Override
    public void OnPutSaleFinished(boolean finished) {

    }

    @Override
    public void OnPostLikeFinished(boolean finished, Sale sale) {

    }

    @Override
    public void OnDeleteLikeFinished(boolean finished, Sale sale) {

    }

    @Override
    public void OnPostDislikeFinished(boolean finished, Sale sale) {

    }

    @Override
    public void OnDeleteDislikeFinished(boolean finished, Sale sale) {

    }

    @Override
    public void OnGetMarketsReady(boolean ready, ArrayList<Market> markets) {
        marketsList = markets;
        mProductAdapter = new ProductListAdapter(getActivity(), salesList, marketsList,productsList, getContext());
        productRecycleView.setAdapter(mProductAdapter);


    }

    @Override
    public void OnPostMarketsFinished(boolean finished) {

    }

    @Override
    public void OnGetMarketsBySearchReady(boolean ready, ArrayList<Market> markets) {

    }

    @Override
    public void OnGetMarketReady(boolean b, Market market) {

    }

    @Override
    public void OnGetProductsReady(boolean ready, ArrayList<Product> products) {
        productsListAux = products;
        if(!isAdded()) {
            return;
        }
        marketTask = new HerokuGetMarketsTask(String.format(getResources().getString(R.string.HEROKU_MARKET_ENDPOINT)) , this);
        marketTask.execute();
   //     mProductAdapter = new ProductListAdapter(getActivity(), salesList, marketsList,productsList);
   //     productRecycleView.setAdapter(mProductAdapter);

    }

    private List<Sale> getProductsSaleByCategory(ArrayList<Sale> sales) {
        String category = ((MainActivity) getActivity()).getCurrentCategory();
        List<Sale> salessList = new ArrayList<>();
        for (Sale sale: sales){
            for (Product product: productsListAux){
                if(product.getId().equals(sale.getProductId()) && product.getCategory().equals(category)) {
                    salessList.add(sale);
                    productsList.add(product);
                }
            }
        }
        return  salessList;
    }

    @Override
    public void OnGetProductsByCategoryReady(boolean ready, ArrayList<Product> products) {

    }

    @Override
    public void OnPostProductFinished(boolean finished) {

    }

    @Override
    public void OnGetProductReady(boolean b, Product product) {

    }

    @Override
    public void OnGetProductByBarcodeReady(boolean ready, Product product) {

    }

    public void updateProductList(){
        productTask = new HerokuGetProductsTask(String.format(getResources().getString(R.string.HEROKU_PRODUCT_ENDPOINT)), this);
        productTask.execute();
    }


    @SuppressLint("LongLogTag")
    @Override
    public void OnSubcategorySelected(boolean selected, String subcategory) {
        if (selected){
            if (subcategory.toLowerCase().equals("outros")){
                waiting();
                mProductAdapter = new ProductListAdapter(getActivity(), salesList, marketsList,productsList, getContext());
                productRecycleView.setAdapter(mProductAdapter);
                Log.d(TAG, subcategory
                );
            }else {
                saleSub = new ArrayList<>();
                Log.d(TAG, subcategory);
                ArrayList<Product> l = new ArrayList();
                for (Product p : productsList) {
                    if (p.getSubcategory().toLowerCase().equals(subcategory.toLowerCase())) {
                        if(!l.contains(p)) l.add(p);
                    }
                }
                for (Sale s : salesList) {
                    for (Product pp : l) {
                        if (s.getProductId().equals(pp.getId())) {
                            if(!saleSub.contains(s))saleSub.add(s);
                        }

                    }
                }
               waiting();

                mProductAdapter = new ProductListAdapter(getActivity(), saleSub, marketsList, productsList, getContext());
                productRecycleView.setAdapter(mProductAdapter);
            }

            //HerokuGetSalesTask sub = new HerokuGetSalesTask(String.format(getResources().getString(R.string.HEROKU_SALE_ENDPOINT_BY_CATEGORY))+subcategory, this);
            //sub.execute();
        }
    }

    private void waiting() {
        Log.d(TAG, String.valueOf(saleSub.size()));
        final TextView no_results = (TextView) mview.findViewById(R.id.no_results);
        Handler handler = new Handler();
        no_results.setVisibility(View.GONE);
        mview.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            public void run() {
                if(saleSub.size() == 0) no_results.setVisibility(View.VISIBLE);
                else no_results.setVisibility(View.GONE);
                mview.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
        }, 1500);
    }
}