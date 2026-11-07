package com.stayhub.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.databinding.ActivityRegisterBinding;
import com.stayhub.app.ui.home.MainActivity;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Màn hình Đăng ký tài khoản
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = StayHubApplication.getInstance().getSessionManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> performRegister());

        binding.tvGoToLogin.setOnClickListener(v -> finish());
    }

    /**
     * Xác thực form đăng ký và gọi API
     */
    private void performRegister() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        // Reset lỗi hiển thị
        binding.tilFullName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        boolean isValid = true;

        if (fullName.isEmpty()) {
            binding.tilFullName.setError(getString(R.string.error_empty_name));
            isValid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_short_password));
            isValid = false;
        }

        if (!isValid) return;

        authViewModel.register(fullName, email, password, phone);
    }

    private void observeViewModel() {
        authViewModel.getRegisterState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;

                case SUCCESS:
                    setLoading(false);
                    if (state.getData() != null) {
                        // Lưu thông tin phiên đăng nhập
                        sessionManager.saveSession(state.getData().getToken(), state.getData().getUser());

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    break;

                case ERROR:
                    setLoading(false);
                    String message = state.getMessage() != null ? state.getMessage() : getString(R.string.error_network);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void setLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!isLoading);
        binding.btnRegister.setText(isLoading ? "" : getString(R.string.register_button));
    }
}

