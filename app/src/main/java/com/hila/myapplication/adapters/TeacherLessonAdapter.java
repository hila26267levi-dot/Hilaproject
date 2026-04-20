package com.hila.myapplication.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;

import java.util.List;

public class TeacherLessonAdapter extends RecyclerView.Adapter<TeacherLessonAdapter.ViewHolder> {



    public interface OnLessonClickListener {
        void onLessonClick(TeacherLesson lesson );
        void onLongLessonClick(TeacherLesson lesson);
    }

    private List<TeacherLesson> lessonList;
    private  TeacherLessonAdapter.OnLessonClickListener onLessonClickListener;



    public TeacherLessonAdapter(List<TeacherLesson> lessonList, OnLessonClickListener onLessonClickListener) {
        this.lessonList = lessonList;
        this.onLessonClickListener = onLessonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeacherLesson lesson = lessonList.get(position);
        holder.subjectText.setText(lesson.getSubject());
        if (lesson.getTeacher() != null) {
            holder.teacherText.setText(lesson.getTeacher().getFname());
        } else {
            holder.teacherText.setText("");
        }
        holder.dateText.setText(lesson.getDate());
        holder.timeText.setText(lesson.getTime());
        holder.statusText.setText(lesson.getStatus());
        holder.zoomHomeText.setText(lesson.getZoomORhome());
        holder.kitaText.setText(lesson.getKita());
        holder.priceText.setText(String.valueOf(lesson.getPrice()));
        holder.itemView.setOnClickListener(v -> {
            if (onLessonClickListener != null) {
                onLessonClickListener.onLessonClick(lesson);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onLessonClickListener != null) {
                onLessonClickListener.onLongLessonClick(lesson);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectText, teacherText, dateText, timeText, statusText, zoomHomeText, kitaText, priceText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            subjectText = itemView.findViewById(R.id.subjectText);
            teacherText = itemView.findViewById(R.id.teacherText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            statusText = itemView.findViewById(R.id.statusText);
            zoomHomeText = itemView.findViewById(R.id.zoomHomeText);
            kitaText = itemView.findViewById(R.id.kitaText);
            priceText = itemView.findViewById(R.id.priceText);
        }
    }
}