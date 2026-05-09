package com.example.examlast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDetail extends AppCompatActivity {

    private TextView tvEmployeeId;
    private ImageView imgProfile;
    private EditText edtName, edtBirth, edtAddress, edtEmail, edtSalary;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spinnerPosition;
    private Button btnSave;

    private String employeeId;
    private String currentImageBase64 = "";
    private boolean isEditMode = true;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        int newWidth = 400;
                        int newHeight = (bitmap.getHeight() * newWidth) / bitmap.getWidth();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos); // Nén 60% chất lượng
                        byte[] imageBytes = baos.toByteArray();

                        currentImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        imgProfile.setImageBitmap(resizedBitmap);

                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        tvEmployeeId = findViewById(R.id.tvEmployeeId);
        imgProfile = findViewById(R.id.imgProfile);
        edtName = findViewById(R.id.edtName);
        edtBirth = findViewById(R.id.edtBirth);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtSalary = findViewById(R.id.edtSalary);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        spinnerPosition = findViewById(R.id.spinnerPositionDetail);
        btnSave = findViewById(R.id.btnSave);

        String[] positions = {"Giám đốc", "Quản lý", "Nhân viên"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, positions);
        spinnerPosition.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            employeeId = intent.getStringExtra("id");
            tvEmployeeId.setText("Mã nhân viên: " + employeeId);
            edtName.setText(intent.getStringExtra("fullName"));
            edtBirth.setText(intent.getStringExtra("birthDate"));
            edtAddress.setText(intent.getStringExtra("address"));
            edtEmail.setText(intent.getStringExtra("email"));

            double rawSalary = intent.getDoubleExtra("salary", 0);
            java.text.DecimalFormat df = new java.text.DecimalFormat("#");

            edtSalary.setText(df.format(rawSalary));
            if ("Nam".equals(intent.getStringExtra("gender"))) rbMale.setChecked(true);
            else rbFemale.setChecked(true);

            String pos = intent.getStringExtra("position");
            if(pos != null) spinnerPosition.setSelection(adapter.getPosition(pos));

            currentImageBase64 = intent.getStringExtra("imageBase64");
            if (currentImageBase64 != null && !currentImageBase64.isEmpty()) {
                byte[] decodedString = Base64.decode(currentImageBase64, Base64.DEFAULT);
                imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            }

            toggleEditMode(false);
        } else {
            employeeId = "EMP-" + System.currentTimeMillis();
            tvEmployeeId.setText("Mã nhân viên: " + employeeId);
            toggleEditMode(true);
        }

        imgProfile.setOnClickListener(v -> {
            if (isEditMode) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(galleryIntent);
            }
        });

        btnSave.setOnClickListener(v -> {
            if (!isEditMode) toggleEditMode(true);
            else saveToFirestore();
        });
    }

    private void toggleEditMode(boolean enable) {
        isEditMode = enable;
        edtName.setEnabled(enable);
        edtBirth.setEnabled(enable);
        edtAddress.setEnabled(enable);
        edtEmail.setEnabled(enable);
        edtSalary.setEnabled(enable);
        spinnerPosition.setEnabled(enable);
        for(int i=0; i<rgGender.getChildCount(); i++) rgGender.getChildAt(i).setEnabled(enable);

        btnSave.setText(enable ? "Lưu dữ liệu" : "Chỉnh sửa");
    }

    private void saveToFirestore() {
        String name = edtName.getText().toString();
        String birth = edtBirth.getText().toString();
        String address = edtAddress.getText().toString();
        String email = edtEmail.getText().toString();
        String salaryStr = edtSalary.getText().toString();

        if (!validateInput(name, birth, address, email, salaryStr)) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("imageBase64", currentImageBase64);
        data.put("fullName", name);
        data.put("birthDate", birth);
        data.put("address", address);
        data.put("email", email);
        data.put("salary", Double.parseDouble(salaryStr));
        data.put("gender", rbMale.isChecked() ? "Nam" : "Nữ");

        btnSave.setEnabled(false);
        FirebaseFirestore.getInstance().collection("Employees").document(employeeId)
                .set(data)
                .addOnSuccessListener(aVoid -> finish())
                .addOnFailureListener(e -> btnSave.setEnabled(true));
    }
    private boolean validateInput(String name, String birth, String address, String email, String salaryStr) {

        if (currentImageBase64 == null || currentImageBase64.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ảnh đại diện!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (name.trim().isEmpty()) {
            edtName.setError("Tên không được để trống");
            edtName.requestFocus();
            return false;
        }

        String dateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$";
        if (birth.trim().isEmpty() || !birth.matches(dateRegex)) {
            edtBirth.setError("Sai định dạng ngày sinh (dd/MM/yyyy)");
            edtBirth.requestFocus();
            return false;
        }

        if (address.trim().isEmpty()) {
            edtAddress.setError("Địa chỉ không được để trống");
            edtAddress.requestFocus();
            return false;
        }

        if (email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return false;
        }

        if (salaryStr.trim().isEmpty()) {
            edtSalary.setError("Mức lương không được để trống");
            edtSalary.requestFocus();
            return false;
        } else {
            try {
                double salary = Double.parseDouble(salaryStr);
                if (salary <= 0) {
                    edtSalary.setError("Mức lương phải lớn hơn 0");
                    edtSalary.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                edtSalary.setError("Mức lương sai định dạng số");
                edtSalary.requestFocus();
                return false;
            }
        }

        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}