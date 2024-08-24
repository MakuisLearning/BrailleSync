package com.example.braille_sync.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.braille_sync.Classes.History;
import com.example.braille_sync.Adapters.HistoryAdapter;
import com.example.braille_sync.Helper.HistoryHelperSwipe;
import com.example.braille_sync.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class HistoryFragment extends Fragment {
//    implements HistoryRecyclerviewInterface


    ArrayList<History> HistoryList;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    TextView emptyData;
    ProgressBar progressDialog;
    HistoryAdapter adapter;
    FirebaseAuth FAuth;
    String uid;
    Date sevenDaysAgo;
    Calendar calendar;


    public HistoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        db = FirebaseFirestore.getInstance();

        recyclerView = v.findViewById(R.id.rvHistory);
        emptyData = v.findViewById(R.id.empty);
        progressDialog = v.findViewById(R.id.loaders);
        FAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(FAuth).getUid();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HistoryList = new ArrayList<>();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        sevenDaysAgo = calendar.getTime();

        HistoryHelperSwipe helper = new HistoryHelperSwipe(getContext(), recyclerView, 200) {
            @Override
            public void instantiateButton(RecyclerView.ViewHolder viewHolder, List<myButton> buffer) {

                buffer.add(
                        new myButton(requireContext(),
                                "Delete",
                                R.drawable.ic_delete_black_24dp,
                                30,
                                Color.parseColor("#FF3C30"),
                                position -> requireActivity().runOnUiThread(() -> {
                                    String HistoryID = HistoryList.get(position).getDocumentID();
                                    deleteDocument(HistoryID);

                                })));

                buffer.add(
                        new myButton(requireContext(),
                                "Go to Home",
                                R.drawable.go,
                                30,
                                Color.parseColor("#00CA36"),
                                position -> requireActivity().runOnUiThread(() -> {
                                    History history = HistoryList.get(position);
                                    String text = history.getText();
                                    Bundle result = new Bundle();
                                    result.putString("text", text);
                                    getParentFragmentManager().setFragmentResult("Data", result);

                                    HomeFragment homeFragment = new HomeFragment();
                                    homeFragment.setArguments(result);

                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, homeFragment)
                                            .addToBackStack(null)
                                            .commit();
                                })));
            }
        };

        dataInitialize();

        adapter = new HistoryAdapter(getContext(), HistoryList);
//        this
        recyclerView.setAdapter(adapter);
        return v;
    }


    @SuppressLint("NotifyDataSetChanged")
    private void dataInitialize() {
        showLoaders();

        db.collection("user-history").whereEqualTo("uid", uid)
                .orderBy("time", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("time", sevenDaysAgo)
                .addSnapshotListener((value, error) -> {
                            if (error != null) {
                                hideLoaders();
                                Log.e("Error?:", Objects.requireNonNull(error.getMessage()));
                                return;
                            }

                            if (value != null && value.isEmpty()) {
                                emptyData.setVisibility(View.VISIBLE);
                                hideLoaders();
                                HistoryList.clear();
                                adapter.notifyDataSetChanged();
                                return;
                            }

                            emptyData.setVisibility(View.INVISIBLE);


                            List<DocumentChange> documentChanges = Objects.requireNonNull(value).getDocumentChanges();
                            for (DocumentChange dc : documentChanges) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        History history = dc.getDocument().toObject(History.class);
                                        history.setDocumentID(dc.getDocument().getId());
                                        HistoryList.add(history);
                                        break;
                                    case MODIFIED:
                                        int index = -1;
                                        for (int i = 0; i < HistoryList.size(); i++) {
                                            if (HistoryList.get(i).getDocumentID().equals(dc.getDocument().getId())) {
                                                index = i;
                                                break;
                                            }
                                        }
                                        if (index != -1) {
                                            History updatedHistory = dc.getDocument().toObject(History.class);
                                            updatedHistory.setDocumentID(dc.getDocument().getId());
                                            HistoryList.set(index, updatedHistory);
                                        }
                                        break;
                                    case REMOVED:
                                        HistoryList.removeIf(historyToRemove -> historyToRemove.getDocumentID().equals(dc.getDocument().getId()));
                                        break;
                                }
                            }

                            adapter.notifyDataSetChanged();

                            hideLoaders();
                        }
                );


    }

    private void deleteDocument(String documentID) {
        db.collection("user-history")
                .document(documentID)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Document deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete document", Toast.LENGTH_SHORT).show());
    }
    private void showLoaders(){
        if (progressDialog.getVisibility() == View.INVISIBLE) {
            progressDialog.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoaders(){
        if (progressDialog.getVisibility() == View.VISIBLE) {
            progressDialog.setVisibility(View.INVISIBLE);
        }
    }

}



