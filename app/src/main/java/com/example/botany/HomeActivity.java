package com.example.botany;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    TextView weather_report,location_name,userNameTextView;
    Button logout,addPlant;
    CircleImageView profilePicture;
    FirebaseUser fUser,plantUser;
    FirebaseAuth fAuth,plantAuth;
    DatabaseReference fRef,plantRef;
    RecyclerView plantRecyclerview;

    //creating weather class
    class Weather extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... address) {

            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1){
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addPlant = (Button) findViewById(R.id.addPlant);
        plantRecyclerview = (RecyclerView)  findViewById(R.id.plantRecyclerview);

        plantAuth = FirebaseAuth.getInstance();
        plantUser = plantAuth.getCurrentUser();
        plantRef = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(plantUser.getUid());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        plantRecyclerview.setHasFixedSize(true);
        plantRecyclerview.setLayoutManager(linearLayoutManager);

        //add plant popup action on button press
        addPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                additem();
            }
        });

        //calling weather api integration method to display text on homescreen
        weatherSearch();

        logout = (Button) findViewById(R.id.logout);

        //fetch user's name and profile picture from firebase database and storage
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        profilePicture = (CircleImageView) findViewById(R.id.profilePicture);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fRef = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(fUser.getUid());
        fRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userData = snapshot.getValue(UserHelperClass.class);
                assert userData != null;
                userNameTextView.setText(userData.getName());
                if (userData.getImageURL().equals("default")){
                    profilePicture.setImageResource(R.drawable.profile_picture);
                }
                else {
                    Glide.with(getApplicationContext()).load(userData.getImageURL()).into(profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        //for user to logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutAction();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        plantRef = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(plantUser.getUid()).child("PlantData");
        FirebaseRecyclerOptions<PlantDataHelperClass> options = new FirebaseRecyclerOptions.Builder<PlantDataHelperClass>()
                .setQuery(plantRef, PlantDataHelperClass.class)
                .build();

        FirebaseRecyclerAdapter<PlantDataHelperClass,MyViewHolder> adapter = new FirebaseRecyclerAdapter<PlantDataHelperClass, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull PlantDataHelperClass model) {
                holder.setPlantName("Plant Name: "+model.getPlantName());
                holder.setPlantHeight("Plant Height: "+model.getPlantHeight()+"cm");
                holder.setPlantType("Type of plant: "+model.getPlantType());

                switch (model.getPlantType()){
                    case "Select Plant Type":
                        holder.pImage.setImageResource(R.drawable.defaultplant);
                        break;
                    case "Flowering Plant":
                        holder.pImage.setImageResource(R.drawable.flower);
                        break;
                    case "Common House Plant":
                        holder.pImage.setImageResource(R.drawable.common);
                        break;
                    case "Low Light Plant":
                        holder.pImage.setImageResource(R.drawable.lowlight);
                        break;
                    case "Cactus Plant":
                        holder.pImage.setImageResource(R.drawable.cactus);
                        break;
                    case "Climbing/Trailing Plant":
                        holder.pImage.setImageResource(R.drawable.climbing);
                        break;
                }
            }
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new MyViewHolder(view);
            }
        };
        plantRecyclerview.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


    }

    //method for add plant pop up panel
    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.add_plant_popup, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        plantAuth = FirebaseAuth.getInstance();
        plantUser = plantAuth.getCurrentUser();
        plantRef = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(plantUser.getUid()).child("PlantData");

        final Spinner plant_type_spinner = myView.findViewById(R.id.plant_type);
        final EditText plant_name = myView.findViewById(R.id.plant_name);
        final EditText plant_height = myView.findViewById(R.id.plant_height);
        final Button cancel_add_plant_btn = myView.findViewById(R.id.cancel_add_plant_btn);
        final Button add_plant_btn = myView.findViewById(R.id.add_plant_btn);

        add_plant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pName = plant_name.getText().toString();
                String pHeight = plant_height.getText().toString();
                String pType = plant_type_spinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(pName)){
                    plant_name.setError("Plant name is required");
                    return;
                }

                if(TextUtils.isEmpty(pHeight)){
                    plant_height.setError("Plant height is required");
                    return;
                }

                if(pType.equals("Select Plant Type")){
                    Toast.makeText(HomeActivity.this, "SELECT VALID PLANT TYPE", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {

                    String id = plantRef.push().getKey();
                    PlantDataHelperClass pData = new PlantDataHelperClass(pName,pType,id,pHeight);
                    plantRef.child(id).setValue(pData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(HomeActivity.this, "Plant added successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(HomeActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

                dialog.dismiss();
            }
        });

        cancel_add_plant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //method for weather api integration in the homescreen
    public void weatherSearch() {
        weather_report = (TextView) findViewById(R.id.weather_report);
        location_name = (TextView) findViewById(R.id.location_name);

        //fetch user's country location from firebase database and use that to input into the weather api location name
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        fRef = FirebaseDatabase.getInstance("https://botany-iub-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(fUser.getUid());
        fRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserHelperClass userData = snapshot.getValue(UserHelperClass.class);
                assert userData != null;
                location_name.setText(userData.getCountry());
                if (userData.getCountry().equals(null)){
                    location_name.setText("No Location");
                }
                else {

                    String cName = userData.getCountry();

                    String content;
                    Weather weather = new Weather();
                    try {
                        content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" +
                                cName+"&units=metric&appid=1164903a1b37102502b2dd2e31b379b2").get();
                        Log.i("contentData",content);

                        JSONObject jsonObject = new JSONObject(content);
                        String weatherData = jsonObject.getString("weather");
                        String mainTemperature = jsonObject.getString("main");
                        JSONArray array = new JSONArray(weatherData);

                        String main = "";
                        String description = "";
                        String temperature = "";

                        for(int i=0; i<array.length(); i++){
                            JSONObject weatherPart = array.getJSONObject(i);
                            main = weatherPart.getString("main");
                            description = weatherPart.getString("description");
                        }

                        JSONObject mainPart = new JSONObject(mainTemperature);
                        temperature = mainPart.getString("temp");

                        Log.i("Temperature",temperature);

                        String resultText = "Weather condition: "+main+"\nTemperature: "+temperature +"°C";
                        weather_report.setText(resultText);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Weather Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //logout button action method
    private void logoutAction() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView pImage;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            mView = itemView;
            pImage = itemView.findViewById(R.id.retrieved_plant_image);
        }

        public void setPlantName (String plantName){
            TextView plant_Name = mView.findViewById(R.id.retrieved_plant_name);
            plant_Name.setText(plantName);
        }

        public void setPlantType (String plantType){
            TextView plant_Type = mView.findViewById(R.id.retrieved_plant_type);
            plant_Type.setText(plantType);
        }

        public void setPlantHeight (String plantHeight){
            TextView plant_Height = mView.findViewById(R.id.retrieved_plant_height);
            plant_Height.setText(plantHeight);
        }

    }

}