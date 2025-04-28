package com.kgJr.posKid.ui

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.kgJr.posKid.api.ApiHandler
import com.kgJr.posKid.api.ApiMethodType
import java.awt.*
import javax.swing.*
import javax.swing.SwingWorker

class MainSidebarView : ToolWindowFactory {
    val methods = arrayOf("GET", "POST", "PUT", "DELETE")

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainPanel = JPanel(BorderLayout())
        val outputTextArea = JTextArea()
        val inputPanel = JPanel()
        val dropdown = JComboBox(methods)
        val urlTextArea = JTextArea()
        val sendButton = JButton("Send")
        val urlScrollPane = JScrollPane(urlTextArea)

        lateinit var bodyTextArea: JTextArea
        lateinit var queryParamsPanel: JPanel
        lateinit var headersPanel: JPanel

        // Side menu bar for CURL Generator
        val sideMenuPanel = JPanel(BorderLayout())
        sideMenuPanel.preferredSize = Dimension(40, 0)
        val curlButton = JButton("</>")
        curlButton.toolTipText = "CURL Generator"
        curlButton.preferredSize = Dimension(40, 40)
        val curlPanel = JPanel(BorderLayout())
        val curlTextArea = JTextArea()
        curlTextArea.isEditable = false
        curlTextArea.lineWrap = true
        curlTextArea.wrapStyleWord = true
        val refreshButton = JButton("Refresh")
        refreshButton.preferredSize = Dimension(80, 25)
        curlPanel.add(JScrollPane(curlTextArea), BorderLayout.CENTER)
        curlPanel.add(refreshButton, BorderLayout.SOUTH)
        curlPanel.isVisible = false
        curlPanel.preferredSize = Dimension(200, 0)

        var isExpanded = false
        curlButton.addActionListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                sideMenuPanel.preferredSize = Dimension(200, 0)
                curlPanel.isVisible = true
                updateCurlText(curlTextArea, dropdown, urlTextArea, queryParamsPanel, headersPanel, bodyTextArea)
            } else {
                sideMenuPanel.preferredSize = Dimension(40, 0)
                curlPanel.isVisible = false
            }
            sideMenuPanel.revalidate()
            sideMenuPanel.repaint()
            mainPanel.revalidate()
            mainPanel.repaint()
        }

        refreshButton.addActionListener {
            if (isExpanded) {
                updateCurlText(curlTextArea, dropdown, urlTextArea, queryParamsPanel, headersPanel, bodyTextArea)
            }
        }

        sideMenuPanel.add(curlButton, BorderLayout.NORTH)
        sideMenuPanel.add(curlPanel, BorderLayout.CENTER)

        mainPanel.alignmentX = Component.LEFT_ALIGNMENT
        mainPanel.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        // Input panel layout
        inputPanel.layout = BoxLayout(inputPanel, BoxLayout.Y_AXIS)
        inputPanel.border = BorderFactory.createEmptyBorder(0, 0, 5, 0)

        dropdown.preferredSize = Dimension(60, 25)
        val dropdownPadding = BorderFactory.createEmptyBorder(2, 2, 2, 2)
        dropdown.border = BorderFactory.createCompoundBorder(dropdown.border, dropdownPadding)

        urlTextArea.lineWrap = true
        urlTextArea.wrapStyleWord = true
        urlScrollPane.preferredSize = Dimension(200, 30)
        urlScrollPane.border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), // Add border to URL input section
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        )

        sendButton.preferredSize = Dimension(60, 25)

        // Tabbed pane for Query Params, Headers, and Body
        val tabbedPane = JTabbedPane()
        queryParamsPanel = createKeyValuePanel()
        headersPanel = createKeyValuePanel()
        bodyTextArea = JTextArea()
        val bodyScrollPane = JScrollPane(bodyTextArea)
        bodyTextArea.lineWrap = true
        bodyTextArea.wrapStyleWord = true
        bodyScrollPane.preferredSize = Dimension(200, 100)
        bodyScrollPane.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)

        tabbedPane.addTab("Query Params", queryParamsPanel)
        tabbedPane.addTab("Headers", headersPanel)
        tabbedPane.addTab("Body", bodyScrollPane)

        sendButton.addActionListener {
            println("Selected Method: ${dropdown.selectedItem}")
            println("Entered URL: ${urlTextArea.text}")
            val url = urlTextArea.text
            val body = bodyTextArea.text
            val queryParams = getKeyValueMap(queryParamsPanel)
            val headers = getKeyValueMap(headersPanel)
            println("Query Params: $queryParams")
            println("Headers: $headers")
            println("Body: $body")

            if (url.isNullOrEmpty()) {
                outputTextArea.text = "Invalid url..."
            } else {
                val finalUrl = buildUrlWithQueryParams(url, queryParams)
                outputTextArea.text = "Loading..." // Show "Loading..."
                // Use SwingWorker for asynchronous API call
                object : SwingWorker<String?, Void>() {
                    override fun doInBackground(): String? {
                        return when (dropdown.selectedItem) {
                            "GET" -> ApiHandler.callRequest(finalUrl, ApiMethodType.GET, "")
                            "POST" -> ApiHandler.callRequest(finalUrl, ApiMethodType.POST, body, headers)
                            "PUT" -> ApiHandler.callRequest(finalUrl, ApiMethodType.PUT, body, headers)
                            "DELETE" -> ApiHandler.callRequest(finalUrl, ApiMethodType.DELETE, body, headers)
                            else -> "Something went wrong"
                        }
                    }

                    override fun done() {
                        try {
                            val result = get()
                            outputTextArea.text = if (result?.startsWith("Error:") == true) {
                                result // Display error from ApiHandler
                            } else {
                                toPrettyFormat(result ?: "")
                            }
                        } catch (e: Exception) {
                            outputTextArea.text = "Error: ${e.message}"
                        }
                        if (isExpanded) {
                            updateCurlText(curlTextArea, dropdown, urlTextArea, queryParamsPanel, headersPanel, bodyTextArea)
                        }
                    }
                }.execute()
            }
        }

        // Sub-panel for dropdown, URL, and send button
        val topInputPanel = JPanel()
        topInputPanel.layout = BoxLayout(topInputPanel, BoxLayout.X_AXIS)
        topInputPanel.add(dropdown)
        topInputPanel.add(Box.createHorizontalStrut(5))
        topInputPanel.add(urlScrollPane)
        topInputPanel.add(Box.createHorizontalStrut(5))
        topInputPanel.add(sendButton)

        inputPanel.add(topInputPanel)
        inputPanel.add(Box.createVerticalStrut(5))
        inputPanel.add(tabbedPane)

        mainPanel.add(sideMenuPanel, BorderLayout.WEST)
        mainPanel.add(inputPanel, BorderLayout.NORTH)

        // Output text area configuration
        outputTextArea.lineWrap = true
        outputTextArea.wrapStyleWord = true
        val outputScrollPane = JScrollPane(outputTextArea)
        outputScrollPane.border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1), // Add border to output section
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        )
        mainPanel.add(outputScrollPane, BorderLayout.CENTER)

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createKeyValuePanel(): JPanel {
        val panel = JPanel()
        panel.layout = BorderLayout()
        val entriesPanel = JPanel()
        entriesPanel.layout = BoxLayout(entriesPanel, BoxLayout.Y_AXIS)
        val entries = mutableListOf<JPanel>()

        val addButton = JButton("Add")
        addButton.preferredSize = Dimension(80, 25)
        addButton.alignmentX = Component.LEFT_ALIGNMENT

        addButton.addActionListener {
            val entryPanel = JPanel(GridBagLayout())
            val gbc = GridBagConstraints()
            gbc.insets = Insets(2, 2, 2, 2)

            val keyField = JTextField(10)
            val valueField = JTextField(10)
            val removeButton = JButton("Remove")
            removeButton.preferredSize = Dimension(80, 25)

            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 1.0
            gbc.fill = GridBagConstraints.HORIZONTAL
            entryPanel.add(keyField, gbc)

            gbc.gridx = 1
            gbc.weightx = 1.0
            entryPanel.add(valueField, gbc)

            gbc.gridx = 2
            gbc.weightx = 0.0
            gbc.fill = GridBagConstraints.NONE
            entryPanel.add(removeButton, gbc)

            removeButton.addActionListener {
                entriesPanel.remove(entryPanel)
                entries.remove(entryPanel)
                entriesPanel.revalidate()
                entriesPanel.repaint()
            }

            entries.add(entryPanel)
            entriesPanel.add(entryPanel)
            entriesPanel.revalidate()
            entriesPanel.repaint()
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        buttonPanel.add(addButton)
        panel.add(buttonPanel, BorderLayout.NORTH)
        panel.add(JScrollPane(entriesPanel), BorderLayout.CENTER)

        return panel
    }

    private fun getKeyValueMap(panel: JPanel): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val entriesPanel = (panel.getComponent(1) as JScrollPane).viewport.view as JPanel
        for (component in entriesPanel.components) {
            if (component is JPanel) {
                val keyField = component.getComponent(0) as JTextField
                val valueField = component.getComponent(1) as JTextField
                val key = keyField.text.trim()
                val value = valueField.text.trim()
                if (key.isNotEmpty()) {
                    map[key] = value
                }
            }
        }
        return map
    }

    private fun buildUrlWithQueryParams(baseUrl: String, queryParams: Map<String, String>): String {
        if (queryParams.isEmpty()) return baseUrl
        val queryString = queryParams.entries.joinToString("&") { "${it.key}=${it.value}" }
        return if (baseUrl.contains("?")) "$baseUrl&$queryString" else "$baseUrl?$queryString"
    }

    private fun generateCurlCommand(method: String, url: String, queryParams: Map<String, String>, headers: Map<String, String>, body: String): String {
        val sb = StringBuilder()
        sb.append("curl -X $method ")

        headers.forEach { (key, value) ->
            sb.append("-H \"$key: $value\" ")
        }

        if ((method == "POST" || method == "PUT" || method == "DELETE") && body.isNotBlank()) {
            val escapedBody = body.replace("\"", "\\\"").replace("\n", "\\n")
            sb.append("-d \"$escapedBody\" ")
        }

        val finalUrl = buildUrlWithQueryParams(url, queryParams)
        sb.append("\"$finalUrl\"")

        return sb.toString()
    }

    private fun updateCurlText(
        curlTextArea: JTextArea,
        dropdown: JComboBox<String>,
        urlTextArea: JTextArea,
        queryParamsPanel: JPanel,
        headersPanel: JPanel,
        bodyTextArea: JTextArea
    ) {
        val method = dropdown.selectedItem as String
        val url = urlTextArea.text
        val queryParams = getKeyValueMap(queryParamsPanel)
        val headers = getKeyValueMap(headersPanel)
        val body = bodyTextArea.text
        curlTextArea.text = generateCurlCommand(method, url, queryParams, headers, body)
    }

    fun toPrettyFormat(jsonString: String): String {
        val jsonElement = JsonParser.parseString(jsonString)
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(jsonElement)
    }
}