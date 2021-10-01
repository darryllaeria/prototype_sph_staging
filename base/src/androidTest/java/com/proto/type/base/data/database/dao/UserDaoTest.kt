package com.proto.type.base.data.database.dao

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.proto.type.base.data.database.entity.UserEntity
import io.realm.Realm
import io.realm.RealmConfiguration
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var realm: Realm
    private val userDao = UserDao()

    @Before
    fun setup() {
        Realm.init(InstrumentationRegistry.getInstrumentation().context)
        val config = RealmConfiguration.Builder()
            .inMemory()
            .name("test-chatq")
            .build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        println("Cleaning up....")
        realm.close()
    }

    @Test
    fun insertNewUser() {
        val id = "123123"
        val userName = "Steve.P"
        val firstName = "Steve"
        val lastName = "Pham"
        val user = UserEntity(
            id = id,
            username = userName,
            first_name = firstName,
            last_name = lastName
        )

        userDao.insertOrUpdateEntity(user)

        val result = realm.where(UserEntity::class.java)
            .equalTo("id", id)
            .findAll()

        assertTrue("UserModel inserted should be one", result.size == 1)
        assertEquals("UserModel is inserted successfully", user.id, result[0]?.id)
    }
}