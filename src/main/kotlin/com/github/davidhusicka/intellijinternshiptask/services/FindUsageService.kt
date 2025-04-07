package com.github.davidhusicka.intellijinternshiptask.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.davidhusicka.intellijinternshiptask.MyBundle
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.impl.source.PsiMethodImpl
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl

@Service(Service.Level.PROJECT)
class FindUsageService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }


    private fun getMethodFromElement(element: PsiElement): PsiMethod? {

        var parent = element.parent
        while (parent !is PsiMethod) {
            parent = parent.parent ?: return null
        }
        return parent
    }

    fun getUsages(method: PsiMethod, searchedMethodName: String): List<List<String>> {
        val classOf = method.containingClass ?: return listOf()
        val body = method.body ?: return listOf()

        // deep first search using children on the body
        val result = mutableListOf<MutableList<String>>(mutableListOf())
        for (child in body.children) {
            deepFirstSearch(child, classOf, searchedMethodName, result, mutableListOf(method.name))
        }

        return result.filter {
            it.isNotEmpty() && it.last() == searchedMethodName
        }
    }

    private fun deepFirstSearch(current: PsiElement, graph: PsiClass, target: String, path: MutableList<MutableList<String>>, currentlyExploredPath: MutableList<String>) {
        if (current is PsiReferenceExpressionImpl && current.parent is PsiMethodCallExpression) {
            val referenceName = current.referenceName
            if (referenceName != null) {

                val newExploredPath = currentlyExploredPath.toMutableList()
                newExploredPath.add(referenceName)
                if (newExploredPath.distinct().size != currentlyExploredPath.size) {
                    path.add(newExploredPath)

                    for (method in graph.findMethodsByName(referenceName)) {
                        for (child in (method as PsiMethodImpl).children) {
                            deepFirstSearch(child, graph, target, path, newExploredPath.toMutableList())
                        }
                    }
                }
            }
        }

        for (child in current.children) {
            deepFirstSearch(child, graph, target, path, currentlyExploredPath)
        }
    }
}
