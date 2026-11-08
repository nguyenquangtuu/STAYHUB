package com.stayhub.app.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.databinding.ActivityOtpVerificationBinding;
import com.stayhub.app.ui.home.MainActivity;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Màn hình nhập và xác thực mã OTP với 6 ô gạch dưới hiện đại
 */
public class OtpVerificationActivity extends AppCompatActivity {

    public static final String EXTRA_IDENTIFIER = "extra_identifier";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_IS_REGISTERED = "extra_is_registered";
    public static final String EXTRA_OTP_MOCK = "extra_otp_mock";

    private ActivityOtpVerificationBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    private String identifier;
    private String type;
    private boolean isRegistered;
    private String otpMock;
    private TextView[] otpSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = StayHubApplication.getInstance().getSessionManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        parseIntentData();
        setupToolbar();
        setupViews();
        setupListeners();
        observeViewModel();
    }

    private void parseIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            identifier = intent.getStringExtra(EXTRA_IDENTIFIER);
            type = intent.getStringExtra(EXTRA_TYPE);
            isRegistered = intent.getBooleanExtra(EXTRA_IS_REGISTERED, false);
            otpMock = intent.getStringExtra(EXTRA_OTP_MOCK);
            if (TextUtils.isEmpty(otpMock)) {
                otpMock = "123456";
            }
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

    private void setupViews() {
        String displayIdentifier = identifier != null ? identifier : "";
        binding.tvOtpSubtitle.setText(getString(R.string.otp_subtitle, displayIdentifier));
        binding.tvMockOtpHint.setText(getString(R.string.otp_mock_hint, otpMock));

        otpSlots = new TextView[]{
                binding.tvOtp1, binding.tvOtp2, binding.tvOtp3,
                binding.tvOtp4, binding.tvOtp5, binding.tvOtp6
        };
        updateOtpSlots("");

        binding.etHiddenOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateOtpSlots(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() == 6) {
                    validateAndVerifyOtp();
                }
            }
        });

        binding.layoutOtpSlots.setOnClickListener(v -> focusHiddenOtp());
        binding.etHiddenOtp.post(this::focusHiddenOtp);
    }

    private void updateOtpSlots(String text) {
        binding.tvOtpError.setVisibility(View.GONE);
        int length = text.length();
        for (int i = 0; i < 6; i++) {
            if (i < length) {
                otpSlots[i].setText(String.valueOf(text.charAt(i)));
                otpSlots[i].setBackgroundResource(R.drawable.bg_otp_slot_filled);
            } else if (i == length) {
                otpSlots[i].setText("");
                otpSlots[i].setBackgroundResource(R.drawable.bg_otp_slot_focused);
            } else {
                otpSlots[i].setText("");
                otpSlots[i].setBackgroundResource(R.drawable.bg_otp_slot_inactive);
            }
        }
    }

    private void focusHiddenOtp() {
        binding.etHiddenOtp.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.etHiddenOtp, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void setupListeners() {
        // Nút điền nhanh mã OTP mẫu
        binding.btnQuickFillOtp.setOnClickListener(v -> {
            binding.etHiddenOtp.setText(otpMock);
            binding.etHiddenOtp.setSelection(binding.etHiddenOtp.getText().length());
        });

        // Nút Xác thực
        binding.btnVerify.setOnClickListener(v -> validateAndVerifyOtp());

        // Nút Gửi lại mã OTP
        binding.btnResendOtp.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(identifier)) {
                authViewModel.sendOtp(identifier);
                Toast.makeText(this, R.string.otp_resent_success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndVerifyOtp() {
        String otp = binding.etHiddenOtp.getText() != null
                ? binding.etHiddenOtp.getText().toString().trim()
                : "";

        binding.tvOtpError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(otp) || otp.length() != 6) {
            binding.tvOtpError.setText(R.string.error_invalid_otp);
            binding.tvOtpError.setVisibility(View.VISIBLE);
            return;
        }

        authViewModel.verifyOtp(identifier, otp);
    }

    private void observeViewModel() {
        authViewModel.getVerifyOtpState().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnVerify.setEnabled(false);
            } else if (state.isSuccess() && state.getData() != null) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);

                if (state.getData().isRegistered()) {
                    // ĐÃ ĐĂNG KÝ -> Lưu phiên và mở MainActivity
                    sessionManager.saveSession(state.getData().getToken(), state.getData().getUser());

                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // CHƯA ĐĂNG KÝ -> Chuyển sang hoàn tất hồ sơ
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.putExtra(RegisterActivity.EXTRA_IDENTIFIER, identifier);
                    intent.putExtra(RegisterActivity.EXTRA_TYPE, type);
                    startActivity(intent);
                    finish();
                }
            } else if (state.isError()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnVerify.setEnabled(true);

                String errorMsg = state.getMessage() != null
                        ? state.getMessage()
                        : getString(R.string.error_network);
                binding.tvOtpError.setText(errorMsg);
                binding.tvOtpError.setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
