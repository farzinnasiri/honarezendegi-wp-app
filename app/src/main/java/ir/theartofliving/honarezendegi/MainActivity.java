package ir.theartofliving.honarezendegi;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import io.github.meness.Library.Utils.ActionBarDrawerToggle;
import io.github.meness.Library.Utils.IntentUtility;
import io.github.meness.Library.Utils.Utility;
import ir.theartofliving.honarezendegi.Activities.FilterActivity;
import ir.theartofliving.honarezendegi.Activities.SearchActivity;
import ir.theartofliving.honarezendegi.Fragments.BlogFragment;
import ir.theartofliving.honarezendegi.Fragments.HomeFragment;
import ir.theartofliving.honarezendegi.Fragments.ProductsFragment;
import maes.tech.intentanim.CustomIntent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    TextView txtToolbarTitle;

    BlogFragment blogFragment = new BlogFragment();
    HomeFragment homeFragment = new HomeFragment();
    ProductsFragment productsFragment = new ProductsFragment();

    private final int FILTER_ACTIVITY_REQUEST_CODE = 987;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        txtToolbarTitle = (TextView) findViewById(R.id.txtToolbarTitle);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        txtToolbarTitle.setTypeface(G.face);
        setSupportActionBar(toolbar);

        Utility.ViewPagerAdapter adapter = new Utility.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(blogFragment);
        adapter.addFragment(productsFragment);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        int[] ids = {R.drawable.ic_web, R.drawable.ic_home, R.drawable.ic_products};

        tabLayout.getTabAt(0).setIcon(ids[1]);
        tabLayout.getTabAt(1).setIcon(ids[0]);
        tabLayout.getTabAt(2).setIcon(ids[2]);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setToolbarTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Utility.setNavigationItemTypeface(navigationView.getMenu(), G.face);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        blueFilterSetUp();
    }

//    private void blueFilterSetUp() {
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        blueFilterSwitch = (Switch) navigationView.getHeaderView(0)
//                .findViewById(R.id.drak_mode_swithc);
//
//        SharedPreferences sharedPrefs = getSharedPreferences(
//                "ir.theartofliving.honarezendegi", MODE_PRIVATE);
//
//        final boolean[] checked = {sharedPrefs.getBoolean("blueFilter", false)};
//
//        blueFilterSwitch.setChecked(checked[0]);
//
//        blueFilterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                checked[0] = isChecked;
//                applyFilter(checked[0]);
//
//            }
//        });
//        applyFilter(checked[0]);
//    }
//
//    private void applyFilter(boolean b) {
//        SharedPreferences.Editor editor = getSharedPreferences(
//                "ir.theartofliving.honarezendegi"
//                , MODE_PRIVATE).edit();
//        if (b) {
//
//
//            editor.putBoolean("blueFilter", true);
//
//        } else {
//            editor.putBoolean("blueFilter", false);
//        }
//        editor.apply();
//    }

    private void setToolbarTitle(int index) {
        if (index == 0)
            txtToolbarTitle.setText("خانه");
        else if (index == 1)
            txtToolbarTitle.setText("بلاگ");
        else if (index == 2)
            txtToolbarTitle.setText("محصولات آموزشی");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            if (viewPager.getCurrentItem() == 1) {
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "این قابلیت فقط در بلاگ فعال است", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.action_search) {
            if (viewPager.getCurrentItem() == 1) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("hint", "جستجو در بلاگ");
                startActivity(intent);
                CustomIntent.customType(this, "fadein-to-fadeout");
            } else {
                Toast.makeText(getApplicationContext(), "این قابلیت فقط در بلاگ فعال است", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_website) {
            IntentUtility.browseWebsite(MainActivity.this, "http://honarzendegi.com/");
        } else if (id == R.id.nav_about_us) {
            IntentUtility.browseWebsite(MainActivity.this, "http://honarzendegi.com/about-us/");
        } else if (id == R.id.nav_share) {
            IntentUtility.share(MainActivity.this, "http://honarzendegi.com/", "", "انتخاب کنید");
        } else if (id == R.id.nav_send_email) {
            IntentUtility.sendEmail(MainActivity.this, "info@honarzendegi.com", "", "Send Email To honarzendegi.com");
        } else if (id == R.id.nav_support) {
            IntentUtility.TelegramIntent(MainActivity.this, "FarzinNasiri");
        } else if (id == R.id.nav_instagram) {
            IntentUtility.InstagramIntent(MainActivity.this, "theartofliving_official");
        } else if (id == R.id.nav_telegram) {
            IntentUtility.TelegramIntent(MainActivity.this, "theartofliving_official");

        } else if (id == R.id.nav_web_contact_form) {
            IntentUtility.browseWebsite(MainActivity.this, "http://honarzendegi.com/contact-us/");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String text = data.getStringExtra("filterData");
            blogFragment.getFilterData(text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
