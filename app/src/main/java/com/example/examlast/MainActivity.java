package com.example.examlast;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private Button btnFilterSalary, btnAdd;
    private EmployeeAdapter adapter;
private EditText edtSearch;
    private List<Employee> allEmployees;
    private List<Employee> displayEmployees;

    private boolean isFilterSalaryActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        //btnFilterSalary = findViewById(R.id.btnFilterSalary);
        btnAdd = findViewById(R.id.btnAdd);
        edtSearch=findViewById(R.id.edtSearch);
        allEmployees = new ArrayList<>();
        displayEmployees = new ArrayList<>();
        adapter = new EmployeeAdapter(this, displayEmployees);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EmployeeDetail.class)));
/*
        btnFilterSalary.setOnClickListener(v -> {
            isFilterSalaryActive = !isFilterSalaryActive;
            if (isFilterSalaryActive) {
                btnFilterSalary.setText("Hủy Lọc Lương");
                btnFilterSalary.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                btnFilterSalary.setText("Lọc Lương > 15M");
                btnFilterSalary.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
            applyFilters(edtSearch.getText().toString());
        });
*/
        loadData();
    }


    private void loadData() {
        FirebaseFirestore.getInstance().collection("Employees").addSnapshotListener((value, error) -> {
            if (value != null) {
                allEmployees.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Employee emp = doc.toObject(Employee.class);
                    emp.setId(doc.getId());
                    allEmployees.add(emp);
                }
                applyFilters(edtSearch.getText().toString());
            }
        });
    }
/*
    private void applyFilters() {
        displayEmployees.clear();
        for (Employee emp : allEmployees) {

            boolean passSalary = !isFilterSalaryActive || emp.getSalary() > 16000000;

            if (passSalary) {
                displayEmployees.add(emp);
            }
        }
        adapter.notifyDataSetChanged();
    }
 */
    /*private void applyFilters(String keyword) {
        displayEmployees.clear();
        String input = keyword.trim();

        if (input.isEmpty()) {
            displayEmployees.addAll(allEmployees);
        } else {
            try {
                double minSalary = Double.parseDouble(input);

                for (Employee emp : allEmployees) {
                    if (emp.getSalary() > minSalary) {
                        displayEmployees.add(emp);
                    }
                }
            } catch (NumberFormatException e) {

            }
        }
        adapter.notifyDataSetChanged();
    }*/
    private void applyFilters(String keyword) {
        displayEmployees.clear();
        String lowerCaseKeyword = keyword.toLowerCase().trim();
        for (Employee emp : allEmployees) {

            if (emp.getFullName().toLowerCase().contains(lowerCaseKeyword)) {
                displayEmployees.add(emp);
            }
        }
        adapter.notifyDataSetChanged();
    }
}