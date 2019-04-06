package com.lms.suite;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({"com.lms.dao.test","com.lms.service.test", "com.lms.menu.test"})
public class AllJunitTestSuite {
}
