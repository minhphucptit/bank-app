package com.example.mikebanks.bankscorpfinancial;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mikebanks.bankscorpfinancial.Model.Account;
import com.example.mikebanks.bankscorpfinancial.Model.Profile;
import com.example.mikebanks.bankscorpfinancial.Model.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

//    private ImageView imgTime;
//    private TextView txtWelcome;
//    private TextView txtMessage;
//    private Button btnAddAccount;
    private Spinner spnAccount;
    private float deposit=0,transfer=0,payment=0;

    private Profile userProfile;
    ArrayList<PieEntry> entries;
    private ArrayList<Account> accounts;
    private ArrayAdapter<Account> accountAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_dashboard, container, false);
        setupViews();
//        imgTime = rootView.findViewById(R.id.img_time);
//        txtWelcome = rootView.findViewById(R.id.txt_welcome);
//        txtMessage = rootView.findViewById(R.id.txt_details_msg);
//        btnAddAccount = rootView.findViewById(R.id.btn_add_account);
        spnAccount = rootView.findViewById(R.id.spn_accounts1);
        accounts = userProfile.getAccounts();
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spnAccount.setAdapter(accountAdapter);


        PieChart pieChart = rootView.findViewById(R.id.any_chart_view);
        entries = new ArrayList<>();

        setValueChart(0);

        PieDataSet dataSet = new PieDataSet(entries,"");
        ArrayList<Integer> colors = new ArrayList<>();
        for(int color : ColorTemplate.MATERIAL_COLORS){
            colors.add(color);
        }
        for(int color : ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        dataSet.setColors(colors);
        dataSet.setDrawValues(true);

        dataSet.setValueTextSize(20f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText("TRANSACTIONS");
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(20f);
        spnAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setValueChart(i);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return rootView;

    }

    private void setValueChart(int i) {
        payment=0;transfer=0;deposit=0;
        entries.clear();

        for (int j = 0; j < userProfile.getAccounts().get(i).getTransactions().size(); j++) {
            Transaction transaction = userProfile.getAccounts().get(i).getTransactions().get(j);
            if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.PAYMENT) {
                payment++;
            } else if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.TRANSFER) {
                transfer++;
            } else if (transaction.getTransactionType() == Transaction.TRANSACTION_TYPE.DEPOSIT) {
                deposit++;
            }
        }

        if(deposit>0){
            entries.add(new PieEntry(deposit,"DEPOSIT"));
        }
        if(payment>0){
            entries.add(new PieEntry(payment,"PAYMENT"));
        }
        if(transfer>0){
            entries.add(new PieEntry(transfer,"TRANSFER"));
        }


    }


    private void setupViews() {

        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        Profile userProfile = gson.fromJson(json, Profile.class);
        this.userProfile = userProfile;

//        btnAddAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("DisplayAccountDialog", true);
//                ((DrawerActivity) getActivity()).manualNavigation(DrawerActivity.manualNavID.ACCOUNTS_ID, bundle);
//            }
//        });

//        if (userProfile.getAccounts().size() == 0) {
//            txtMessage.setVisibility(View.VISIBLE);
//            btnAddAccount.setVisibility(View.VISIBLE);
//            txtMessage.setText("You do not have any accounts, click below to add an account");
//        } else {
//            txtMessage.setVisibility(View.GONE);//TEMP to clear field
//            btnAddAccount.setVisibility(View.GONE);
//
//
//        }

        StringBuilder welcomeString = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

//        if (timeOfDay >= 5 && timeOfDay < 12) {
//            welcomeString.append(getString(R.string.good_morning));
//            imgTime.setImageResource(R.drawable.morning_icon_96);
//        } else if (timeOfDay >= 12 && timeOfDay < 17) {
//            welcomeString.append(getString(R.string.good_afternoon));
//            imgTime.setImageResource(R.drawable.day_icon_96);
//        } else {
//            welcomeString.append(getString(R.string.good_evening));
//            imgTime.setImageResource(R.drawable.night_icon_96);
//        }

        welcomeString.append(", ")
                .append(userProfile.getFirstName())
                .append(". Welcome to the Bank App Demo. ")
                .append(getString(R.string.happy))
                .append(" ");

        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String[] days = getResources().getStringArray(R.array.days);
        String dow = "";

        switch(day) {
            case Calendar.SUNDAY:
                dow = days[0];
                break;
            case Calendar.MONDAY:
                dow = days[1];
                break;
            case Calendar.TUESDAY:
                dow = days[2];
                break;
            case Calendar.WEDNESDAY:
                dow = days[3];
                break;
            case Calendar.THURSDAY:
                dow = days[4];
                break;
            case Calendar.FRIDAY:
                dow = days[5];
                break;
            case Calendar.SATURDAY:
                dow = days[6];
                break;
            default:
                break;
        }

        welcomeString.append(dow)
                .append(".");

//        txtWelcome.setText(welcomeString.toString());
    }

}
