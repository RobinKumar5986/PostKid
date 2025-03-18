package com.kgJr.posKid.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

class MainSidebarView : ToolWindowFactory {
    val methods = arrayOf("GET", "POST", "PUT", "DELETE")

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainPanel = JPanel(BorderLayout())
        mainPanel.alignmentX = Component.LEFT_ALIGNMENT
        mainPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        val inputPanel = JPanel()
        inputPanel.layout = BoxLayout(inputPanel, BoxLayout.X_AXIS)
        inputPanel.border = BorderFactory.createEmptyBorder(0, 0, 5, 0)

        val dropdown = JComboBox(methods)
        dropdown.preferredSize = Dimension(60, 25)
        val dropdownPadding = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        dropdown.border = BorderFactory.createCompoundBorder(dropdown.border, dropdownPadding)
        dropdown.addActionListener(object : ActionListener {
            override fun actionPerformed(e: ActionEvent) {
                //TODO: implement something if needed in future
            }
        })

        val textArea = JTextArea()
        textArea.lineWrap = true
        textArea.wrapStyleWord = true
        val scrollPane = JScrollPane(textArea)
        scrollPane.preferredSize = Dimension(200, 30)
        scrollPane.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)

        val sendButton = JButton("Send")
        sendButton.preferredSize = Dimension(60, 25)
        sendButton.addActionListener { println("Send clicked") }

        inputPanel.add(dropdown)
        inputPanel.add(Box.createHorizontalStrut(5))
        inputPanel.add(scrollPane)
        inputPanel.add(Box.createHorizontalStrut(5))
        inputPanel.add(sendButton)

        mainPanel.add(inputPanel, BorderLayout.NORTH)

        val outputTextArea = JTextArea()
        outputTextArea.lineWrap = true
        outputTextArea.wrapStyleWord = true
        val outputScrollPane = JScrollPane(outputTextArea)
        outputScrollPane.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        mainPanel.add(outputScrollPane, BorderLayout.CENTER)

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}