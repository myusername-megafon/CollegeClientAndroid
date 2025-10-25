package com.example.collegeclientandroid

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.collegeclientandroid.network.ApiService
import com.example.collegeclientandroid.network.LoginResponse
import com.example.collegeclientandroid.viewmodel.HomeScreenViewModel
import com.example.collegeclientandroid.viewmodel.ProfileScreenViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.Response

/**
 * Unit тесты для основных компонентов приложения
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockPrefsEditor: SharedPreferences.Editor
    private lateinit var mockApiService: ApiService
    private lateinit var authManager: AuthManager
    private lateinit var homeViewModel: HomeScreenViewModel
    private lateinit var profileViewModel: ProfileScreenViewModel

    @Before
    fun setup() {
        mockContext = mockk<Context>(relaxed = true)
        mockPrefs = mockk<SharedPreferences>(relaxed = true)
        mockPrefsEditor = mockk<SharedPreferences.Editor>(relaxed = true)
        mockApiService = mockk<ApiService>(relaxed = true)

        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs
        every { mockPrefs.edit() } returns mockPrefsEditor
        every { mockPrefsEditor.putInt(any(), any()) } returns mockPrefsEditor
        every { mockPrefsEditor.putString(any(), any()) } returns mockPrefsEditor
        every { mockPrefsEditor.remove(any()) } returns mockPrefsEditor
        every { mockPrefsEditor.apply() } just Runs

        authManager = AuthManager(mockContext)
        homeViewModel = HomeScreenViewModel(mockApiService, authManager)
        profileViewModel = ProfileScreenViewModel(authManager)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    /**
     * Тест 1: Проверка логики авторизации в AuthManager
     */
    @Test
    fun testAuthManager_LoginSuccess() = runTest {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0

        val email = "test@example.com"
        val password = "password123"
        val loginResponse = LoginResponse(
            id = 10,
            fio = "test",
            email = email,
            photo = "",
            group = "22-исп-3"
        )

        coEvery { mockApiService.login(any()) } returns Response.success(loginResponse)

        val result = authManager.logIn(email, password)

        assertTrue("Вход должен быть успешным", result)
        verify { mockPrefsEditor.putInt("id", 10) }
        verify { mockPrefsEditor.putString("fio", "test") }
        verify { mockPrefsEditor.putString("email", email) }
        verify { mockPrefsEditor.putString("photo", any()) }
        verify { mockPrefsEditor.putString("group", "22-исп-3") }
    }
    /**
     * Тест 2: Проверка логики работы с фото в ProfileScreenViewModel
     */
    @Test
    fun testProfileScreenViewModel_GetUserInfo() = runTest {
        val userInfo = UserInfo(
            id = 10,
            fio = "test",
            email = "test@example.com",
            photo = "",
            group = "22-исп-3"
        )

        every { mockPrefs.getInt("id", -1) } returns 10
        every { mockPrefs.getString("fio", "") } returns "test"
        every { mockPrefs.getString("email", "") } returns "test@example.com"
        every { mockPrefs.getString("photo", "") } returns ""
        every { mockPrefs.getString("group", "") } returns "22-исп-3"

        val result = profileViewModel.getUserInfo()

        assertNotNull("Информация о пользователе должна быть получена", result)
        assertEquals("ID должен совпадать", 10, result?.id)
        assertEquals("ФИО должно совпадать", "test", result?.fio)
        assertEquals("Email должен совпадать", "test@example.com", result?.email)
        assertEquals("Фото должно совпадать", "", result?.photo)
        assertEquals("Группа должна совпадать", "22-исп-3", result?.group)
    }
}