package com.projeto1.projeto1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.projeto1.projeto1.fragments.AboutFragment;
import com.projeto1.projeto1.fragments.AddProductFragment;
import com.projeto1.projeto1.fragments.ChooseMarketFragment;
import com.projeto1.projeto1.fragments.FavoritListFragment;
import com.projeto1.projeto1.fragments.LoginFragment;
import com.projeto1.projeto1.fragments.MainFragment;
import com.projeto1.projeto1.fragments.ProfileFragment;
import com.projeto1.projeto1.fragments.SaleDetailsFragment;
import com.projeto1.projeto1.fragments.UpdateSaleFragment;
import com.projeto1.projeto1.listeners.SaleListener;
import com.projeto1.projeto1.listeners.UserListener;
import com.projeto1.projeto1.listeners.MarketListener;
import com.projeto1.projeto1.listeners.ProductListener;
import com.projeto1.projeto1.models.Address;
import com.projeto1.projeto1.models.Market;
import com.projeto1.projeto1.models.Product;
import com.projeto1.projeto1.models.Sale;
import com.projeto1.projeto1.models.User;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ProductListener, MarketListener, UserListener, SaleListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private UpdateSaleFragment updateSaleFragment;

    private TextView info;
    private LoginButton loginButton;
    private MainFragment myMainFragment;
    private LoginFragment loginFragment;
    private CallbackManager mCallbackManager;
    private ArrayList<Product> products;
    private AppBarLayout mAppBarLayout;
    private ArrayList<Sale> sales;
    private ArrayList<Sale> salesSearch;
    private User user;
    private String searchStr;

    //Menu
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Fragment profileFragment;
    private Fragment addProductFragment;
    private String scanContent;
    private String currentCategory;
    private Market chosenMarket;
    private String idMarketSearch;
    private Address currentAddress;
    private BottomBar bottomBar;
    private String marketUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMenu();

        salesSearch = new ArrayList<>();
        myMainFragment = MainFragment.getInstance();
        loginFragment = LoginFragment.getInstance();
        profileFragment = ProfileFragment.getInstance();
        addProductFragment = AddProductFragment.getInstance();
        updateSaleFragment = UpdateSaleFragment.getInstance();
        scanContent = "";
        currentCategory = "";
        searchStr = "";
        idMarketSearch = "";
        marketUpdate = "";
        currentAddress = new Address("-","-","-","Campina Grande", "PB","Brasil", "-");


        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    changeFragment(MainFragment.getInstance(),MainFragment.TAG,true);
                }
                if (tabId == R.id.tab_user) {
                    callProfileFragment();
                }
                if (tabId == R.id.tab_code_scan) {
                    marketUpdate = "chooseMarket";
                    startScanCode();
                }
                if (tabId == R.id.tab_favorite) {
                    changeFragment(FavoritListFragment.getInstance(),FavoritListFragment.TAG,true);
                }

                if (tabId == R.id.tab_share_sales) {
                    marketUpdate = "chooseMarket";
                    changeFragment(ChooseMarketFragment.getInstance(),ChooseMarketFragment.TAG,true);
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    changeFragment(MainFragment.getInstance(),MainFragment.TAG,true);
                }
                if (tabId == R.id.tab_user) {
                    callProfileFragment();
                }
                if (tabId == R.id.tab_code_scan) {
                    marketUpdate = "chooseMarket";
                    startScanCode();
                }
                if (tabId == R.id.tab_favorite) {
                    changeFragment(FavoritListFragment.getInstance(),FavoritListFragment.TAG,true);
                }

                if (tabId == R.id.tab_share_sales) {
                    marketUpdate = "chooseMarket";
                    changeFragment(ChooseMarketFragment.getInstance(),ChooseMarketFragment.TAG,true);
                }
            }
        });

        if (SharedPreferencesUtils.getUser(getBaseContext()) == null) {
            Log.d(TAG, "not logged yet");
            initializeFacebookSdk();
            changeFragment(loginFragment, LoginFragment.TAG, false);

        } else {
            Log.d(TAG, "Already logged");
            changeFragment(myMainFragment, MainFragment.TAG, true);
            user = SharedPreferencesUtils.getUser(getBaseContext());
            Log.d(TAG, user.toString());

//            HerokuDeleteLikeTask mTask = new HerokuDeleteLikeTask("59ef75ac5f9b760004390237", user.getId(), getBaseContext(), String.format(getResources().getString(R.string.HEROKU_SALE_ENDPOINT)), this);

//            mTask.execute();

            //Mudando nome do usuário no menu
            //TODO dando erro
            /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            TextView nameTextView = (TextView) navigationView.findViewById(R.id.user_menu);
            nameTextView.setText(user.getName()); */
        }

        /** GET TODOS OS USUARIOS**/
        //HerokuGetUsersTask getUserTask = new HerokuGetUserTask(String.format(getResources().getString(R.string.HEROKU_USER_ENDPOINT)),this, user);
        //getUserTask.execute();


        /**POST DE PROMOÇÕES**/
        /*HerokuPostSalesTask mTask = new HerokuPostSalesTask(new Sale(), getBaseContext(), String.format(getResources().getString(R.string.HEROKU_SALE_ENDPOINT)));
        mTask.execute(); */

        /** POST DE PRODUTOS**/
        /*
        HerokuPostProductsTask mAuthTask = new HerokuPostProductsTask("594z81z04zz96z0004z93980", "0",
                getBaseContext(),String.format(getResources().getString(R.string.HEROKU_PRODUCT_ENDPOINT)));
        mAuthTask.execute(); */

        /** GET DE PRODUTOS **/
        /*

        HerokuGetProductsTask mAuthTask = new HerokuGetProductsTask(String.format(getResources().getString(R.string.HEROKU_PRODUCT_ENDPOINT)));
        mAuthTask.execute();
        */

        //Address address = new Address("Av. Pref. Severino Bezerra Cabral","1339", "Mirante", "Campina Grande", "PB", "Brasil", " ");
        //Localization localization = new Localization(0,0);
        //Market market = new Market(null,"Extra",address," ", "1234567890",localization);
        //HerokuPostMarketsTask marketsTask = new HerokuPostMarketsTask(market, getBaseContext(), String.format(getResources().getString(R.string.HEROKU_MARKET_ENDPOINT)), this);
        //marketsTask.execute();


        modifyActioonBar();

    }

    private void makingMenuClickable() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initMenu() {

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mAppBarLayout.setVisibility(View.VISIBLE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        makingMenuClickable();
        mToggle.setDrawerIndicatorEnabled(false);

        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        mToggle.setHomeAsUpIndicator(R.drawable.ic_hamburguer);


    }


    public void changeFragment(Fragment frag, String tag, boolean saveInBackstack) {

        try {
            FragmentManager manager = getSupportFragmentManager();
            //fragment not in back stack, create it.
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.content_layout, frag, tag);


            if (saveInBackstack) {
                Log.d(TAG, "Change Fragment: addToBackTack " + tag);
                transaction.addToBackStack(tag);
                transaction.commit();
            } else {
                Log.d(TAG, "Change Fragment: NO addToBackTack");
            }

            if (tag.equals(LoginFragment.TAG)) {
                mAppBarLayout.setVisibility(View.GONE);
                bottomBar.setVisibility(View.GONE);
            } else {
                mAppBarLayout.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
            }


            transaction.commit();
            // custom effect if fragment is already instanciated

        } catch (IllegalStateException exception) {
            Log.w(TAG, "Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
            return;
        }
        if (getSupportFragmentManager().findFragmentByTag(SaleDetailsFragment.TAG) != null) {
            getSupportFragmentManager().popBackStack(SaleDetailsFragment.TAG,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (getSupportFragmentManager().findFragmentByTag(UpdateSaleFragment.TAG) != null) {
                // I'm viewing Fragment C
                getSupportFragmentManager().popBackStack(UpdateSaleFragment.TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if (getSupportFragmentManager().findFragmentByTag(SaleDetailsFragment.TAG) != null) {
                    // I'm viewing Fragment C
                    getSupportFragmentManager().popBackStack(SaleDetailsFragment.TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            }
        }else super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            if (scanContent != null) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        changeFragment(ChooseMarketFragment.getInstance(), ChooseMarketFragment.TAG, true);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Produto lido!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }, 1000);
            }


        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }


    }


    public String getScanContent() {
        if (scanContent != null) return scanContent;
        else return "";
    }

    public void setScanContent(String str) {
        scanContent = str;
    }

    // Deve ser implementado para dar ação aos itens do menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.nav_home:
                changeFragment(MainFragment.getInstance(), MainFragment.TAG, false);
                break;

/*
            case R.id.nav_config:
                callProfileFragment();
                //Toast.makeText(getBaseContext(), R.string.not_ready, Toast.LENGTH_LONG).show();

                break;
*/

            case R.id.nav_about:
                changeFragment(AboutFragment.getInstance(), AboutFragment.TAG, true);
                break;

            case R.id.nav_logout:
                initializeFacebookSdk();
                LoginManager.getInstance().logOut();
                SharedPreferencesUtils.setUser(this, null);
                changeFragment(loginFragment, LoginFragment.TAG, false);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callProfileFragment() {
        SharedPreferencesUtils.setUserSelected(getBaseContext(), user);
        changeFragment(profileFragment, ProfileFragment.TAG, true);
    }


    /**
     * Inicia as definições da ActionBar para esse fragment
     */
    private void modifyActioonBar() {
        android.support.v7.app.ActionBar mActionbar = (this.getSupportActionBar());
        mActionbar.setTitle("");
        mActionbar.setElevation(0);
        mActionbar.setDisplayHomeAsUpEnabled(true);//if true displays the left menu
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public CallbackManager initializeFacebookSdk() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        return mCallbackManager;
    }


    public ArrayList<Product> getProducts() {
        return products;
    }


    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    @Override
    public void OnGetProductsReady(boolean ready, ArrayList<Product> products) {
        setProducts(products);
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

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public ArrayList<Sale> getSales() {
        return sales;
    }

    public void setSales(ArrayList<Sale> sales) {
        this.sales = sales;
    }


    @Override
    public void OnGetMarketsReady(boolean ready, ArrayList<Market> products) {

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

    public ArrayList<Sale> getSalesSearch() {
        return salesSearch;
    }

    public void setSalesSearch(ArrayList<Sale> salesSearch) {
        this.salesSearch = salesSearch;
    }

    public void startScanCode() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();

    }

    public String getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }

    public void setChosenMarket(Market chosenMarket) {
        this.chosenMarket = chosenMarket;
    }

    public Market getChosenMarket() {
        return chosenMarket;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    @Override
    public void OnGetAllUsersFinished(boolean ready, ArrayList<User> users) {

    }

    @Override
    public void OnGetUserFinished(boolean find, User user) {

    }

    @Override
    public void OnPostUserFinished(boolean finished) {

    }

    @Override
    public void OnAddFavoriteSaleFinished(boolean finished) {

    }

    @Override
    public void OnRemoveFavoriteSaleFinished(boolean finished) {

    }

    @Override
    public void OnPutUserFinished(boolean finished) {

    }

    @Override
    public void OnGetSalesReady(boolean ready, ArrayList<Sale> sales) {

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

    public void setIdMarketSearch(String idMarketSearch) {
        this.idMarketSearch = idMarketSearch;
    }

    public String getIdMarketSearch() {
        return idMarketSearch;
    }

    public String getMarketUpdate() {
        return marketUpdate;
    }

    public void setMarketUpdate(String marketUpdate) {
        this.marketUpdate = marketUpdate;
    }

    public BottomBar getBottomBar() {
        return bottomBar;
    }
}