package com.proto.type.base.data.database.mapper

import com.proto.type.base.data.database.entity.UserEntity
import com.proto.type.base.data.mapper.UserMapper
import com.proto.type.base.data.model.UserModel
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserMapperTest {

    private val mockDefaultEntity = UserEntity(
        id = "123",
        bio = "Mock bio",
        last_name = "Last name",
        first_name = "First name",
        username = "UserModel name",
        local_name = "Local name",
        email = "abc@mail.com",
        is_default_user = true)

    private val mockDefaultModel = UserModel(
        id = "123",
        bio = "Mock bio",
        last_name = "Last name",
        first_name = "First name",
        username = "UserModel name",
        local_name = "Local name",
        email = "abc@mail.com",
        is_default_user = true
    )

    @Test
    fun entityToModel() {
        val userModel = UserMapper.toModel(mockDefaultEntity)
        assertNotNull(userModel)
        assertEquals("123", userModel.id)
        assertEquals("Mock bio", userModel.bio)
        assertEquals("Last name", userModel.last_name)
        assertEquals("First name", userModel.first_name)
        assertEquals("UserModel name", userModel.username)
        assertEquals("abc@mail.com", userModel.email)
        assertTrue(userModel.is_default_user)
    }

    @Test
    fun modelToEntity() {
        val userEntity = UserMapper.toEntity(mockDefaultModel)
        assertNotNull(userEntity)
        assertEquals("123", userEntity.id)
        assertEquals("Mock bio", userEntity.bio)
        assertEquals("Last name", userEntity.last_name)
        assertEquals("First name", userEntity.first_name)
        assertEquals("UserModel name", userEntity.username)
        assertEquals("abc@mail.com", userEntity.email)
        assertTrue(userEntity.is_default_user)
    }
}