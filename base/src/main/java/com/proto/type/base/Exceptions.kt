package com.proto.type.base

import java.io.IOException

sealed class ChatQException : Exception()

class NoNetworkException : IOException()

class UserExistedException: ChatQException()

class WrongUserNamePasswordException: ChatQException()

class UnknownException: ChatQException()