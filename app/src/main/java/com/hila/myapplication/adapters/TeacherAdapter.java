package com.hila.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {


    public interface OnTeacherClickListener {
        void onTeacherClick(Teacher teacher );
        void onLongTeacherClick(Teacher teacher);
    }

    private final List<Teacher> teacherList;
    private final TeacherAdapter.OnTeacherClickListener onTeacherClickListener;
    public TeacherAdapter(@Nullable final TeacherAdapter.OnTeacherClickListener onTeacherClickListener) {
        teacherList = new ArrayList<>();
        this.onTeacherClickListener = onTeacherClickListener;
    }

    @NonNull
    @Override
    public TeacherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.ViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        if (teacher == null) return;

        holder.tvFname.setText(teacher.getFname());
        holder.tvLname.setText(teacher.getLname());
        holder.tvprofession.setText(teacher.getSubject());
        holder.tvClassteach.setText(teacher.getTeachclass());
        holder.itemView.setOnClickListener(v -> {
            if (onTeacherClickListener != null) {
                onTeacherClickListener.onTeacherClick(teacher);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onTeacherClickListener != null) {
                onTeacherClickListener.onLongTeacherClick(teacher);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public void setTeacherList(List<Teacher> teachers) {
        teacherList.clear();
        teacherList.addAll(teachers);
        notifyDataSetChanged();
    }

    public void addTeacher(Teacher teacher) {
        teacherList.add(teacher);
        notifyItemInserted(teacherList.size() - 1);
    }
    public void updateTeacher(Teacher teacher) {
        int index = teacherList.indexOf(teacher);
        if (index == -1) return;
        teacherList.set(index, teacher);
        notifyItemChanged(index);
    }

    public void removeTeacher(Teacher teacher) {
        int index = teacherList.indexOf(teacher);
        if (index == -1) return;
      teacherList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFname, tvLname, tvClassteach, tvprofession;
        Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFname = itemView.findViewById(R.id.tvTeacherFName);
            tvLname = itemView.findViewById(R.id.tvTeacherLName);
            tvClassteach = itemView.findViewById(R.id.tvTeacherclassteach);
            tvprofession = itemView.findViewById(R.id.tvTeacherprofession);
        }
    }
}
