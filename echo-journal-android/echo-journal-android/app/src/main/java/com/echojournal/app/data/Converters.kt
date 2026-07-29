package com.echojournal.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromEntryType(type: EntryType): String = type.name

    @TypeConverter
    fun toEntryType(value: String): EntryType = EntryType.valueOf(value)
}
