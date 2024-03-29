package com.example.problem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.problem.Fragment.LoginFragment;
import com.example.problem.adapter.ProblemPostResyclerAdapter;
import com.example.problem.model.Constants;
import com.example.problem.model.Problem_Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 1;
    private Dialog dialog;
    private ImageView img;
    private EditText addres_problem;
    private EditText description_problem;
    private Button save_problem;
    private Button calsle_dialog;
    private Button open_camera;
    private Button location;
    private Button open_gallirey;
    private RecyclerView recyclerView;
    private ProblemPostResyclerAdapter adapter;
    private StorageReference mStorageRef;
    private Uri imageUri;
    private StorageTask upload_task;
    private FirebaseFirestore db;
    private Uri uri;
    private final int CAMERA_PIC_REQUEST = 100;
    private int PERMISSION_ID = 44;
    private int spinner_position = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private List<Problem_Model> list;
    private final String[] data = {"","dirty streets", "garbage not dumped", "quarter problems"};
    private Spinner spinner;
    ArrayAdapter<String> array_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        db = FirebaseFirestore.getInstance();
        array_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, data);
        array_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner=findViewById(R.id.spinner_post_activity);
        spinner.setAdapter(array_adapter);
        spinner.setPrompt("Title");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                adapter.getFilter().filter(data[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(view1 -> myDialog());
        recyclerView = findViewById(R.id.resView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(LoginFragment.user.getDisplayName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        readList();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void readList() {
        list = new ArrayList<>();
        try {
            db.collection(Constants.problem)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Problem_Model model = document.toObject(Problem_Model.class);
                                model.setId(document.getId());
                                list.add(model);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

        adapter = new ProblemPostResyclerAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void myDialog() {
        dialog = new Dialog(Objects.requireNonNull(this));
        dialog.setContentView(R.layout.post_dialog);
        dialog.setCancelable(false);
        findIdDialog();
        calsle_dialog.setOnClickListener(v -> dialog.cancel());
        save_problem.setOnClickListener(v -> btnSave());
        img.setOnClickListener(v -> openImage());

        Spinner spinner = dialog.findViewById(R.id.spinner);
        spinner.setAdapter(array_adapter);
        spinner.setPrompt("Title");
        spinner.setSelection(2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                spinner_position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        dialog.show();
    }

    private void btnSave() {
        if (addres_problem.getText().toString().isEmpty()) {
            addres_problem.setError(Constants.isEmpty);
        } else if (description_problem.getText().toString().isEmpty()) {
            addres_problem.setError(Constants.isEmpty);
        } else if (!description_problem.getText().toString().isEmpty() && !addres_problem.getText().toString().isEmpty()) {
            if (upload_task != null && upload_task.isInProgress()) {
                Toast.makeText(this, "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                Problem_Model model;
                if (LoginFragment.user.getPhotoUrl() == null) {
                    if (imageUri != null) {
                        model = new Problem_Model(LoginFragment.user.getDisplayName(), imageUri.toString(),
                                description_problem.getText().toString(), addres_problem.getText().toString(), UUID.randomUUID().toString(), data[spinner_position]);
                        uploadImage(model);
                    } else {
                        model = new Problem_Model(LoginFragment.user.getDisplayName(),
                                description_problem.getText().toString(), addres_problem.getText().toString(), UUID.randomUUID().toString(), data[spinner_position]);
                        uploadImage(model);
                    }
                } else {
                    if (imageUri != null) {
                        model = new Problem_Model(LoginFragment.user.getDisplayName(), LoginFragment.user.getPhotoUrl(),
                                imageUri, description_problem.getText().toString(), addres_problem.getText().toString(), UUID.randomUUID().toString(), data[spinner_position]);
                        uploadImage(model);
                    } else {
                        model = new Problem_Model(LoginFragment.user.getDisplayName(), LoginFragment.user.getPhotoUrl(),
                                description_problem.getText().toString(), addres_problem.getText().toString(), UUID.randomUUID().toString(), data[spinner_position]);
                        uploadImage(model);
                    }

                }


            }


            dialog.dismiss();
        } else {
            if (description_problem.getText().toString().isEmpty()) {
                description_problem.setError(Constants.isEmpty);
            }
            if (addres_problem.getText().toString().isEmpty()) {
                addres_problem.setError(Constants.isEmpty);
            }
        }
        readList();

    }

    private void findIdDialog() {
        img = dialog.findViewById(R.id.img_posts);
        addres_problem = dialog.findViewById(R.id.addres_problem);
        description_problem = dialog.findViewById(R.id.description_problem);
        save_problem = dialog.findViewById(R.id.save);
        calsle_dialog = dialog.findViewById(R.id.close_dialog);
        open_camera = dialog.findViewById(R.id.open_camera);
        location = dialog.findViewById(R.id.location);
        open_gallirey = dialog.findViewById(R.id.open_gallirey);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
        open_camera.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        });
        location.setOnClickListener(view -> getLastLocation());
        open_gallirey.setOnClickListener(view -> openImage());

    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        } else if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }

    private void uploadImage(Problem_Model model) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        try {
            if (imageUri != null) {
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(imageUri));
                upload_task = fileReference.putFile(imageUri);
                upload_task.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        uri = downloadUri;
                        model.setProblemimg(uri.toString());
                        db.collection(Constants.problem)
                                .add(model);

                        pd.dismiss();
                    } else {
                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            } else {
                Toast.makeText(PostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                db.collection(Constants.problem)
                        .add(model);
                pd.dismiss();
            }
            adapter.notifyDataSetChanged();
            fileList();
        } catch (Exception e) {
            pd.dismiss();
            Toast.makeText(this, "please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    addres_problem.setText("Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            addres_problem.setText("Latitude: " + mLastLocation.getLatitude() + " Longitude: " + mLastLocation.getLongitude());

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = null;
        if (search != null) {
            searchView = (SearchView) search.getActionView();
        }
        if (searchView != null) {
            assert searchManager != null;
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    adapter.getFilter().filter(s);
                    return true;
                }
            });
        }
        return true;
    }
}
