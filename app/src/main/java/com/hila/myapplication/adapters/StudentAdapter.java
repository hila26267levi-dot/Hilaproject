package com.hila.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {


    public interface OnStudentClickListener {
        void onStudentClick(Student student);
        void onLongStudentClick(Student student);
    }

    private final List<Student> studentList;
    private final OnStudentClickListener onStudentClickListener;
    public StudentAdapter(@Nullable final OnStudentClickListener onStudentClickListener) {
        studentList = new ArrayList<>();
        this.onStudentClickListener = onStudentClickListener;
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        if (student == null) return;

        holder.tvFname.setText(student.getFname());
        holder.tvLname.setText(student.getLname());
        holder.tvClass.setText(student.getKita());

        holder.itemView.setOnClickListener(v -> {
            if (onStudentClickListener != null) {
                onStudentClickListener.onStudentClick(student);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onStudentClickListener != null) {
                onStudentClickListener.onLongStudentClick(student);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setStudentList(List<Student> students) {
        studentList.clear();
        studentList.addAll(students);
        notifyDataSetChanged();
    }

    public void addStudent(Student student) {
        studentList.add(student);
        notifyItemInserted(studentList.size() - 1);
    }
    public void updateStudent(Student student) {
        int index = studentList.indexOf(student);
        if (index == -1) return;
        studentList.set(index, student);
        notifyItemChanged(index);
    }

    public void removeStudent(Student student) {
        int index = studentList.indexOf(student);
        if (index == -1) return;
        studentList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFname, tvLname, tvClass;
        Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFname = itemView.findViewById(R.id.tvStusendFName);
            tvLname = itemView.findViewById(R.id.tvStusendLName);
            tvClass = itemView.findViewById(R.id.tvStusendclass);
        }
    }
}