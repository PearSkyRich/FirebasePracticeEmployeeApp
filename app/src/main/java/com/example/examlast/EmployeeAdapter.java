package com.example.examlast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee emp = employeeList.get(position);

        holder.tvName.setText(emp.getFullName());
        holder.tvPosition.setText(emp.getPosition());
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedSalary = formatter.format(emp.getSalary());

        holder.tvSalary.setText("Lương: " + formattedSalary + " VNĐ");

        if (emp.getImageBase64() != null && !emp.getImageBase64().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(emp.getImageBase64(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imgProfile.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.imgProfile.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        } else {
            holder.imgProfile.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            String[] options = {"Xem chi tiết", "Xóa nhân viên"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Tùy chọn: " + emp.getFullName());
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    Intent intent = new Intent(context, EmployeeDetail.class);
                    intent.putExtra("id", emp.getId());
                    intent.putExtra("imageBase64", emp.getImageBase64());
                    intent.putExtra("fullName", emp.getFullName());
                    intent.putExtra("birthDate", emp.getBirthDate());
                    intent.putExtra("address", emp.getAddress());
                    intent.putExtra("gender", emp.getGender());
                    intent.putExtra("email", emp.getEmail());
                    intent.putExtra("salary", emp.getSalary());
                    intent.putExtra("position", emp.getPosition());
                    context.startActivity(intent);
                } else if (which == 1) {
                    FirebaseFirestore.getInstance().collection("Employees").document(emp.getId())
                            .delete().addOnSuccessListener(aVoid -> Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show());
                }
            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() { return employeeList.size(); }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvName, tvPosition, tvSalary;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgItemProfile);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPosition = itemView.findViewById(R.id.tvItemPosition);
            tvSalary = itemView.findViewById(R.id.tvItemSalary);
        }
    }
}