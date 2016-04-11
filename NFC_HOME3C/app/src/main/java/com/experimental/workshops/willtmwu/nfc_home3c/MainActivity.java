package com.experimental.workshops.willtmwu.nfc_home3c;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.factory.NDEFRecordFactory;
import com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.model.BaseRecord;
import com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.model.NDEFExternalType;
import com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.model.RDTSpRecord;
import com.experimental.workshops.willtmwu.nfc_home3c.nfc_spec.model.RDTTextRecord;

import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NfcAdapter nfcAdpt;
    PendingIntent nfcPendingIntent;
    IntentFilter[] intentFiltersArray;

    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            /*public void onDrawerClosed(View view) {
                getActionBar().setTitle("HOME3C");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("HOME3C");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }*/
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            mainFragment = new MainFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, mainFragment).commit();
            navigationView.setCheckedItem(0);
        }


        //lView = (ListView) findViewById(R.id.recList);


        //recNumberTxt = (TextView) findViewById(R.id.recNumber);


        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
        IntentFilter tagIntentFilter =
        new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            tagIntentFilter.addDataType("text/plain");
            intentFiltersArray = new IntentFilter[]{tagIntentFilter};
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Nfc", "New intent");
        getTag(intent);
    }

    private void handleIntent(Intent i) {
        Log.d("NFC", "Intent [" + i + "]");
        getTag(i);
    }

    protected void onResume() {
        super.onResume();
        nfcAdpt.enableForegroundDispatch(
                this,
                nfcPendingIntent,
                intentFiltersArray,
                null);
        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdpt.disableForegroundDispatch(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getFragmentManager();
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);


        switch (item.getItemId()){
            case R.id.nav_scan:
                mainFragment = new MainFragment();
                //Bundle args = new Bundle();
                //args.putString(MainFragment.ARG_PLANET_NUMBER, "Main");
                //mainFragment.setArguments(args);

                fragmentManager.beginTransaction().replace(R.id.content_frame, mainFragment).commit();
                nav.setCheckedItem(item.getItemId());
                break;
            case R.id.nav_recent:

                Fragment recentFragment = new RecentFragment();

                fragmentManager.beginTransaction().replace(R.id.content_frame, recentFragment).commit();
                nav.setCheckedItem(item.getItemId());
                break;
            default:
                int id = item.getItemId();
                if (id == R.id.nav_settings) {
                    // start the SettingsActivity
                } else if (id == R.id.nav_share) {
                    // setup to call system
                }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class MainFragment extends Fragment {
        //public static final String ARG_PLANET_NUMBER = "planet_number";

        private TextView recNumberTxt;
        private ListView lView;

        public MainFragment() {
            // Empty constructor required for fragment subclasses


            /*ArticleFragment articleFrag = (ArticleFragment)
                    getSupportFragmentManager().findFragmentById(R.id.article_fragment);
            articleFrag.updateArticleView(position);*/
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            //int i = getArguments().getInt(ARG_PLANET_NUMBER);
            //String s = getArguments().getString(ARG_PLANET_NUMBER);

            /*if(s==null){
                s = "";
            }*/
            //getActivity().setTitle(s + " Fragment");

            //((TextView) rootView.findViewById(R.id.textView)).setText(s + "Fragment Loaded");
            recNumberTxt = (TextView) rootView.findViewById(R.id.textView);
            lView = (ListView) rootView.findViewById(R.id.listView);

            return rootView;
        }

        public void SetListView(NdefAdapter apt){
            if (lView != null) {
                lView.setAdapter(apt);
            }
        }

        public void SetTextView(String txt){
            if (recNumberTxt != null) {
                recNumberTxt.setText(txt);
            }
        }
    }

    public static class RecentFragment extends ListFragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public RecentFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recent, container, false);
            return rootView;
        }
    }



    private void getTag(Intent i) {
        if (i == null)
            return ;

        String type = i.getType();
        String action = i.getAction();
        List<BaseRecord> dataList = new ArrayList<BaseRecord>();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d("Nfc", "Action NDEF Found");
            Parcelable[] parcs = i.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            // List record


            for (Parcelable p : parcs) {
                NdefMessage msg = (NdefMessage) p;
                final int numRec = msg.getRecords().length;

                if (mainFragment != null){
                    mainFragment.SetTextView(String.valueOf(numRec));
                }

                NdefRecord[] records = msg.getRecords();
                for (NdefRecord record: records) {
                    BaseRecord result = NDEFRecordFactory.createRecord(record);
                    if (result instanceof RDTSpRecord)
                        dataList.addAll( ((RDTSpRecord) result).records);
                    else
                        dataList.add(result);

                }
            }

            NdefAdapter adpt = new NdefAdapter(dataList);
            if (mainFragment != null){
                mainFragment.SetListView(adpt);
            }

        }

    }



    // ListView adapter
    class NdefAdapter extends ArrayAdapter<BaseRecord> {
        List<BaseRecord> recordList;

        public NdefAdapter(List<BaseRecord> recordList) {
            super(MainActivity.this, R.layout.record_layout, recordList);
            this.recordList = recordList;
        }

        @Override
        public int getCount() {
            return recordList.size();
        }

        @Override
        public BaseRecord getItem(int position) {
            return recordList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Log.d("Nfc", "Get VIew");
            if (v == null) {
                LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inf.inflate(R.layout.record_layout, null);
            }

            TextView tnfTxt = (TextView) v.findViewById(R.id.tnfText);
            TextView recContentTxT = (TextView) v.findViewById(R.id.recCont);
            TextView typeTxt = (TextView) v.findViewById(R.id.typeTxt);
            TextView headTxt = (TextView) v.findViewById(R.id.header);

            BaseRecord record = recordList.get(position);
            tnfTxt.setText("" + record.tnf);
            headTxt.setText("MB:" + record.MB + " ME:" + record.ME + " SR:" + record.SR);

            recContentTxT.setText(record.toString());

            return v;

        }




        @Override
        public long getItemId(int position) {
            return recordList.get(position).hashCode();
        }
    }


}
