package com.example.allamvizsga;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class CreateNewClass extends AppCompatActivity {
    EditText className;
    private String currentTanarID;
    Spinner categoryOfTheClass;
    Spinner specificClassName;
    TextView descriptionForTheClass;
    String currentUserId;
    TextView gradingForTheClass;

    Button createClassButton;
    String tantargyID;

    ImageView blueBackground;
    private Map<String, List<Integer>> iconMap = new HashMap<>();
    private List<UserData.Tantargyak> tantargyakList = new ArrayList<>();
    Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_class_fragment2);
        SharedPreferences sharedPreferences = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("currentUserId", "");
        UserData.getCurrentTanarID(currentUserId, tanarID -> currentTanarID = tanarID);
        random = new Random();

        blueBackground = findViewById(R.id.blurImageView);
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(1, 3));

        Glide.with(this)
                .load(R.drawable.background_for_dashboard)
                .apply(requestOptions)
                .into(blueBackground);

        className = findViewById(R.id.newClassName);
        categoryOfTheClass = findViewById(R.id.categoryOfTheClass);
        descriptionForTheClass = findViewById(R.id.descriptionForTheClass);
        gradingForTheClass = findViewById(R.id.gradingForTheClass);

        createClassButton = findViewById(R.id.createClassButton);

        specificClassName = findViewById(R.id.specificClassName);

        //Setting up the spinner and map for specific class names to use in second spinner to choose from
        String[] allCategoryForCall = {"Alapvető tantárgyak", "Társadalomtudományok", "Nyelvészet és irodalom", "Művészet és kreatív tantárgyak", "Tudomány és technológia", "Testnevelés és sport", "Vallás és etika", "Üzleti és gazdasági tanulmányok", "Egyéb tantárgyak"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allCategoryForCall);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryOfTheClass.setAdapter(adapter);

        Map<String, String[]> specificClassNameMap = new HashMap<>();
        specificClassNameMap.put("Alapvető tantárgyak", new String[]{"Matematika", "Biológia", "Fizika"});
        specificClassNameMap.put("Társadalomtudományok", new String[]{"Történelem", "Politika", "Szociológia"});
        specificClassNameMap.put("Nyelvészet és irodalom", new String[]{"Angol irodalom", "Magyar irodalom", "Német irodalom"});
        specificClassNameMap.put("Művészet és kreatív tantárgyak", new String[]{"Képzőművészet", "Zene", "Dráma"});
        specificClassNameMap.put("Tudomány és technológia", new String[]{"Informatika", "Mérnöki tudományok", "Kémia"});
        specificClassNameMap.put("Testnevelés és sport", new String[]{"Futás", "Úszás", "Kosárlabda"});
        specificClassNameMap.put("Vallás és etika", new String[]{"Kereszténység", "Iszlám", "Buddhizmus"});
        specificClassNameMap.put("Üzleti és gazdasági tanulmányok", new String[]{"Pénzügyek", "Marketing", "Vállalkozás"});
        specificClassNameMap.put("Egyéb tantárgyak", new String[]{"Programozás", "Pszichológia", "Filozófia"});

        populateIconMap();

        categoryOfTheClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=allCategoryForCall[position];
                String[]optionsForSecondSpinner=specificClassNameMap.get(selectedItem);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, optionsForSecondSpinner);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                specificClassName.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = className.getText().toString();
                String category = categoryOfTheClass.getSelectedItem().toString();
                String description = descriptionForTheClass.getText().toString();
                String grading = gradingForTheClass.getText().toString();
                String image = "";
                if (name.isEmpty() || category.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Kötelező kitölteni a név és a kategoria mezőt", Toast.LENGTH_SHORT).show();

                } else {
                    String selectedClassName = specificClassName.getSelectedItem().toString();
                    List<Integer> iconList = iconMap.get(selectedClassName);
                    Integer iconResource= iconList.get(random.nextInt(iconList.size()));
                    Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), iconResource);
                    tantargyID = tantargyIDGenerator(tantargyakList);
                    saveUserData(name, category, description, grading, iconBitmap, currentTanarID, tantargyID);
                }


            }

        });


    }
    //ToDo Test and rand img upload as icons

    public void saveUserData(String name, String category, String description, String grading, Bitmap iconBitmap, String tanarID, String tantargyID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String tantargyakIDs = databaseReference.child("tantargyak").push().getKey();

        //Image upload onto FireStorage with tantargyID+_image as name
        uploadClassImage(tantargyID,iconBitmap, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString(); // Image URL-je

               //Create tantargy object
                UserData.Tantargyak tantargy = new UserData.Tantargyak(name, category, description, grading, imageUrl, tanarID, tantargyID);

                //Save tantargy object to database
                databaseReference.child("tantargyak").child(tantargyakIDs).setValue(tantargy)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Registration", "Sikeresen létrehozta a tantargyat");
                                Toast.makeText(CreateNewClass.this, "Sikeresen létrehozta a tantargyat", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Registration", "Sikertelen tantárgy létrehozás", e);
                                Toast.makeText(getApplicationContext(), "Sikertelen tantárgy létrehozás", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }




    public String tantargyIDGenerator(List<UserData.Tantargyak> tantargyakList) {
        Random random = new Random();
        String tantargyID;
        int randomNumber;
        do {
            randomNumber = random.nextInt(1000);
            tantargyID = "C-" + randomNumber;
        } while (containstantargyID(tantargyakList, tantargyID));
        return tantargyID;
    }
    private boolean containstantargyID(List<UserData.Tantargyak> tantargyakList, String tantargyID) {
        for (UserData.Tantargyak tantargyak : tantargyakList) {
            if (tantargyak.getTantargyID().equals(tantargyID)) {
                return true;
            }
        }
        return false;
    }
    public void populateIconMap(){
        iconMap.put("Matematika",Arrays.asList(R.drawable.mathematics, R.drawable.calculating, R.drawable.calculation));
        iconMap.put("Biológia",Arrays.asList(R.drawable.biology, R.drawable.bacteria, R.drawable.book_biologic));
        iconMap.put("Fizika",Arrays.asList(R.drawable.atom, R.drawable.relativity, R.drawable.einstein));
        iconMap.put("Történelem",Arrays.asList(R.drawable.hourglass, R.drawable.history_book, R.drawable.parchment));
        iconMap.put("Politika",Arrays.asList(R.drawable.politician, R.drawable.politics2, R.drawable.politics));
        iconMap.put("Szociológia",Arrays.asList(R.drawable.sociology, R.drawable.sociology2, R.drawable.network));
        iconMap.put("Angol irodalom",Arrays.asList(R.drawable.eng1, R.drawable.eng, R.drawable.brain));
        iconMap.put("Magyar irodalom",Arrays.asList(R.drawable.flag_hungarian, R.drawable.flag_hungarian2));
        iconMap.put("Német irodalom",Arrays.asList(R.drawable.german, R.drawable.germany, R.drawable.language_german));
        iconMap.put("Képzőművészet",Arrays.asList(R.drawable.museum, R.drawable.palette, R.drawable.art));
        iconMap.put("Zene",Arrays.asList(R.drawable.music_notes, R.drawable.musical_note, R.drawable.guitar));
        iconMap.put("Dráma",Arrays.asList(R.drawable.theater, R.drawable.theater2, R.drawable.shakespeare));
        iconMap.put("Informatika",Arrays.asList(R.drawable.informatics, R.drawable.informatics2, R.drawable.real_time));
        iconMap.put("Mérnöki tudományok",Arrays.asList(R.drawable.engineering, R.drawable.engineering2, R.drawable.building_construction));
        iconMap.put("Kémia",Arrays.asList(R.drawable.chemical, R.drawable.chemistry2, R.drawable.search));
        iconMap.put("Futás",Arrays.asList(R.drawable.chase, R.drawable.run, R.drawable.running));
        iconMap.put("Úszás",Arrays.asList(R.drawable.swimming, R.drawable.swimming_championship, R.drawable.swimming_pool));
        iconMap.put("Kosárlabda",Arrays.asList(R.drawable.basketball, R.drawable.basketball2, R.drawable.basketball3));
        iconMap.put("Kereszténység",Arrays.asList(R.drawable.congregation, R.drawable.christianity, R.drawable.cross));
        iconMap.put("Iszlám",Arrays.asList(R.drawable.mosque, R.drawable.ramadan, R.drawable.praying));
        iconMap.put("Buddhizmus",Arrays.asList(R.drawable.monk, R.drawable.om, R.drawable.buddha));
        iconMap.put("Pénzügyek",Arrays.asList(R.drawable.budget, R.drawable.asset_management, R.drawable.financial_profit));
        iconMap.put("Marketing",Arrays.asList(R.drawable.social_media, R.drawable.digital_campaign, R.drawable.digital_marketing));
        iconMap.put("Vállalkozás",Arrays.asList(R.drawable.company, R.drawable.coding2, R.drawable.binary));
        iconMap.put("Programozás",Arrays.asList(R.drawable.company, R.drawable.coding2, R.drawable.binary));
        iconMap.put("Pszichológia",Arrays.asList(R.drawable.positive_thinking, R.drawable.psychology, R.drawable.science_book));
        iconMap.put("Filozófia",Arrays.asList(R.drawable.light_bulb, R.drawable.philosophy, R.drawable.philosophy));


    }
    public void uploadClassImage(String classId, Bitmap imageBitmap, OnSuccessListener<Uri> onSuccessListener) {
        //Image reformat as byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

       //Image upload onto FireStorage
        String imageName = classId + "_image.jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imageName);
        UploadTask uploadTask = storageRef.putBytes(imageData);

     //With successfully uploded we gave back the url
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
        }).addOnFailureListener(e -> {
            Log.e("Upload", "Hiba a kép feltöltésekor", e);
        });
    }





}