package com.proto.type.base

// MARK: - Callbacks
typealias EmptyCallback = () -> Unit
typealias ErrorStringCallback = (String) -> Unit
typealias IDCallback = (String?) -> Unit
typealias IDsCallback = (List<String>) -> Unit
typealias StringCallback = (String) -> Unit
typealias SuccessCallback = (Boolean) -> Unit

// MARK: - Types
typealias JSONMutableMap = MutableMap<String, Any?>
typealias MQTTRecipientMutableMap = MutableMap<String, Pair<MutableList<MQTTRecipient>, ByteArray?>>