package com.stenleone.clenner.managers.config

import com.stenleone.clenner.worker.CreatePushNotificationWorker.Companion.DEF_PUSH_VALUE

enum class Config(val key: String, val defaultValue: Any) {
  SHOW_NOTIFICATION_TIME_IN_HOUR("show_notification_time_in_hour", "10"),
  CLEAN_PUSH_TITLE("push_clean_title_text", DEF_PUSH_VALUE),
  CLEAN_PUSH_SUB_TITLE("push_clean_sub_title_text", DEF_PUSH_VALUE),
  CLEAN_PUSH_TITLE_2("push_clean_title_text_2", DEF_PUSH_VALUE),
  CLEAN_PUSH_SUB_TITLE_2("push_clean_sub_title_text_2", DEF_PUSH_VALUE),
  CLEAN_PUSH_TITLE_3("push_clean_title_text_3", DEF_PUSH_VALUE),
  CLEAN_PUSH_SUB_TITLE_3("push_clean_sub_title_text_3", DEF_PUSH_VALUE),

  POST_USER_DATA_URL("post_user_data_url", "google.com");
}
