package com.stayhub.app.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.data.model.User;
import com.stayhub.app.databinding.ActivityEditProfileBinding;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Activity cho phép người dùng chỉnh sửa thông tin cá nhân (Họ tên, SĐT)
 */
public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = StayHubApplication.getInstance().getSessionManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupToolbar();
        populateUserData();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateUserData() {
        currentUser = sessionManager.getUser();
        if (currentUser != null) {
            if (currentUser.getFullName() != null) {
                binding.etFullName.setText(currentUser.getFullName());
            }
            if (currentUser.getPhone() != null) {
                binding.etPhone.setText(currentUser.getPhone());
            }
            if (currentUser.getEmail() != null) {
                binding.etEmail.setText(currentUser.getEmail());
            }
        }
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> validateAndUpdate());
    }

    private void validateAndUpdate() {
        String fullName = binding.etFullName.getText() != null
                ? binding.etFullName.getText().toString().trim()
                : "";
        String phone = binding.etPhone.getText() != null
                ? binding.etPhone.getText().toString().trim()
                : "";

        binding.tilFullName.setError(null);

        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.setError(getString(R.string.error_empty_name));
            binding.etFullName.requestFocus();
            return;
        }

        authViewModel.updateProfile(fullName, phone);
    }

    private void observeViewModel() {
        authViewModel.getUpdateProfileState().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSave.setEnabled(false);
            } else if (state.isSuccess() && state.getData() != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);

                // Lưu user mới vào SessionManager
                sessionManager.saveUser(state.getData());
                Toast.makeText(this, R.string.update_profile_success, Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish();
            } else if (state.isError()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setEnabled(true);

                String errorMsg = state.getMessage() != null
                        ? state.getMessage()
                        : getString(R.string.update_profile_error);
                Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
