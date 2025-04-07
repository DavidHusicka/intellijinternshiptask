package com.github.davidhusicka.intellijinternshiptask

import com.github.davidhusicka.intellijinternshiptask.services.FindUsageService
import com.intellij.openapi.components.service
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod

@TestDataPath("\$CONTENT_ROOT/src/test/testData/usages")
class FindUsageTest : BasePlatformTestCase() {

    fun testGetUsagesNone() {
        val psiFile = myFixture.configureByFile("NoUsage.java")

        val foo = findMethodByName(psiFile, "foo")
        val findUsageService = project.service<FindUsageService>()
        val usages = findUsageService.getUsages(foo, "interestingMethod")

        assertEquals(0, usages.size)
    }

    fun testGetUsagesSingle() {
        val psiFile = myFixture.configureByFile("SingleUsage.java")

        val foo = findMethodByName(psiFile, "foo")
        val findUsageService = project.service<FindUsageService>()
        val usages = findUsageService.getUsages(foo, "interestingMethod")

        assertEquals(1, usages.size)
        assertEquals(listOf("foo", "bar", "baz", "interestingMethod"), usages[0])
    }

    fun testGetUsagesLoop() {
        val psiFile = myFixture.configureByFile("SingleUsageLoop.java")

        val foo = findMethodByName(psiFile, "foo")
        val findUsageService = project.service<FindUsageService>()
        val usages = findUsageService.getUsages(foo, "interestingMethod")

        assertEquals(1, usages.size)
        assertEquals(listOf("foo", "bar", "baz", "interestingMethod"), usages[0])
    }

    fun testGetUsagesMultiple() {
        val psiFile = myFixture.configureByFile("MultipleUsages.java")

        val foo = findMethodByName(psiFile, "foo")
        val findUsageService = project.service<FindUsageService>()
        val usages = findUsageService.getUsages(foo, "interestingMethod")

        assertEquals(2, usages.size)
        assertEquals(listOf("foo", "bar", "baz", "biz", "interestingMethod"), usages[0])
        assertEquals(listOf("foo", "bar", "baz", "interestingMethod"), usages[1])
    }

    private fun findMethodByName(psiFile: PsiFile, methodName: String): PsiMethod {
        return psiFile.children
            .filterIsInstance<PsiClass>()
            .flatMap { it.methods.toList() }
            .first { it.name == methodName }
    }

    override fun getTestDataPath() = "src/test/testData/usages"
}
