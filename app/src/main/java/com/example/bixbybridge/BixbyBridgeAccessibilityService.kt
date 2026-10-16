package com.example.bixbybridge

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BixbyBridgeAccessibilityService : AccessibilityService() {
    companion object {
        var pendingCommand: String? = null
        var isBridgeActive = false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val command = pendingCommand ?: return
        if (!isBridgeActive) return
        val rootNode = rootInActiveWindow ?: return

        val inputNodes = rootNode.findAccessibilityNodeInfosByViewId("com.samsung.android.bixby.agent:id/input_edit_text")
        if (inputNodes.isNotEmpty()) {
            val editText = inputNodes[0]
            val arguments = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, command)
            }
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

            val sendButtons = rootNode.findAccessibilityNodeInfosByViewId("com.samsung.android.bixby.agent:id/send_button")
            if (sendButtons.isNotEmpty()) {
                sendButtons[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                pendingCommand = null
                isBridgeActive = false
            }
        }
    }

    override fun onInterrupt() {}
}
