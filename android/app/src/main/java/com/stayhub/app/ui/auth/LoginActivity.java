package com.stayhub.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.databinding.ActivityLoginBinding;
import com.stayhub.app.util.ValidationUtils;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Màn hình Xác thực tập trung duy nhất (Unified Auth Screen)
 * Nhập số điện thoại hoặc email để tiếp tục, tự động định tuyến đăng nhập hoặc đăng ký qua OTP
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnContinue.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String identifier = binding.etIdentifier.getText() != null
                ? binding.etIdentifier.getText().toString().trim()
                : "";

        binding.tilIdentifier.setError(null);

        if (TextUtils.isEmpty(identifier)) {
            binding.tilIdentifier.setError(getString(R.string.error_invalid_identifier));
            binding.etIdentifier.requestFocus();
            return;
        }

        ValidationUtils.IdentifierType type = ValidationUtils.checkIdentifierType(identifier);
        if (type == ValidationUtils.IdentifierType.INVALID) {
            binding.tilIdentifier.setError(getString(R.string.error_invalid_identifier));
            binding.etIdentifier.requestFocus();
            return;
        }

        // Gọi API gửi mã OTP
        authViewModel.sendOtp(identifier);
    }

    private void observeViewModel() {
        authViewModel.getSendOtpState().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnContinue.setEnabled(false);
            } else if (state.isSuccess() && state.getData() != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnContinue.setEnabled(true);

                // Chuyển sang màn hình xác thực OTP
                Intent intent = new Intent(this, OtpVerificationActivity.class);
                intent.putExtra(OtpVerificationActivity.EXTRA_IDENTIFIER, state.getData().getIdentifier());
                intent.putExtra(OtpVerificationActivity.EXTRA_TYPE, state.getData().getType());
                intent.putExtra(OtpVerificationActivity.EXTRA_IS_REGISTERED, state.getData().isRegistered());
                intent.putExtra(OtpVerificationActivity.EXTRA_OTP_MOCK, state.getData().getOtpMock());
                startActivity(intent);
            } else if (state.isError()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnContinue.setEnabled(true);

                String errorMsg = state.getMessage() != null
                        ? state.getMessage()
                        : getString(R.string.error_network);
                Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
