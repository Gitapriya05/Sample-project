package com.kickstarter.libs.graphql

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter : CustomTypeAdapter<Date> {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun encode(value: Date): CustomTypeValue<*> {
        return CustomTypeValue.GraphQLString(DATE_FORMAT.format(value))
    }

    override fun decode(value: CustomTypeValue<*>): Date {
        try {
            return DATE_FORMAT.parse(value.value.toString())
        } catch (exception: ParseException) {
            throw RuntimeException(exception)
        }
    }
}
