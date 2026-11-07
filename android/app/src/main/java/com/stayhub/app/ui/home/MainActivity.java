package com.stayhub.app.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stayhub.app.R;
import com.stayhub.app.databinding.ActivityMainBinding;
import com.stayhub.app.ui.booking.MyBookingsFragment;
import com.stayhub.app.ui.profile.ProfileFragment;

/**
 * MainActivity: Điều hướng 3 tab chính (Home | Bookings | Profile)
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final HomeFragment homeFragment = new HomeFragment();
    private final MyBookingsFragment bookingsFragment = new MyBookingsFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mặc định nạp HomeFragment
        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(homeFragment);
                return true;
            } else if (itemId == R.id.navigation_bookings) {
                loadFragment(bookingsFragment);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                loadFragment(profileFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navHostFragment, fragment);
        transaction.commit();
    }
}
