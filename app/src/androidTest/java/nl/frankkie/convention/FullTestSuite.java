package nl.frankkie.convention;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;

/**
 * Created by fbouwens on 19-11-14.
 */
public class FullTestSuite {

    //Some boilerplate code to set up testing,
    //according to Lesson 4a, 'JUnit testing'.

    public static Test Suite(){
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }

    public FullTestSuite(){
        super();
    }
}
