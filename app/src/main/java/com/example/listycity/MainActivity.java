package com.example.listycity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    RecyclerView cityRecyclerView;
    CityAdapter cityAdapter;
    ArrayList<String> dataList;
    Button addCityButton;

    // Firestore instance and reference to the "cities" collection
    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        cityRecyclerView = findViewById(R.id.city_recycler_view);
        addCityButton = findViewById(R.id.add_city_button);

        dataList = new ArrayList<>();
        cityAdapter = new CityAdapter(dataList);
        cityRecyclerView.setAdapter(cityAdapter);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCityDialog();
            }
        });

        // Get real-time updates from Firestore
        citiesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String city = doc.getId();
                        String province = doc.getString("Province");
                        Log.d("Firestore", String.format("City(%s, %s) fetched", city, province));
                        dataList.add(city + ", " + province);
                    }
                    cityAdapter.notifyDataSetChanged();
                }
            }
        });

        // Handle swiping to delete a city
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Not used, since we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position of the swiped item
                int position = viewHolder.getAdapterPosition();
                String cityState = dataList.get(position);
                String cityName = cityState.split(", ")[0];

                // Show confirmation dialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete City")
                        .setMessage("Are you sure you want to delete " + cityState + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the city from the list immediately
                            dataList.remove(position);
                            cityAdapter.notifyItemRemoved(position);

                            // Delete the city from Firestore
                            citiesRef.document(cityName).delete().addOnSuccessListener(aVoid -> {
                                Toast.makeText(MainActivity.this, "City deleted!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                // Handle the failure - if the deletion fails, add the item back to the list
                                dataList.add(position, cityState);
                                cityAdapter.notifyItemInserted(position);
                                Toast.makeText(MainActivity.this, "Failed to delete city: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Cancel the swipe and restore the item
                            cityAdapter.notifyItemChanged(position);
                        })
                        .show();
            }
        }).attachToRecyclerView(cityRecyclerView);

        // Set up click listener for city items
        cityRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, cityRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Launch ShowActivity with the selected city's information
                String cityState = dataList.get(position);
                Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                intent.putExtra("CITY_NAME", cityState);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // Handle long click if needed
            }
        }));
    }

    // Function to display the popup dialog to add city and state/province
    private void showAddCityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_city, null);
        builder.setView(dialogView);

        EditText cityInput = dialogView.findViewById(R.id.city_input);
        EditText stateInput = dialogView.findViewById(R.id.state_input);
        Button confirmButton = dialogView.findViewById(R.id.confirm_city_button);

        AlertDialog dialog = builder.create();
        dialog.show();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityInput.getText().toString().trim();
                String stateName = stateInput.getText().toString().trim();

                if (!cityName.isEmpty() && !stateName.isEmpty()) {
                    String fullCityState = cityName + ", " + stateName;

                    if (!dataList.contains(fullCityState)) {
                        addNewCity(new City(cityName, stateName));
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "City and state already exist!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter both city and state/province.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to add a new city to Firestore
    private void addNewCity(City city) {
        HashMap<String, String> data = new HashMap<>();
        data.put("Province", city.getProvinceName());

        citiesRef.document(city.getCityName()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                });
    }

    public boolean hasCity(String city) {
        return dataList.contains(city);
    }

    public void deleteCity(String city) {
        dataList.remove(city);
    }

    public int countCities() {
        return dataList.size();
    }



    // City class
    public class City {
        private String cityName;
        private String provinceName;

        public City(String cityName, String provinceName) {
            this.cityName = cityName;
            this.provinceName = provinceName;
        }

        public String getCityName() {
            return cityName;
        }

        public String getProvinceName() {
            return provinceName;
        }
    }
}
