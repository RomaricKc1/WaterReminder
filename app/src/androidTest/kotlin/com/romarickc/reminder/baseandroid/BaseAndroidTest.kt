package com.romarickc.reminder.baseandroid

import android.content.Context
import android.util.Log
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import androidx.work.WorkManager
import com.romarickc.reminder.R
import com.romarickc.reminder.commons.Constants
import com.romarickc.reminder.commons.Constants.WORKER_TAG_NAME
import com.romarickc.reminder.domain.repository.WaterIntakeRepository
import com.romarickc.reminder.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.assert

private const val SCROLL_DURATION: Long = 3
private const val MAX_TRIES: Long = 10

class CustomException(
    message: String,
) : Exception(message)

@HiltAndroidTest
class BaseAndroidTest {
    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule? =
        GrantPermissionRule.grant(
            Constants.PERMISSION_TO_RQ,
        )

    @Inject
    lateinit var repository: WaterIntakeRepository

    // navigation
    private lateinit var homeSummary: String
    private lateinit var intakeHist: String
    private lateinit var hydrationTips: String
    private lateinit var setTarget: String
    private lateinit var settings: String
    private lateinit var langSettings: String
    private lateinit var notifSettings: String
    private lateinit var importExport: String
    private lateinit var about: String

    private val baseLazyColumn = "lazyColumn"

    // intake
    private val stepperIncreaseDesc = "increase glass count"
    private val stepperDecreaseDesc = "decrease glass count"
    private val currentGlassCntDesc = "current glass cnt"

    // graph
    private val seeMonthsGraphBtnDesc = "days graph intakes"
    private val seeDaysGraphBtnDesc = "months graph intakes"

    // settings
    private val notifPickerDesc = "sel notif pref"
    private val langPickerDesc = "sel lang pref"
    private val notifValidateBtnDesc = "triggers chg notif"
    private val langValidateBtnDesc = "triggers chg lang"

    // about
    private val sourcesKey = "RomaricKc1"
    private val versionFakeBtn = "versionFakeBtn"

    @Before
    fun setup() {
        permissionRule.apply {
            hiltRule.inject()
        }
    }

    @Before
    fun updateStr() {
        val context: Context = ApplicationProvider.getApplicationContext()
        homeSummary = context.resources.getString(R.string.daily_intake)
        intakeHist = context.resources.getString(R.string.intake_history)
        hydrationTips = context.resources.getString(R.string.hydration_tips)
        setTarget = context.resources.getString(R.string.set_target)
        settings = context.resources.getString(R.string.settings)
        langSettings = context.resources.getString(R.string.lang_settings)
        notifSettings = context.resources.getString(R.string.notif_settings)
        importExport = context.resources.getString(R.string.import_export)
        about = context.resources.getString(R.string.about)
    }

    @Test
    fun checkHome() {
        helperScrollOperation(baseLazyColumn, homeSummary) {}
    }

    @Test
    fun checkAbout() {
        helperScrollOperation(baseLazyColumn, about) {
            composeTestRule.apply {
                onNodeWithText(about).performClick()
                onNodeWithText(sourcesKey)
                    .assertExists()
                onNodeWithContentDescription(versionFakeBtn).assertExists()
            }
        }
    }

    @Test
    fun checkPendingNotif() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val workInfos =
            WorkManager
                .getInstance(context)
                .getWorkInfosByTag(WORKER_TAG_NAME)
                .get()
        Log.i("Tests worker infos", "$workInfos")

        val nextSched = workInfos[0].nextScheduleTimeMillis
        val periodSched: Long = workInfos[0].periodicityInfo?.repeatIntervalMillis ?: 0
        val currentTime = System.currentTimeMillis()
        val tenMinutesMs = 600000

        var currentIntakeCnt = 0
        runTest {
            currentIntakeCnt = repository.getCountTgtThis(currentTime).firstOrNull() ?: 0
        }
        println("current intake cnt $currentIntakeCnt")

        assert(nextSched > (currentTime + periodSched - tenMinutesMs))
        assert(nextSched < (currentTime + periodSched + tenMinutesMs))
    }

    @Test
    fun checkAddRMIntake() {
        val currentTime = System.currentTimeMillis()
        var currentIntakeCnt = 0
        runTest {
            currentIntakeCnt = repository.getCountTgtThis(currentTime).firstOrNull() ?: 0
        }
        println("current intake cnt $currentIntakeCnt")

        helperScrollOperation(baseLazyColumn, homeSummary) {
            composeTestRule.apply {
                onNodeWithText(homeSummary).performClick()
                onNodeWithContentDescription(stepperIncreaseDesc).assertExists()
                onNodeWithContentDescription(stepperDecreaseDesc).assertExists()
                onNodeWithTag(currentGlassCntDesc)
                    .assertExists()
                    .assertTextEquals(currentIntakeCnt.toString())

                // inc 2x
                onNodeWithContentDescription(stepperIncreaseDesc).performClick()
                onNodeWithContentDescription(stepperIncreaseDesc).performClick()

                onNodeWithTag(currentGlassCntDesc)
                    .assertExists()
                    .assertTextEquals((currentIntakeCnt + 2).toString())

                // dec 3x, make sure it's > 1
                onNodeWithContentDescription(stepperDecreaseDesc).performClick()
                onNodeWithContentDescription(stepperDecreaseDesc).performClick()
                onNodeWithContentDescription(stepperDecreaseDesc).performClick()

                if (currentIntakeCnt <= 3) {
                    onNodeWithTag(currentGlassCntDesc).assertExists().assertTextEquals("1")
                }
            }
        }
    }

    @Test
    fun checkSeeDaysGraph() {
        helperScrollOperation(
            baseLazyColumn,
            intakeHist,
        ) {
            composeTestRule.apply {
                onNodeWithText(intakeHist).performClick()
                onNodeWithContentDescription(seeDaysGraphBtnDesc)
                    .assertExists()
                    .performClick()
            }
        }
    }

    @Test
    fun checkSeeMonthsGraph() {
        helperScrollOperation(
            baseLazyColumn,
            intakeHist,
        ) {
            composeTestRule.apply {
                onNodeWithText(intakeHist).performClick()
                onNodeWithContentDescription(seeMonthsGraphBtnDesc)
                    .assertExists()
                    .performClick()
            }
        }
    }

    @Test
    fun checkLangSettings() {
        helperScrollOperation(baseLazyColumn, settings) {
            composeTestRule.apply {
                onNodeWithText(settings).performClick()
                onNodeWithText(langSettings).performClick()
                onNodeWithContentDescription(langPickerDesc).assertExists()
                onNodeWithContentDescription(langValidateBtnDesc).assertExists()
            }
        }
    }

    @Test
    fun checkNotifSettings() {
        val items = listOf("Every hour", "Every 3 hours", "Deactivated")
        helperScrollOperation(baseLazyColumn, settings) {
            composeTestRule.apply {
                onNodeWithText(settings).performClick()
                onNodeWithText(notifSettings).performClick()
                onNodeWithContentDescription(notifPickerDesc).assertExists()
                onNodeWithContentDescription(notifValidateBtnDesc).assertExists()

                // chg it
                onNodeWithContentDescription(notifPickerDesc).performScrollToIndex(0)
                onNodeWithContentDescription(notifValidateBtnDesc).performClick()
                /*onNodeWithContentDescription(pickerDesc)
                    .assertTextEquals(
                        items[0],
                    )
                onNodeWithText(items[2]).performClick()
                onNodeWithContentDescription(pickerValidateBtnDesc).performClick().assertTextEquals(
                    items[2],
                )*/
            }
        }
    }

    @Test
    fun checkImportExport() {
        helperScrollOperation(baseLazyColumn, importExport) {}
    }

    @Test
    fun checkSetTarget() {
        helperScrollOperation(baseLazyColumn, setTarget) {}
    }

    @Test
    fun checkHydrationTips() {
        helperScrollOperation(baseLazyColumn, hydrationTips) {}
    }

    @Test
    fun checkIntakeHist() {
        helperScrollOperation(baseLazyColumn, intakeHist) {}
    }

    fun helperScrollOperation(
        baseColum: String,
        target: String,
        operation: () -> Unit,
    ) {
        val maxTries = MAX_TRIES
        var tryCnt = 0
        var notFound = true

        while (notFound and (tryCnt < maxTries)) {
            tryCnt += 1
            try {
                composeTestRule
                    .onNodeWithText(target)
                    .performScrollTo()
                    .assertExists()
                notFound = false
            } catch (_: AssertionError) {
                composeTestRule
                    .onNodeWithTag(baseColum)
                    .performTouchInput { swipeUp(durationMillis = SCROLL_DURATION) }
            }
        }

        if (!notFound) {
            operation()
            return
        }
        tryCnt = 0
        while (notFound and (tryCnt < maxTries)) {
            tryCnt += 1
            try {
                composeTestRule
                    .onNodeWithText(target)
                    .performScrollTo()
                    .assertExists()
                notFound = false
            } catch (_: AssertionError) {
                composeTestRule
                    .onNodeWithTag(baseColum)
                    .performTouchInput { swipeDown(durationMillis = SCROLL_DURATION) }
            }
        }
        if (notFound) {
            throw CustomException("can't find it: \"$target\"")
        } else {
            operation()
        }
    }
}
