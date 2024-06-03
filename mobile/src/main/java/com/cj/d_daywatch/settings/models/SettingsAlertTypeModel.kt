package com.cj.d_daywatch.settings.models

import android.content.Context
import com.cj.d_daywatch.R

interface SettingsAlertContentsModel {
    fun getTitle(context: Context, type: SettingsAlertTypeModel): String
    fun getMessage(context: Context, type: SettingsAlertTypeModel): String
}

enum class SettingsAlertTypeModel: SettingsAlertContentsModel {
    CONFIRM_SIGN_OUT {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_SIGN_OUT)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_CONFIRM_SIGN_OUT)
        }
    }, SIGN_OUT_SUCCESS {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_SIGN_OUT)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_SIGN_OUT_SUCCESS)
        }
    }, SIGN_OUT_FAIL {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_ERROR)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_ERROR_UPLOAD)
        }
    }, CONFIRM_CANCEL_MEMBERSHIP {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_CANCEL_MEMBERSHIP)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_CONFIRM_CANCEL_MEMBRSHIP)
        }
    }, CANCEL_MEMBERSHIP_SUCCESS {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_CANCEL_MEMBERSHIP)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_CANCEL_MEMBERSHIP_SUCCESS)
        }
    }, CANCEL_MEMBERSHIP_FAIL {
        override fun getTitle(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_ERROR)
        }

        override fun getMessage(context: Context, type: SettingsAlertTypeModel): String {
            return context.getString(R.string.TXT_ERROR_UPLOAD)
        }
    }
}
