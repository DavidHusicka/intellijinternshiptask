package com.github.davidhusicka.intellijinternshiptask.intentions

import com.github.davidhusicka.intellijinternshiptask.services.FindUsageService
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*

class FindUsageIntention : IntentionAction {
    override fun startInWriteAction() = false

    override fun getFamilyName() = "Find usage of a function within this function"

    override fun getText() = "Find usage of a function within this function"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val element = file?.findElementAt(editor?.caretModel?.offset ?: return false) ?: return false

        return getMethodFromElement(element) != null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val searchedMethodName = Messages.showInputDialog(
            project,
            "Enter the name of the method to search for:",
            "Find Usage",
            Messages.getQuestionIcon()
        ) ?: return

        val element = file?.findElementAt(editor?.caretModel?.offset ?: return) ?: return
        val method = getMethodFromElement(element) ?: return

        val usages = project.service<FindUsageService>().getUsages(method, searchedMethodName)

        for (usage in usages) {
            if (usage.isEmpty()) {
                continue
            }
            val methodNamesWithoutLast = usage.toMutableList()
            val lastMethodName = methodNamesWithoutLast.removeLast()
            for (methodName in methodNamesWithoutLast) {
                print("$methodName -> ")
            }
            println(lastMethodName)
        }
    }

    private fun getMethodFromElement(element: PsiElement): PsiMethod? {

        var parent = element.parent
        while (parent !is PsiMethod) {
            parent = parent.parent ?: return null
        }
        return parent
    }
}