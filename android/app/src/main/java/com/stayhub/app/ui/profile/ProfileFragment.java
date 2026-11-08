package com.stayhub.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.stayhub.app.R;
import com.stayhub.app.StayHubApplication;
import com.stayhub.app.data.local.SessionManager;
import com.stayhub.app.data.model.User;
import com.stayhub.app.databinding.FragmentProfileBinding;
import com.stayhub.app.ui.auth.LoginActivity;
import com.stayhub.app.viewmodel.AuthViewModel;

/**
 * Fragment hiển thị thông tin cá nhân của người dùng và chức năng Đăng xuất
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = StayHubApplication.getInstance().getSessionManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        displayLocalUserInfo();
        setupListeners();
        observeViewModel();

        // Đồng bộ thông tin mới nhất từ máy chủ
        authViewModel.loadProfile();
    }

    private void displayLocalUserInfo() {
        User user = sessionManager.getUser();
        if (user != null) {
            updateUiWithUser(user);
        }
    }

    private void updateUiWithUser(User user) {
        binding.tvProfileName.setText(user.getFullName() != null && !user.getFullName().isEmpty()
                ? user.getFullName()
                : "Người dùng StayHub");

        binding.tvProfileEmail.setText(user.getEmail() != null ? user.getEmail() : "Chưa có email");
        binding.tvProfilePhone.setText(user.getPhone() != null && !user.getPhone().isEmpty()
                ? user.getPhone()
                : "Chưa cập nhật số điện thoại");

        boolean isAdmin = user.isAdmin();
        binding.tvProfileRole.setText(isAdmin ? getString(R.string.role_admin) : getString(R.string.role_customer));
    }

    private void setupListeners() {
        binding.btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    public void onResume() {
        super.onResume();
        displayLocalUserInfo();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_logout_title)
                .setMessage(R.string.confirm_logout_message)
                .setPositiveButton(R.string.action_yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.action_no, null)
                .show();
    }

    private void performLogout() {
        // Xóa thông tin phiên làm việc trong SharedPreferences
        sessionManager.clearSession();

        // Điều hướng về LoginActivity và xóa toàn bộ Activity stack
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void observeViewModel() {
        authViewModel.getProfileState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.isSuccess() && state.getData() != null) {
                User freshUser = state.getData();
                sessionManager.saveUser(freshUser);
                updateUiWithUser(freshUser);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

