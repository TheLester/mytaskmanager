package com.dogar.mytaskmanager.suite;

import com.dogar.mytaskmanager.ChangeAppStatusInstrumentationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Runs all Junit3 and Junit4 Instrumentation tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ChangeAppStatusInstrumentationTest.class})
public class InstrumentationTestSuite {}