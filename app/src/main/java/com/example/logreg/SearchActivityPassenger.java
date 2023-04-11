package com.example.logreg;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchActivityPassenger extends Fragment {
    private SearchView searchStartView;
    private SearchView searchEndView;
    private Gson gson;
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> filteredCities = new ArrayList<>();
    private EditText editText1, editText2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SearchActivityPassenger() {
        // пустой конструктор
    }

    public static SearchActivityPassenger newInstance(String param1, String param2) {
        SearchActivityPassenger fragment = new SearchActivityPassenger();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        gson = new Gson();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.tb_city);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Type type = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
            ArrayList<ArrayList<String>> data = gson.fromJson(reader, type);
            reader.close();
            inputStream.close();
            for (ArrayList<String> cityData : data) {
                cities.add(cityData.get(0));
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка получения данных городов", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);
        searchStartView = view.findViewById(R.id.start_adress);
        searchEndView = view.findViewById(R.id.end_adress);
        ListView citiesListView = view.findViewById(R.id.cities_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, filteredCities);
        citiesListView.setAdapter(adapter);
        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // получаем текст выбранного элемента
                String selectedCity = filteredCities.get(i);
                // устанавливаем текст в SearchView
                if (searchStartView.hasFocus()) {
                    searchStartView.setQuery(selectedCity, true);
                } else if (searchEndView.hasFocus()) {
                    searchEndView.setQuery(selectedCity, true);
                }
            }
        });
        // слушатель изменений текста для searchStartView
        searchStartView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCities(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        // слушатель изменений текста для searchEndView
        searchEndView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCities(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }
    private void filterCities(String text) {
        filteredCities.clear();
        if (text.isEmpty()) {
            filteredCities.addAll(cities);
        } else {
            text = text.toLowerCase();
            for (String city : cities) {
                if (city.toLowerCase().contains(text)) {
                    filteredCities.add(city);
                }
            }
        }
    }
}
