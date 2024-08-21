package com.example.allamvizsga;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserData {
    Diakok diakok;
    Szulok szulok;
    static Tanarok tanarok;
    static Tantargyak tantargyak;
    static DocumentSnapshot documentSnapshot;


    public static Tantargyak getTantargyak() {
        return tantargyak;
    }

    public interface ResultCallback<T> {
        void onResult(T result);
    }
    public interface TanarIDCallback {
        void onResult(String tanarID);
    }
    public static void getCurrentTanarID(String id, TanarIDCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("tanarok").child(id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String tanarID = dataSnapshot.child("tanarID").getValue(String.class);
                    callback.onResult(tanarID);
                } else {
                    callback.onResult(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onResult(null);
            }
        });
    }


    //With the automaticlly generated FirebaseID search for Teacher and then check if he is a teacher by tanarID
    public static void isTanar(String id, ResultCallback<Boolean> callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("tanarok").child(id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isTanar = false;
                if (dataSnapshot.exists()) {
                    String tanarID = dataSnapshot.child("tanarID").getValue(String.class);
                    if (tanarID != null && tanarID.startsWith("T")) {
                        isTanar = true;
                    }
                }
                callback.onResult(isTanar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                callback.onResult(false);
            }
        });
    }

    public static void isDiak(String id, ResultCallback<Boolean> callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("diakok").child(id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isDiak = false;
                if (dataSnapshot.exists()) {
                    String diakID = dataSnapshot.child("diakID").getValue(String.class);
                    if (diakID != null && diakID.startsWith("D")) {
                        isDiak = true;
                    }
                }
                callback.onResult(isDiak);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onResult(false);
            }
        });
    }

    public static void isSzulo(String id, ResultCallback<Boolean> callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("szulok").child(id);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isSzulo = false;
                if (dataSnapshot.exists()) {
                    String szuloID = dataSnapshot.child("szuloID").getValue(String.class);
                    Log.d("szuloID", szuloID);
                    if (szuloID != null && szuloID.startsWith("P")) {
                        isSzulo = true;
                    }
                }
                callback.onResult(isSzulo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }



    public static class Diakok {

         String DiakID;
        String Nev;
        String Jelszo;
        String  Evfolyam;
        String Csoport;
        String Felhasznalonev;



        public Diakok(String nev, String jelszo, String evfolyam, String csoport, String felhasznalonev, String diakID) {
            DiakID = diakID;
            Nev = nev;
            Jelszo = jelszo;
            Evfolyam = evfolyam;
            Csoport = csoport;
            Felhasznalonev = felhasznalonev;
        }


        public  String getDiakID() {
            return DiakID;
        }


        public void setDiakID(String diakID) {
            DiakID = diakID;
        }


        public String getNev() {
            return Nev;
        }

        public void setNev(String nev) {
            Nev = nev;
        }

        public String getJelszo() {
            return Jelszo;
        }

        public void setJelszo(String jelszo) {
            Jelszo = jelszo;
        }

        public String getEvfolyam() {
            return Evfolyam;
        }

        public void setEvfolyam(String evfolyam) {
            Evfolyam = evfolyam;
        }

        public String getCsoport() {
            return Csoport;
        }

        public void setCsoport(String csoport) {
            Csoport = csoport;
        }



        public String getFelhasznalonev() {
            return Felhasznalonev;
        }

        public void setFelhasznalonev(String felhasznalonev) {
            Felhasznalonev = felhasznalonev;
        }
    }

    public static class Szulok{
        public Szulok(String nev,String felhasznalonev , String jelszo, String gyermekSzam, String szuloID) {
            Nev = nev;
            GyermekSzam = gyermekSzam;
            Jelszo = jelszo;
            Felhasznalonev = felhasznalonev;
            SzuloID = szuloID;
        }

        String SzuloID;
        String Nev;
        String GyermekSzam;
        String Jelszo;
        String Felhasznalonev;


        public void setSzuloID(String szuloID) {
            SzuloID = szuloID;
        }

        public String getSzuloID() {
            return SzuloID;
        }

        public String getNev() {
            return Nev;
        }

        public void setNev(String nev) {
            Nev = nev;
        }

        public String getGyermekSzam() {
            return GyermekSzam;
        }

        public void setGyermekSzam(int gyermekSzam) {
            GyermekSzam = String.valueOf(gyermekSzam);
        }

        public String getJelszo() {
            return Jelszo;
        }

        public void setJelszo(String jelszo) {
            Jelszo = jelszo;
        }

        public String getFelhasznalonev() {
            return Felhasznalonev;
        }

        public void setFelhasznalonev(String felhasznalonev) {
            Felhasznalonev = felhasznalonev;
        }

    }

    public static class Tanarok {
        String nev;
        String felhasznalonev;
        String jelszo;
        String tanarID;

        public Tanarok() {
            // Alapértelmezett konstruktor
        }

        public Tanarok(String nev, String felhasznalonev, String jelszo, String tanarID) {
            this.nev = nev;
            this.felhasznalonev = felhasznalonev;
            this.jelszo = jelszo;
            this.tanarID = tanarID;
        }

        public String getNev() {
            return nev;
        }

        public void setNev(String nev) {
            this.nev = nev;
        }

        public String getFelhasznalonev() {
            return felhasznalonev;
        }

        public void setFelhasznalonev(String felhasznalonev) {
            this.felhasznalonev = felhasznalonev;
        }

        public String getJelszo() {
            return jelszo;
        }

        public void setJelszo(String jelszo) {
            this.jelszo = jelszo;
        }

        public String getTanarID() {
            return tanarID;
        }

        public void setTanarID(String tanarID) {
            this.tanarID = tanarID;
        }
    }

    public static class Tantargyak{
        String Nev;
        String TantargyID;
        String Leiras;
        String TanarID;
        String kategoria;
        String kep;
        ArrayList<String> diakListInClass = new ArrayList<>();
        ArrayList<String>diakWantToJoin = new ArrayList<>();
        static List<Tantargyak> tantargyakList = new ArrayList<>();

        public static void addTantargy(Tantargyak tantargy) {
            tantargyakList.add(tantargy);
        }
        public static List<Tantargyak> getAllTantargyak() {
            return tantargyakList;
        }

        public Tantargyak() {
            // Üres konstruktor
        }

        public String getKep() {
            return kep;
        }

        public void setKep(String kep) {
            this.kep = kep;
        }

        public Tantargyak(String nev, String kategoria, String leiras, String jegyadas, String kep, String tanarID, String tantargyID) {
            this.Nev = nev;
            this.kategoria = kategoria;
            this.Leiras = leiras;
            this.kep = kep;
            this.TanarID = tanarID;
            this.TantargyID = tantargyID;
            this.diakWantToJoin = new ArrayList<>();

        }

        public void addDiakToClass(String diakId) {
            diakListInClass.add(diakId);
        }

        public void addDiakToWantToJoin(String diakId) {
            diakWantToJoin.add(diakId);
        }

        public ArrayList<String> getDiakWantToJoin() {
            return diakWantToJoin;
        }


        public void deleteDiakFromWantToJoin(String diakId) {
            diakWantToJoin.remove(diakId);
        }

        public ArrayList<String> getDiakListInClass() {
            return diakListInClass;
        }

        public void deleteDiakFromClass(String diakId) {
            diakListInClass.remove(diakId);
        }


        public String getNev() {
            return Nev;
        }

        public void setNev(String nev) {
            Nev = nev;
        }

        public String getTantargyID() {
            return TantargyID;
        }

        public void setTantargyID(String tantargyID) {
            TantargyID = tantargyID;
        }

        public String getLeiras() {
            return Leiras;
        }

        public void setLeiras(String leiras) {
            Leiras = leiras;
        }

        public String getTanarID() {
            return TanarID;
        }

        public void setTanarID(String tanarID) {
            TanarID = tanarID;
        }

        public String getKategoria() {
            return kategoria;
        }

        public void setKategoria(String kategoria) {
            this.kategoria = kategoria;
        }




        //ToDo fix toString
        @Override
        public String toString() {
            return "Tantargyak{" +
                    "Nev='" + Nev + '\'' +
                    ", TantargyID=" + TantargyID +
                    ", Leiras='" + Leiras + '\'' +
                    ", TanarID='" + TanarID + '\'' +
                    '}';
        }
    }


}
