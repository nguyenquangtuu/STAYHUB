package com.stayhub.app.ui.auth;

import android.content.Intent;
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
import com.stayhub.app.databinding.ActivityRegisterBinding;
import com.stayhub.app.ui.home.MainActivity;
import com.stayhub.app.util.ValidationUtils;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Màn hình Hoàn tất đăng ký tài khoản (sau khi xác thực OTP thành công)
 */
public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_IDENTIFIER = "extra_identifier";
    public static final String EXTRA_TYPE = "extra_type";

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    private String identifier;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = StayHubApplication.getInstance().getSessionManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        parseIntentData();
        setupToolbar();
        setupForm();
        setupListeners();
        observeViewModel();
    }

    private void parseIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            identifier = intent.getStringExtra(EXTRA_IDENTIFIER);
            type = intent.getStringExtra(EXTRA_TYPE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupForm() {
        if ("phone".equalsIgnoreCase(type)) {
            // Xác thực bằng SĐT -> Khóa ô SĐT, yêu cầu nhập Email + Họ tên
            binding.etPhone.setText(identifier);
            binding.tilPhone.setEnabled(false);
            binding.tilPhone.setHelperText("✓ Số điện thoại đã xác thực");
            binding.etFullName.requestFocus();
        } else {
            // Xác thực bằng Email -> Khóa ô Email, yêu cầu nhập SĐT + Họ tên
            binding.etEmail.setText(identifier);
            binding.tilEmail.setEnabled(false);
            binding.tilEmail.setHelperText("✓ Email đã xác thực");
            binding.etFullName.requestFocus();
        }
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String fullName = binding.etFullName.getText() != null
                ? binding.etFullName.getText().toString().trim()
                : "";
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim()
                : "";
        String phone = binding.etPhone.getText() != null
                ? binding.etPhone.getText().toString().trim()
                : "";

        binding.tilFullName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPhone.setError(null);

        // 1. Kiểm tra Họ và tên
        if (!ValidationUtils.isValidFullName(fullName)) {
            binding.tilFullName.setError(getString(R.string.error_empty_name));
            binding.etFullName.requestFocus();
            return;
        }

        // 2. Kiểm tra Email
        if (TextUtils.isEmpty(email) || !ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            binding.etEmail.requestFocus();
            return;
        }

        // 3. Kiểm tra Số điện thoại
        if (!TextUtils.isEmpty(phone) && !ValidationUtils.isValidVietnamesePhone(phone)) {
            binding.tilPhone.setError(getString(R.string.error_invalid_phone));
            binding.etPhone.requestFocus();
            return;
        }

        authViewModel.completeRegistration(fullName, email, phone);
    }

    private void observeViewModel() {
        authViewModel.getCompleteRegistrationState().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
            } else if (state.isSuccess() && state.getData() != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);

                // Lưu JWT token và thông tin người dùng
                sessionManager.saveSession(state.getData().getToken(), state.getData().getUser());

                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (state.isError()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);

                String errorMsg = state.getMessage() != null
                        ? state.getMessage()
                        : getString(R.string.error_network);
                Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
