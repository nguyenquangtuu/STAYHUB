package com.stayhub.app.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.stayhub.app.R;
import com.stayhub.app.data.model.Homestay;
import com.stayhub.app.data.model.request.HomestayRequest;
import com.stayhub.app.data.repository.ApiCallback;
import com.stayhub.app.data.repository.HomestayRepository;
import com.stayhub.app.databinding.ActivityAdminEditHomestayBinding;
import com.stayhub.app.databinding.ItemPresetImageBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Chỉnh sửa / Thêm mới Homestay dành cho Admin
 * Hỗ trợ chọn Tỉnh/Thành phố & Quận/Huyện chuẩn xác, đổi ảnh bìa, xem trước trực quan và chọn tiện ích.
 */
public class AdminEditHomestayActivity extends AppCompatActivity {

    public static final String EXTRA_HOMESTAY = "extra_homestay";

    private ActivityAdminEditHomestayBinding binding;
    private final HomestayRepository homestayRepository = new HomestayRepository();

    private Homestay currentHomestay;
    private boolean isEditMode = false;

    // Dữ liệu Tỉnh/Thành phố và Quận/Huyện du lịch
    private final List<String> cityList = Arrays.asList(
            "Đà Lạt", "Đà Nẵng", "Hà Nội", "TP. Hồ Chí Minh",
            "Nha Trang", "Vũng Tàu", "Phú Quốc", "Hội An",
            "Sa Pa", "Quy Nhơn", "Phan Thiết", "Huế", "Ninh Bình", "Hạ Long"
    );

    private final Map<String, List<String>> districtMap = new HashMap<>();

    // Danh sách ảnh mẫu Unsplash chất lượng cao
    private static class PresetImage {
        String label;
        String url;

        PresetImage(String label, String url) {
            this.label = label;
            this.url = url;
        }
    }

    private final List<PresetImage> presetImages = Arrays.asList(
            new PresetImage("Đà Lạt Thông Reo", "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&auto=format&fit=crop&q=80"),
            new PresetImage("Đà Nẵng Biển", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80"),
            new PresetImage("Hội An Hoài Cổ", "https://images.unsplash.com/photo-1559592413-7cec4d0cae2b?w=800&auto=format&fit=crop&q=80"),
            new PresetImage("Resort Phú Quốc", "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800&auto=format&fit=crop&q=80"),
            new PresetImage("Biệt Thự Vườn", "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&auto=format&fit=crop&q=80"),
            new PresetImage("Villa Hồ Bơi", "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&auto=format&fit=crop&q=80")
    );

    private String selectedImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminEditHomestayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentHomestay = (Homestay) getIntent().getSerializableExtra(EXTRA_HOMESTAY);
        isEditMode = currentHomestay != null;

        initDistrictData();
        setupToolbar();
        setupPresetImages();
        setupLocationPickers();
        setupImageInputWatcher();
        setupAmenities();
        setupSaveButton();

        if (isEditMode) {
            populateExistingData();
        } else {
            // Mặc định chọn ảnh mẫu đầu tiên
            selectPresetImage(presetImages.get(0).url);
        }
    }

    private void initDistrictData() {
        districtMap.put("Đà Lạt", Arrays.asList("Phường 1 (Trung tâm)", "Phường 2", "Phường 3", "Phường 10 (Ga Đà Lạt)", "Khu Hồ Tuyền Lâm", "Trại Mát", "Cầu Đất"));
        districtMap.put("Đà Nẵng", Arrays.asList("Quận Sơn Trà (Sát Biển Mỹ Khê)", "Quận Ngũ Hành Sơn", "Quận Hải Châu (Trung tâm)", "Quận Thanh Khê", "Bà Nà Hills"));
        districtMap.put("Hà Nội", Arrays.asList("Quận Hoàn Kiếm (Phố Cổ)", "Quận Ba Đình", "Quận Tây Hồ (View Hồ Tây)", "Quận Cầu Giấy", "Ba Vì - Sơn Tây"));
        districtMap.put("TP. Hồ Chí Minh", Arrays.asList("Quận 1 (Trung tâm Bến Thành)", "Quận 3", "Quận Bình Thạnh", "Thành phố Thủ Đức (Thảo Điền)", "Quận 7 (Phú Mỹ Hưng)"));
        districtMap.put("Nha Trang", Arrays.asList("Đường Trần Phú (Mặt biển)", "Phường Lộc Thọ", "Bãi Dài Cam Ranh", "Đảo Hòn Tre", "Phường Vĩnh Hải"));
        districtMap.put("Vũng Tàu", Arrays.asList("Bãi Sau (Đường Thùy Vân)", "Bãi Trước", "Bãi Dâu", "Núi Nhỏ (Ngọn Hải Đăng)", "Hồ Tràm - Bình Châu"));
        districtMap.put("Phú Quốc", Arrays.asList("Phường Dương Đông (Chợ Đêm)", "Phường An Thới (Nam Đảo)", "Bãi Trường", "Bãi Khem", "Gành Dầu (Bắc Đảo)"));
        districtMap.put("Hội An", Arrays.asList("Phố cổ Hội An (Minh An)", "Cẩm Châu", "Biển An Bàng (Cẩm An)", "Cửa Đại", "Làng rau Trà Quế"));
        districtMap.put("Sa Pa", Arrays.asList("Thị xã Sa Pa (Nhà thờ Đá)", "Bản Cát Cát", "Bản Tả Van", "Bản Lao Chải", "Đèo Ô Quy Hồ"));
        districtMap.put("Quy Nhơn", Arrays.asList("Phường Ghềnh Ráng", "Phường Hải Cảng", "Eo Gió - Kỳ Co", "Bán đảo Phương Mai"));
        districtMap.put("Phan Thiết", Arrays.asList("Mũi Né (Đồi Cát Bay)", "Hàm Tiến", "Phú Hài", "Tiến Thành"));
        districtMap.put("Huế", Arrays.asList("TP. Huế (Bờ Nam Sông Hương)", "Vĩ Dạ", "Thuận An", "Vịnh Lăng Cô"));
        districtMap.put("Ninh Bình", Arrays.asList("Khu Quần thể Tràng An", "Tam Cốc - Bích Động", "Cố Đô Hoa Lư", "Đầm Vân Long"));
        districtMap.put("Hạ Long", Arrays.asList("Bãi Cháy (Trung tâm)", "Hòn Gai", "Bán đảo Tuần Châu", "Đảo Cát Bà"));
    }

    private void setupToolbar() {
        binding.toolbarAdminEditHomestay.setTitle(isEditMode ? "Chỉnh sửa Chi nhánh" : "Thêm Chi nhánh Mới");
        binding.toolbarAdminEditHomestay.setNavigationOnClickListener(v -> finish());
    }

    private void setupPresetImages() {
        LayoutInflater inflater = LayoutInflater.from(this);
        for (PresetImage preset : presetImages) {
            ItemPresetImageBinding itemBinding = ItemPresetImageBinding.inflate(inflater, binding.layoutPresetImages, false);
            itemBinding.tvPresetLabel.setText(preset.label);

            Glide.with(this)
                    .load(preset.url)
                    .placeholder(R.drawable.bg_image_placeholder)
                    .centerCrop()
                    .into(itemBinding.ivPresetThumbnail);

            itemBinding.getRoot().setOnClickListener(v -> selectPresetImage(preset.url));
            binding.layoutPresetImages.addView(itemBinding.getRoot());
        }
    }

    private void selectPresetImage(String url) {
        selectedImageUrl = url;
        binding.etHomestayImageUrl.setText(url);
        loadPreviewImage(url);
    }

    private void loadPreviewImage(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.bg_image_placeholder)
                    .error(R.drawable.bg_image_placeholder)
                    .centerCrop()
                    .into(binding.ivHomestayPreview);
        }
    }

    private void setupImageInputWatcher() {
        binding.etHomestayImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String inputUrl = s != null ? s.toString().trim() : "";
                if (!inputUrl.isEmpty() && !inputUrl.equals(selectedImageUrl)) {
                    selectedImageUrl = inputUrl;
                    loadPreviewImage(selectedImageUrl);
                }
            }
        });
    }

    private void setupLocationPickers() {
        // Spinner Thành phố
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cityList);
        binding.spinnerCity.setAdapter(cityAdapter);

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cityList.get(position);
                updateDistrictSpinner(selectedCity);
                updateAddressPreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateAddressPreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.etStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateAddressPreview();
            }
        });
    }

    private void updateDistrictSpinner(String city) {
        List<String> districts = districtMap.get(city);
        if (districts == null) {
            districts = Arrays.asList("Khu vực Trung tâm", "Ngoại ô");
        }
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, districts);
        binding.spinnerDistrict.setAdapter(districtAdapter);
    }

    private void updateAddressPreview() {
        String street = binding.etStreetAddress.getText() != null ? binding.etStreetAddress.getText().toString().trim() : "";
        String district = (binding.spinnerDistrict.getSelectedItem() != null) ? binding.spinnerDistrict.getSelectedItem().toString() : "";
        String city = (binding.spinnerCity.getSelectedItem() != null) ? binding.spinnerCity.getSelectedItem().toString() : "";

        StringBuilder full = new StringBuilder();
        if (!street.isEmpty()) {
            full.append(street).append(", ");
        }
        if (!district.isEmpty()) {
            full.append(district).append(", ");
        }
        full.append("TP. ").append(city);

        binding.tvFullAddressPreview.setText("Địa chỉ: " + full.toString());
    }

    private void setupAmenities() {
        // Chips already in XML:
        // chipAmenityWifi, chipAmenityPool, chipAmenityAc, chipAmenityKitchen, chipAmenityParking,
        // chipAmenityBalcony, chipAmenityBbq, chipAmenityBeach, chipAmenityWasher, chipAmenityPet
    }

    private List<String> getSelectedAmenities() {
        List<String> list = new ArrayList<>();
        if (binding.chipAmenityWifi.isChecked()) list.add("wifi");
        if (binding.chipAmenityPool.isChecked()) list.add("pool");
        if (binding.chipAmenityAc.isChecked()) list.add("air_conditioning");
        if (binding.chipAmenityKitchen.isChecked()) list.add("kitchen");
        if (binding.chipAmenityParking.isChecked()) list.add("parking");
        if (binding.chipAmenityBalcony.isChecked()) list.add("balcony");
        if (binding.chipAmenityBbq.isChecked()) list.add("bbq");
        if (binding.chipAmenityBeach.isChecked()) list.add("beach_front");
        if (binding.chipAmenityWasher.isChecked()) list.add("washing_machine");
        if (binding.chipAmenityPet.isChecked()) list.add("pet_friendly");

        // Lưu ảnh bìa tùy chỉnh hoặc preset vào danh sách tiện ích với prefix img: để lưu trữ đồng bộ
        if (!TextUtils.isEmpty(selectedImageUrl)) {
            list.add("img:" + selectedImageUrl);
        }
        return list;
    }

    private void populateExistingData() {
        binding.etHomestayName.setText(currentHomestay.getName());
        binding.etHomestayDescription.setText(currentHomestay.getDescription());

        // Chọn Thành phố
        String currentCity = currentHomestay.getCity();
        int cityIndex = -1;
        for (int i = 0; i < cityList.size(); i++) {
            if (currentCity != null && currentCity.toLowerCase().contains(cityList.get(i).toLowerCase())) {
                cityIndex = i;
                break;
            }
        }
        if (cityIndex >= 0) {
            binding.spinnerCity.setSelection(cityIndex);
            updateDistrictSpinner(cityList.get(cityIndex));
        }

        // Điền địa chỉ
        if (currentHomestay.getAddress() != null) {
            binding.etStreetAddress.setText(currentHomestay.getAddress());
        }

        // Tải ảnh
        String img = currentHomestay.getImageUrl();
        if (!TextUtils.isEmpty(img)) {
            selectPresetImage(img);
        }

        // Tick chọn tiện ích
        List<String> amenities = currentHomestay.getAmenities();
        if (amenities != null) {
            for (String a : amenities) {
                if (a == null) continue;
                String lower = a.toLowerCase();
                if (lower.contains("wifi")) binding.chipAmenityWifi.setChecked(true);
                if (lower.contains("pool")) binding.chipAmenityPool.setChecked(true);
                if (lower.contains("air") || lower.contains("ac")) binding.chipAmenityAc.setChecked(true);
                if (lower.contains("kitchen") || lower.contains("bếp")) binding.chipAmenityKitchen.setChecked(true);
                if (lower.contains("parking") || lower.contains("xe")) binding.chipAmenityParking.setChecked(true);
                if (lower.contains("balcony") || lower.contains("ban công")) binding.chipAmenityBalcony.setChecked(true);
                if (lower.contains("bbq")) binding.chipAmenityBbq.setChecked(true);
                if (lower.contains("beach") || lower.contains("biển")) binding.chipAmenityBeach.setChecked(true);
                if (lower.contains("wash") || lower.contains("giặt")) binding.chipAmenityWasher.setChecked(true);
                if (lower.contains("pet") || lower.contains("thú")) binding.chipAmenityPet.setChecked(true);
            }
        }
    }

    private void setupSaveButton() {
        binding.btnSaveHomestay.setOnClickListener(v -> saveHomestay());
    }

    private void saveHomestay() {
        String name = binding.etHomestayName.getText() != null ? binding.etHomestayName.getText().toString().trim() : "";
        String desc = binding.etHomestayDescription.getText() != null ? binding.etHomestayDescription.getText().toString().trim() : "";
        String street = binding.etStreetAddress.getText() != null ? binding.etStreetAddress.getText().toString().trim() : "";
        String district = (binding.spinnerDistrict.getSelectedItem() != null) ? binding.spinnerDistrict.getSelectedItem().toString() : "";
        String city = (binding.spinnerCity.getSelectedItem() != null) ? binding.spinnerCity.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(name)) {
            binding.tilHomestayName.setError("Vui lòng nhập tên chi nhánh (vd: StayHub - Chi nhánh Đà Lạt)");
            binding.etHomestayName.requestFocus();
            return;
        } else {
            binding.tilHomestayName.setError(null);
        }

        if (TextUtils.isEmpty(street)) {
            binding.tilStreetAddress.setError("Vui lòng nhập số nhà, tên đường");
            binding.etStreetAddress.requestFocus();
            return;
        } else {
            binding.tilStreetAddress.setError(null);
        }

        // Tạo chuỗi địa chỉ chi tiết kết hợp số nhà + quận
        String finalAddress = street;
        if (!district.isEmpty() && !finalAddress.toLowerCase().contains(district.toLowerCase())) {
            finalAddress += ", " + district;
        }

        List<String> amenities = getSelectedAmenities();
        HomestayRequest request = new HomestayRequest(name, city, finalAddress, desc, amenities);

        binding.layoutLoadingOverlay.setVisibility(View.VISIBLE);

        if (isEditMode) {
            homestayRepository.updateHomestay(currentHomestay.getId(), request, new ApiCallback<Homestay>() {
                @Override
                public void onSuccess(Homestay data, String message) {
                    binding.layoutLoadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(AdminEditHomestayActivity.this, "Cập nhật chi nhánh thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.layoutLoadingOverlay.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), errorMessage != null ? errorMessage : "Cập nhật thất bại", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            homestayRepository.createHomestay(request, new ApiCallback<Homestay>() {
                @Override
                public void onSuccess(Homestay data, String message) {
                    binding.layoutLoadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(AdminEditHomestayActivity.this, "Tạo chi nhánh mới thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    binding.layoutLoadingOverlay.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), errorMessage != null ? errorMessage : "Tạo chi nhánh thất bại", Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}

