package com.proto.type.base.data.database

import com.proto.type.base.utils.AppLog
import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * @Details A migration config of realm
 */

class RealmMigration: RealmMigration {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = RealmMigration::class.java.simpleName
    }

    // MARK: - Override Function
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        AppLog.d(TAG, "Database Migration needed. Old version: $oldVersion, new version: $newVersion")
        var oldVersion = oldVersion

        // DynamicRealm exposes an editable schema
        val schema = realm.schema

        // Example:
        // Migrate to version 1: Add a new class.
        // open class Person(
        //     var name: String = "",
        //     var age: Int = 0,
        // ): RealmObject()
        if (oldVersion == 0L) {
            AppLog.d(TAG, "Database schema: $schema")
            // TODO("Implement data migration here using schema")
            // Example:
            // schema.create("Person")
            //    .addField("name", String::class.java)
            //    .addField("age", Int::class.javaPrimitiveType)
            oldVersion++
        }
    }

}