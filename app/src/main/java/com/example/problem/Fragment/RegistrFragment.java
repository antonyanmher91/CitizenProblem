package com.example.problem.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.problem.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class RegistrFragment extends Fragment {
    private EditText name;
    private EditText surname;
    private EditText password;
    private EditText repassword;
    private EditText mail;
    private Button sing_up;
    private FirebaseAuth mAuth;
    private static final int IMAGE_REQUEST = 1;
    private Uri image_uri;
    private StorageTask upload_task;
    private StorageReference mStorageRef;
    private FirebaseUser user;
    private ImageView img;

    public RegistrFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registr, container, false);
        findId(view);
        mAuth = FirebaseAuth.getInstance();
        img = view.findViewById(R.id.img);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        img.setOnClickListener(v -> openImage());
        sing_up.setOnClickListener(v -> clickButton());
        return view;
    }

    private void clickButton() {
        if (chekRegistr()) {
            if (upload_task != null && upload_task.isInProgress()) {
                Toast.makeText(getContext(), "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                sing_up(mail.getText().toString(), password.getText().toString());
            }

        }

    }

    private boolean chekRegistr() {
        if (name.getText().toString().isEmpty() || name.length() < 2) {
            name.setError("Name Empty");
            return false;
        }
        if (surname.getText().toString().isEmpty() || surname.length() < 2) {
            surname.setError("Surname Empty");
            return false;
        }
        if (mail.getText().toString().isEmpty()) {
            mail.setError("Field shouldn't be empty");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches()) {
            mail.setError("Enter a valid email address");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Field shouldn't be empty");
            return false;
        } else if (password.length() < 6) {
            password.setError("Password must not be less than 6 characters");
            return false;
        }
        if (!repassword.getText().toString().equals(password.getText().toString())) {
            repassword.setError("Password does not match");
        }
        return true;
    }

    private void sing_up(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        uploadImage();

                    }

                });
    }

    private void findId(View view) {
        name = view.findViewById(R.id.name);
        surname = view.findViewById(R.id.surname);
        password = view.findViewById(R.id.password);
        repassword = view.findViewById(R.id.repassword);
        mail = view.findViewById(R.id.mail);
        sing_up = view.findViewById(R.id.sing_up);
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
        if (requestCode == IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            image_uri = data.getData();
            img.setImageURI(image_uri);


        }
    }

    private void uploadImage() {


        if (image_uri != null) {
            final ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("Uploading");
            pd.show();
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(image_uri));
            upload_task = fileReference.putFile(image_uri);
            upload_task.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name.getText().toString())
                            .setPhotoUri(Uri.parse(mUri))
                            .build();
                    user.updateProfile(profileUpdates);
                    pd.dismiss();
                    getFragmentManager().beginTransaction().replace(R.id.main_conteneier, new LoginFragment()).commit();
                } else {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            });
        } else {
            getFragmentManager().beginTransaction().replace(R.id.main_conteneier, new LoginFragment()).commit();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}
