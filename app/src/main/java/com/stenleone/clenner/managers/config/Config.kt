package com.stenleone.clenner.managers.config

enum class Config(val key: String, val defaultValue: Any) {
  SHOW_NOTIFICATION_TIME_IN_HOUR("show_notification_time_in_hour", "10"),
  CLEAN_PUSH_TITLE("push_clean_title_text", "def"),
  CLEAN_PUSH_SUB_TITLE("push_clean_sub_title_text", "def"),
  POST_USER_DATA_URL("post_user_data_url", "google.com");
}
