package com.db.easylearning.interfaces;

import android.widget.RadioButton;

import com.db.easylearning.models.ReportUserModel;

import java.util.List;

public interface ReportUserClickListener {
    void OnItemClick(int position, List<ReportUserModel> reportUserModelList, RadioButton rbBtnReport);
}
