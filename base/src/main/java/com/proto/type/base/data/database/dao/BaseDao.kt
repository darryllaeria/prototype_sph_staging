package com.proto.type.base.data.database.dao

import io.realm.Realm
import io.realm.RealmModel

abstract class BaseDao {

    // MARK: - Inline Functions
    inline fun <reified T> insertOrUpdateEntity(item: T) where T: RealmModel {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(item)
        }
    }

    inline fun <reified T> insertOrUpdateEntities(items: List<T>) where T: RealmModel {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(items)
        }
    }
}