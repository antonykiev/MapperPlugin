package org.mapper.generator.mapperplugin.presentation.ui

import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.JBUI
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JLabel


fun <C : JComponent> C.margin(insets: JBInsets): C = apply {
    val border = JBEmptyBorder(insets)
    this.border = BorderFactory.createCompoundBorder(border, this.border)
}

fun <C : JComponent> C.marginLeft(m: Int): C = apply {
    val border = JBEmptyBorder(JBUI.insetsLeft(m))
    this.border = BorderFactory.createCompoundBorder(border, this.border)
}

fun <C : JComponent> C.marginRight(m: Int): C = apply {
    val border = JBEmptyBorder(JBUI.insetsRight(m))
    this.border = BorderFactory.createCompoundBorder(border, this.border)
}

fun <C : JComponent> C.marginTop(m: Int): C = apply {
    val border = JBEmptyBorder(JBUI.insetsTop(m))
    this.border = BorderFactory.createCompoundBorder(border, this.border)
}

fun <C : JComponent> C.marginBottom(m: Int): C = apply {
    val border = JBEmptyBorder(JBUI.insetsBottom(m))
    this.border = BorderFactory.createCompoundBorder(border, this.border)
}

fun JLabel.smallFont(): JLabel = apply {
    font = Font("Inter", Font.PLAIN, 12)
}