package com.db.easylearning.interfaces;

import com.db.easylearning.models.DailyConversationModel;

import java.util.List;

public interface ItemClickListener {
    void onItemClick(int position, List<DailyConversationModel> dailyConversationModelList);
}
